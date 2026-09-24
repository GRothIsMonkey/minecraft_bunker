package com.blacksite.bunker.build;

import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.Canvas;
import com.blacksite.bunker.design.EntitySpec;
import com.blacksite.bunker.design.Layout;
import com.blacksite.bunker.design.TileSpec;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Verifies the built facility against the design, spread over ticks: every planned block, tile data, entities,
 * real in-game block light on every indoor spawnable spot, the beacon column and the sign lifts.
 */
public final class VerifyJob implements Runnable {
    public interface Done {
        void done(VerifyJob job);
    }

    private final Plugin plugin;
    private final Plan plan;
    private final World world;
    private final Done done;
    private int budgetMs;
    private int taskId = -1;

    private int stage = 0, cursor = 0;
    // results
    public long checked, mismatched;
    public final Map<String, Integer> mismatchByType = new TreeMap<String, Integer>();
    public final List<String> samples = new ArrayList<String>();
    public int signsOk, signsBad, invOk, invBad, entOk, entMissing;
    public int spawnChecked, darkSpots, slimeSpots, railSlimeSpots;
    public final List<String> darkSamples = new ArrayList<String>();
    public boolean beaconOk;
    public String beaconNote = "";
    public int liftsOk, liftsBad;
    public final List<String> liftNotes = new ArrayList<String>();
    private final int[] spawnCells;
    private int[] allCells;
    public long startedAt, finishedAt;
    public File reportFile;

    public VerifyJob(Plugin plugin, Plan plan, World world, int budgetMs, Done done) {
        this.plugin = plugin;
        this.plan = plan;
        this.world = world;
        this.budgetMs = budgetMs;
        this.done = done;
        this.spawnCells = spawnCandidates(plan.canvas);
        int n = plan.totalBlocks;
        allCells = new int[n];
        int k = 0;
        for (int[] a : new int[][]{plan.structure, plan.lights, plan.fluids, plan.fixtures, plan.mechanisms}) {
            System.arraycopy(a, 0, allCells, k, a.length);
            k += a.length;
        }
    }

    /** Indoor cells (design space) where a mob could stand: free feet cell over a solid-topped block. */
    static int[] spawnCandidates(Canvas c) {
        List<Integer> out = new ArrayList<Integer>();
        for (int y = c.minY + 1; y < c.maxY; y++) {
            for (int z = c.minZ; z <= c.maxZ; z++) {
                for (int x = c.minX; x <= c.maxX; x++) {
                    int feet = c.get(x, y, z);
                    if (feet < 0 || B.hasCollision(feet) || B.isLiquid(feet) || B.isOpaqueCube(feet)) {
                        continue;
                    }
                    int below = c.get(x, y - 1, z);
                    if (below < 0 || !B.solidTop(below)) {
                        continue;
                    }
                    int head = c.get(x, y + 1, z);
                    if (head >= 0 && B.isOpaqueCube(head)) {
                        continue;
                    }
                    if (!indoor(c, x, y, z)) {
                        continue;
                    }
                    out.add(c.index(x, y, z));
                }
            }
        }
        int[] r = new int[out.size()];
        for (int i = 0; i < r.length; i++) {
            r[i] = out.get(i);
        }
        return r;
    }

    static boolean indoor(Canvas c, int x, int y, int z) {
        if (y <= 66) {
            return true;
        }
        if (y == Layout.SURF + 1 && ((x >= -291 && x <= -249 && z >= 111 && z <= 171)
                || (x >= -247 && x <= -222 && z >= 133 && z <= 149))) {
            return true; // courtyard pad and approach road (lit for night time)
        }
        for (Canvas.Room r : c.rooms) {
            if (x >= r.x1 && x <= r.x2 && y >= r.y1 && y <= r.y2 && z >= r.z1 && z <= r.z2) {
                return true;
            }
        }
        return false;
    }

    public void start() {
        startedAt = System.currentTimeMillis();
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L).getTaskId();
    }

    public void cancel() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public boolean isRunning() {
        return taskId != -1;
    }

    public String progress() {
        String[] names = {"blocks", "tile data", "entities", "light levels", "beacon & lifts"};
        int total = stage == 0 ? allCells.length : stage == 1 ? plan.tiles.size() : stage == 2
                ? plan.canvas.entities.size() : stage == 3 ? spawnCells.length : 1;
        return names[Math.min(stage, 4)] + " " + cursor + "/" + total;
    }

    @Override
    public void run() {
        long end = System.nanoTime() + budgetMs * 1000000L;
        int n = 0;
        while (((++n & 63) != 0) || System.nanoTime() < end) {
            if (!step()) {
                finish();
                return;
            }
        }
    }

    private void load(int x, int z) {
        if (!world.isChunkLoaded(x >> 4, z >> 4)) {
            world.loadChunk(x >> 4, z >> 4, true);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean step() {
        switch (stage) {
            case 0: {
                if (cursor >= allCells.length) {
                    stage++;
                    cursor = 0;
                    return true;
                }
                int idx = allCells[cursor++];
                int x = plan.wx(idx), y = plan.wy(idx), z = plan.wz(idx);
                load(x, z);
                int want = plan.block(idx);
                int have = Placer.current(world, x, y, z);
                checked++;
                if (!Placer.equivalent(want, have)) {
                    mismatched++;
                    String key = B.id(want) + "->" + B.id(have);
                    Integer v = mismatchByType.get(key);
                    mismatchByType.put(key, v == null ? 1 : v + 1);
                    if (samples.size() < 60) {
                        samples.add(x + " " + y + " " + z + " expected " + B.id(want) + ":" + B.data(want) + " found "
                                + B.id(have) + ":" + B.data(have));
                    }
                }
                return true;
            }
            case 1: {
                if (cursor >= plan.tiles.size()) {
                    stage++;
                    cursor = 0;
                    return true;
                }
                TileSpec t = plan.tiles.get(cursor++);
                int x = t.x + plan.dx, y = t.y + plan.dy, z = t.z + plan.dz;
                load(x, z);
                BlockState st = world.getBlockAt(x, y, z).getState();
                if (t instanceof TileSpec.SignText) {
                    boolean ok = st instanceof Sign;
                    if (ok) {
                        String[] want = ((TileSpec.SignText) t).lines;
                        String[] have = ((Sign) st).getLines();
                        for (int i = 0; i < 4; i++) {
                            if (!want[i].equals(have[i])) {
                                ok = false;
                            }
                        }
                    }
                    if (ok) {
                        signsOk++;
                    } else {
                        signsBad++;
                        if (samples.size() < 80) {
                            samples.add("sign text differs at " + x + " " + y + " " + z);
                        }
                    }
                } else if (t instanceof TileSpec.Inventory) {
                    if (st instanceof InventoryHolder) {
                        invOk++;
                    } else {
                        invBad++;
                        if (samples.size() < 80) {
                            samples.add("missing container at " + x + " " + y + " " + z);
                        }
                    }
                }
                return true;
            }
            case 2: {
                List<EntitySpec> ents = plan.canvas.entities;
                if (cursor >= ents.size()) {
                    stage++;
                    cursor = 0;
                    return true;
                }
                EntitySpec e = ents.get(cursor++);
                load(e.x + plan.dx, e.z + plan.dz);
                if (Placer.present(world, e, plan.dx, plan.dy, plan.dz)) {
                    entOk++;
                } else {
                    entMissing++;
                    if (samples.size() < 90) {
                        samples.add("missing " + e.kind + " at " + (e.x + plan.dx) + " " + (e.y + plan.dy) + " "
                                + (e.z + plan.dz));
                    }
                }
                return true;
            }
            case 3: {
                if (cursor >= spawnCells.length) {
                    stage++;
                    cursor = 0;
                    return true;
                }
                int idx = spawnCells[cursor++];
                int x = plan.wx(idx), y = plan.wy(idx), z = plan.wz(idx);
                load(x, z);
                spawnChecked++;
                Block b = world.getBlockAt(x, y, z);
                int dy = y - plan.dy;
                if (dy < 40) {
                    int feet = plan.block(idx);
                    if (B.id(feet) == B.RAIL || B.id(feet) == B.POWERED_RAIL) {
                        railSlimeSpots++;
                    } else {
                        slimeSpots++;
                        if (darkSamples.size() < 40) {
                            darkSamples.add("slime-capable " + x + " " + y + " " + z);
                        }
                    }
                } else {
                    int light = b.getLightFromBlocks();
                    if (light < 8) {
                        darkSpots++;
                        if (darkSamples.size() < 40) {
                            darkSamples.add("dark (" + light + ") " + x + " " + y + " " + z);
                        }
                    }
                }
                return true;
            }
            case 4: {
                checkBeacon();
                checkLifts();
                stage++;
                return false;
            }
            default:
                return false;
        }
    }

    private void checkBeacon() {
        int x = Layout.CX + plan.dx, z = Layout.CZ + plan.dz;
        int by = -1;
        for (Canvas.Marker m : plan.canvas.markers) {
            if (m.type.equals("beacon")) {
                by = m.y + plan.dy;
            }
        }
        if (by < 0) {
            beaconNote = "no beacon in design";
            return;
        }
        load(x, z);
        Block b = world.getBlockAt(x, by, z);
        if (b.getType() != org.bukkit.Material.BEACON) {
            beaconNote = "beacon block missing";
            return;
        }
        int blocked = -1;
        for (int y = by + 1; y < world.getMaxHeight(); y++) {
            Block a = world.getBlockAt(x, y, z);
            if (B.opacity(B.of(a.getTypeId(), a.getData())) >= 15 && a.getTypeId() != B.BEDROCK) {
                blocked = y;
                break;
            }
        }
        beaconOk = blocked < 0;
        beaconNote = beaconOk ? "beacon at y=" + by + " has a clear light-blue beam column to the sky"
                : "beam blocked at y=" + blocked + " (" + world.getBlockAt(x, blocked, z).getType() + ")";
    }

    private void checkLifts() {
        for (Canvas.Marker m : plan.canvas.markers) {
            if (!m.type.equals("lift")) {
                continue;
            }
            int x = m.x + plan.dx, y = m.y + plan.dy, z = m.z + plan.dz;
            load(x, z);
            Block sign = world.getBlockAt(x, y, z);
            String res = com.blacksite.bunker.plugin.LiftListener.describe(sign);
            if (res.startsWith("OK")) {
                liftsOk++;
            } else {
                liftsBad++;
            }
            liftNotes.add(m.note + ": " + res);
        }
    }

    private void finish() {
        cancel();
        finishedAt = System.currentTimeMillis();
        writeReport();
        done.done(this);
    }

    public boolean passed() {
        return mismatched == 0 && signsBad == 0 && invBad == 0 && entMissing == 0 && darkSpots == 0 && slimeSpots == 0
                && beaconOk && liftsBad == 0;
    }

    public List<String> summaryLines() {
        List<String> l = new ArrayList<String>();
        l.add("Blocks checked: " + checked + ", mismatched: " + mismatched);
        l.add("Signs: " + signsOk + " ok, " + signsBad + " differ | containers: " + invOk + " ok, " + invBad + " missing");
        l.add("Entities: " + entOk + " present, " + entMissing + " missing");
        l.add("Spawn spots checked: " + spawnChecked + " | dark (light<8, y>=40): " + darkSpots
                + " | slime-capable (y<40): " + slimeSpots + " (+" + railSlimeSpots + " on the secret rail track)");
        l.add("Beacon: " + beaconNote);
        l.add("Lifts: " + liftsOk + " ok, " + liftsBad + " broken");
        l.add("RESULT: " + (passed() ? "PASS" : "ATTENTION NEEDED"));
        return l;
    }

    private void writeReport() {
        File dir = new File(plugin.getDataFolder(), "reports");
        dir.mkdirs();
        String ts = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        reportFile = new File(dir, "verify-" + ts + ".txt");
        StringBuilder sb = new StringBuilder();
        sb.append("SITE-7 verification report\n");
        sb.append("Generated: ").append(new Date()).append('\n');
        sb.append("World: ").append(world.getName()).append("  bounds: ").append(plan.boundsString()).append('\n');
        sb.append("Duration: ").append((finishedAt - startedAt) / 1000).append(" s\n\n");
        for (String s : summaryLines()) {
            sb.append(s).append('\n');
        }
        if (!mismatchByType.isEmpty()) {
            sb.append("\nMismatches by type (expected->found):\n");
            for (Map.Entry<String, Integer> e : mismatchByType.entrySet()) {
                sb.append("  ").append(e.getKey()).append(" x").append(e.getValue()).append('\n');
            }
        }
        if (!samples.isEmpty()) {
            sb.append("\nSamples:\n");
            for (String s : samples) {
                sb.append("  ").append(s).append('\n');
            }
        }
        if (!darkSamples.isEmpty()) {
            sb.append("\nSpawn spot samples:\n");
            for (String s : darkSamples) {
                sb.append("  ").append(s).append('\n');
            }
        }
        sb.append("\nLifts:\n");
        for (String s : liftNotes) {
            sb.append("  ").append(s).append('\n');
        }
        try {
            Writer w = new OutputStreamWriter(new FileOutputStream(reportFile), Charset.forName("UTF-8"));
            w.write(sb.toString());
            w.close();
            Writer w2 = new OutputStreamWriter(new FileOutputStream(new File(dir, "verify-latest.txt")),
                    Charset.forName("UTF-8"));
            w2.write(sb.toString());
            w2.close();
        } catch (IOException e) {
            plugin.getLogger().warning("could not write report: " + e);
        }
    }
}
