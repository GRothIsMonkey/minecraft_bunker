package com.blacksite.bunker.build;

import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.EntitySpec;
import com.blacksite.bunker.design.TileSpec;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Bat;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The staged, tick-budgeted builder. Each server tick it works through the current phase until the configured
 * time budget is used, then yields. Progress (phase + cursor) is persisted so a crash or restart can resume.
 * Every operation is idempotent: blocks that already match are skipped, tile data is overwritten with the exact
 * design contents and entities are replaced rather than duplicated.
 */
public final class BuildJob implements Runnable {
    public interface Listener {
        void message(String msg);

        void finished(BuildJob job);
    }

    private final Plugin plugin;
    private final Logger log;
    public final Plan plan;
    public final World world;
    public final BuildState st;
    private final Listener listener;
    public volatile int budgetMs;

    private Plan.Phase phase;
    private int cursor;
    private int taskId = -1;
    private long lastSave, lastReport;
    private int lastPct = -1;
    private final List<String> errors = new ArrayList<String>();
    private int persistent;
    public final List<String> persistentSamples = new ArrayList<String>();
    /** First-pass repairs: blocks the game changed while the build was running (falling gravel, fluids...). */
    public final List<String> repairSamples = new ArrayList<String>();
    private int relit;
    private long workStart;
    private long workDone;
    private final long workTotal;

    public BuildJob(Plugin plugin, Plan plan, World world, BuildState st, int budgetMs, Listener listener) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
        this.plan = plan;
        this.world = world;
        this.st = st;
        this.budgetMs = budgetMs;
        this.listener = listener;
        Plan.Phase p;
        try {
            p = Plan.Phase.valueOf(st.phase);
        } catch (IllegalArgumentException e) {
            p = Plan.Phase.CHUNKS;
        }
        this.phase = p;
        this.cursor = st.cursor;
        this.workTotal = plan.chunkX.length + (long) plan.totalBlocks * 3 + plan.tiles.size()
                + plan.canvas.entities.size() + 10;
        this.workDone = doneBefore(phase) + cursor;
    }

    private long doneBefore(Plan.Phase p) {
        long d = 0;
        for (Plan.Phase q : Plan.Phase.values()) {
            if (q == p) {
                break;
            }
            d += size(q) * (q == Plan.Phase.REPAIR ? 2 : 1);
        }
        if (p == Plan.Phase.REPAIR && st.repairPass > 0) {
            d += plan.totalBlocks;
        }
        return d;
    }

    private long size(Plan.Phase p) {
        switch (p) {
            case CHUNKS: return plan.chunkX.length;
            case TILES: return plan.tiles.size();
            case ENTITIES: return plan.canvas.entities.size();
            case REPAIR: return plan.totalBlocks;
            case CLEANUP: return 1;
            case DONE: return 0;
            default: return plan.blocksOf(p).length;
        }
    }

    public void start() {
        st.status = BuildState.Status.RUNNING;
        st.cleanStop = false; // until onDisable records a normal shutdown
        if (st.startedAt == 0) {
            st.startedAt = System.currentTimeMillis();
        }
        st.save();
        workStart = System.currentTimeMillis();
        doneAtStart = workDone;
        lastReport = System.currentTimeMillis();
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L).getTaskId();
    }

    public void stop(BuildState.Status newStatus) {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        saveProgress();
        st.status = newStatus;
        st.save();
    }

    public boolean isRunning() {
        return taskId != -1;
    }

    public Plan.Phase phase() {
        return phase;
    }

    public int cursor() {
        return cursor;
    }

    public double progress() {
        return Math.min(1.0, workDone / (double) workTotal);
    }

    public String eta() {
        long el = System.currentTimeMillis() - workStart;
        double done = workDone - doneAtStart;
        if (el < 3000 || done <= 0) {
            return "estimating...";
        }
        double rate = done / el;
        long rem = (long) ((workTotal - workDone) / rate / 1000);
        return rem < 60 ? rem + "s" : (rem / 60) + "m " + (rem % 60) + "s";
    }

    private long doneAtStart = 0;

    private void saveProgress() {
        st.phase = phase.name();
        st.cursor = cursor;
        st.save();
        lastSave = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long end = System.nanoTime() + budgetMs * 1000000L;
        int n = 0;
        try {
            while (true) {
                if ((++n & 31) == 0 && System.nanoTime() > end) {
                    break;
                }
                if (!step()) {
                    finish();
                    return;
                }
                if (phase == Plan.Phase.CHUNKS && System.nanoTime() > end) {
                    break;
                }
            }
        } catch (RuntimeException e) {
            log.severe("[SITE-7] build error in phase " + phase + " at " + cursor + ": " + e);
            e.printStackTrace();
            stop(BuildState.Status.PAUSED);
            listener.message("§cBuild paused after an error in phase " + phase.title + ": " + e
                    + ". Fix the cause and run 'bunker resume'.");
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastSave > 5000) {
            saveProgress();
        }
        int pct = (int) (progress() * 100);
        if (now - lastReport > 20000 || pct / 10 != lastPct / 10) {
            lastReport = now;
            lastPct = pct;
            listener.message("§7[SITE-7] " + pct + "% - " + phase.title + " (" + cursor + "/" + size(phase)
                    + "), ETA " + eta());
        }
    }

    /** Performs one unit of work. Returns false when everything is done. */
    private boolean step() {
        switch (phase) {
            case CHUNKS: {
                if (cursor >= plan.chunkX.length) {
                    return next();
                }
                world.loadChunk(plan.chunkX[cursor], plan.chunkZ[cursor], true);
                cursor++;
                workDone++;
                return true;
            }
            case STRUCTURE:
            case LIGHTS:
            case FLUIDS:
            case FIXTURES:
            case MECHANISMS: {
                int[] arr = plan.blocksOf(phase);
                if (cursor >= arr.length) {
                    return next();
                }
                int idx = arr[cursor++];
                workDone++;
                int x = plan.wx(idx), y = plan.wy(idx), z = plan.wz(idx);
                ensureLoaded(x, z);
                int want = plan.block(idx);
                if (Placer.place(world, x, y, z, want)) {
                    st.placed++;
                } else if (phase == Plan.Phase.LIGHTS && Placer.staleLight(world, x, y, z, want)) {
                    Placer.reseat(world, x, y, z, want);
                    st.repaired++;
                    relit++;
                } else {
                    st.skipped++;
                }
                return true;
            }
            case TILES: {
                if (cursor >= plan.tiles.size()) {
                    return next();
                }
                TileSpec t = plan.tiles.get(cursor++);
                workDone++;
                ensureLoaded(t.x + plan.dx, t.z + plan.dz);
                String err = Placer.applyTile(world, t, plan.dx, plan.dy, plan.dz);
                if (err == null) {
                    st.tiles++;
                } else {
                    st.tileErrors++;
                    note("tile " + (t.x + plan.dx) + "," + (t.y + plan.dy) + "," + (t.z + plan.dz) + ": " + err);
                }
                return true;
            }
            case ENTITIES: {
                List<EntitySpec> ents = plan.canvas.entities;
                if (cursor >= ents.size()) {
                    return next();
                }
                EntitySpec e = ents.get(cursor++);
                workDone++;
                ensureLoaded(e.x + plan.dx, e.z + plan.dz);
                String err = Placer.spawn(world, e, plan.dx, plan.dy, plan.dz);
                if (err == null) {
                    st.entities++;
                } else {
                    st.entityErrors++;
                    note("entity " + e.kind + " at " + (e.x + plan.dx) + "," + (e.y + plan.dy) + "," + (e.z + plan.dz)
                            + ": " + err);
                }
                return true;
            }
            case REPAIR: {
                int total = plan.totalBlocks;
                if (cursor >= total) {
                    if (st.repairPass == 0) {
                        st.repairPass = 1;
                        cursor = 0;
                        persistent = 0;
                        return true;
                    }
                    if (persistent > 0) {
                        log.warning("[SITE-7] " + persistent + " blocks keep changing after repair (see report)");
                    }
                    return next();
                }
                int idx = repairIndex(cursor++);
                workDone++;
                int x = plan.wx(idx), y = plan.wy(idx), z = plan.wz(idx);
                ensureLoaded(x, z);
                int want = plan.block(idx);
                int have = Placer.current(world, x, y, z);
                if (!repairOk(want, have)) {
                    Placer.place(world, x, y, z, want);
                    st.repaired++;
                    if (st.repairPass == 0 && repairSamples.size() < 40) {
                        repairSamples.add(x + " " + y + " " + z + " want " + B.id(want) + ":" + B.data(want) + " had "
                                + B.id(have) + ":" + B.data(have));
                    }
                    if (st.repairPass == 1) {
                        persistent++;
                        if (persistentSamples.size() < 40) {
                            persistentSamples.add(x + " " + y + " " + z + " want " + B.id(want) + ":" + B.data(want)
                                    + " have " + B.id(have) + ":" + B.data(have));
                        }
                    }
                }
                return true;
            }
            case CLEANUP: {
                cleanup();
                workDone++;
                return next();
            }
            default:
                return false;
        }
    }

    /** Repair index over the concatenation structure, lights, fluids, fixtures, mechanisms. */
    private int repairIndex(int i) {
        int[][] all = {plan.structure, plan.lights, plan.fluids, plan.fixtures, plan.mechanisms};
        for (int[] a : all) {
            if (i < a.length) {
                return a[i];
            }
            i -= a.length;
        }
        return plan.structure[0];
    }

    /** Fresh-build strictness: exact match, except fluids/farm/leaf state bits that the game manages itself. */
    private static boolean repairOk(int want, int have) {
        if (want == have) {
            return true;
        }
        int id = B.id(want);
        switch (id) {
            case B.WATER: case B.WATER_FLOW: case B.LAVA: case B.LAVA_FLOW: case B.FARMLAND: case B.LEAVES:
            case B.LEAVES2: case B.REDSTONE_WIRE: case B.DAYLIGHT_SENSOR:
                return Placer.equivalent(want, have);
            default:
                return false;
        }
    }

    private boolean next() {
        Plan.Phase[] all = Plan.Phase.values();
        phase = all[Math.min(all.length - 1, phase.ordinal() + 1)];
        cursor = 0;
        workDone = doneBefore(phase);
        saveProgress();
        if (phase == Plan.Phase.DONE) {
            return false;
        }
        listener.message("§7[SITE-7] phase: " + phase.title + " (" + size(phase) + " items)");
        return true;
    }

    private void ensureLoaded(int x, int z) {
        int cx = x >> 4, cz = z >> 4;
        if (!world.isChunkLoaded(cx, cz)) {
            world.loadChunk(cx, cz, true);
        }
    }

    private void note(String e) {
        if (errors.size() < 50) {
            errors.add(e);
            log.warning("[SITE-7] " + e);
        }
    }

    public List<String> errors() {
        return errors;
    }

    private void writeReport() {
        java.io.File dir = new java.io.File(plugin.getDataFolder(), "reports");
        dir.mkdirs();
        StringBuilder sb = new StringBuilder();
        sb.append("SITE-7 build report\n").append(new java.util.Date()).append("\n\n");
        sb.append("World: ").append(world.getName()).append("  bounds: ").append(plan.boundsString()).append('\n');
        sb.append("Duration: ").append((st.finishedAt - st.startedAt) / 1000).append(" s\n");
        sb.append("Blocks placed: ").append(st.placed).append(", already correct: ").append(st.skipped)
                .append(", repaired: ").append(st.repaired).append('\n');
        sb.append("Tile entities: ").append(st.tiles).append(" (errors ").append(st.tileErrors).append(")\n");
        sb.append("Entities: ").append(st.entities).append(" (errors ").append(st.entityErrors).append(")\n");
        sb.append("Light sources re-seated to refresh lighting: ").append(relit).append('\n');
        sb.append("\nBlocks fixed by the first repair pass (sample; the game changed them during the build):\n");
        for (String s : repairSamples) {
            sb.append("  ").append(s).append('\n');
        }
        sb.append("\nBlocks the world changed again after the repair pass:\n");
        for (String s : persistentSamples) {
            sb.append("  ").append(s).append('\n');
        }
        sb.append("\nErrors:\n");
        for (String s : errors) {
            sb.append("  ").append(s).append('\n');
        }
        try {
            java.io.Writer w = new java.io.OutputStreamWriter(new java.io.FileOutputStream(
                    new java.io.File(dir, "build-latest.txt")), java.nio.charset.Charset.forName("UTF-8"));
            w.write(sb.toString());
            w.close();
        } catch (java.io.IOException e) {
            log.warning("could not write build report: " + e);
        }
    }

    private void cleanup() {
        int items = 0, mobs = 0, spawners = 0;
        boolean killSpawners = plugin.getConfig().getBoolean("remove-spawners-in-bounds", true);
        for (int i = 0; i < plan.chunkX.length; i++) {
            if (!world.isChunkLoaded(plan.chunkX[i], plan.chunkZ[i])) {
                continue;
            }
            Chunk ch = world.getChunkAt(plan.chunkX[i], plan.chunkZ[i]);
            for (Entity e : ch.getEntities()) {
                if (!plan.contains(e.getLocation().getBlockX(), e.getLocation().getBlockY(), e.getLocation().getBlockZ())) {
                    continue;
                }
                if (e instanceof Item || e instanceof ExperienceOrb || e instanceof FallingBlock) {
                    e.remove();
                    items++;
                } else if ((e instanceof Monster || e instanceof Slime || e instanceof Bat)
                        && e.getLocation().getBlockY() < plan.minY + 62) {
                    e.remove();
                    mobs++;
                }
            }
            if (killSpawners) {
                for (BlockState bs : ch.getTileEntities()) {
                    if (bs instanceof CreatureSpawner && plan.contains(bs.getX(), bs.getY(), bs.getZ())) {
                        bs.getBlock().setType(Material.STONE);
                        spawners++;
                    }
                }
            }
        }
        listener.message("§7[SITE-7] cleanup: removed " + items + " dropped items, " + mobs
                + " stray underground mobs, " + spawners + " mob spawners in bounds");
    }

    private void finish() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        st.phase = Plan.Phase.DONE.name();
        st.cursor = 0;
        st.status = BuildState.Status.COMPLETE;
        st.finishedAt = System.currentTimeMillis();
        st.save();
        writeReport();
        listener.finished(this);
    }
}
