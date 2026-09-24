package com.blacksite.bunker.plugin;

import com.blacksite.bunker.build.BuildJob;
import com.blacksite.bunker.build.BuildState;
import com.blacksite.bunker.build.Plan;
import com.blacksite.bunker.build.SelfTest;
import com.blacksite.bunker.build.VerifyJob;
import com.blacksite.bunker.design.BunkerDesign;
import com.blacksite.bunker.design.Layout;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/** Plugin entry point: owns the build/verify/self-test jobs and the persistent state. */
public final class BlacksiteBunkerPlugin extends JavaPlugin implements Listener {
    private BuildState state;
    private BuildJob job;
    private VerifyJob verifyJob;
    private SelfTest selfTest;
    private Plan plan;
    private volatile boolean generating;
    private String notifyName; // player name or null for console only

    @Override
    public void onEnable() {
        saveDefaultConfig();
        state = new BuildState(new File(getDataFolder(), "state.yml"));
        state.load();
        getCommand("bunker").setExecutor(new BunkerCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new LiftListener(this), this);
        getLogger().info("SITE-7 blacksite builder ready. Entrance anchor " + anchorString() + ", facing EAST. "
                + "Use 'bunker build' then 'bunker confirm'.");
        if (state.status == BuildState.Status.INTERRUPTED || state.status == BuildState.Status.PAUSED) {
            getLogger().warning("A SITE-7 build was " + state.status.name().toLowerCase() + " in phase "
                    + state.phase + ". Run 'bunker resume' to continue safely."
                    + (state.uncleanRestart ? " (The server was not shut down cleanly, so resume re-checks"
                    + " every phase from the start.)" : ""));
        }
    }

    @Override
    public void onDisable() {
        if (job != null && job.isRunning()) {
            job.stop(BuildState.Status.INTERRUPTED);
            getLogger().warning("SITE-7 build interrupted by shutdown; 'bunker resume' will continue it.");
        }
        if (!state.uncleanRestart && !state.cleanStop) {
            // normal shutdown: the server saves every chunk after plugins are disabled
            state.cleanStop = true;
            state.save();
        }
        if (verifyJob != null) {
            verifyJob.cancel();
        }
        if (selfTest != null) {
            selfTest.cancel();
        }
    }

    // ---------------------------------------------------------------------------------------------
    public BuildState state() {
        return state;
    }

    public BuildJob job() {
        return job;
    }

    public VerifyJob verifyJob() {
        return verifyJob;
    }

    public boolean isGenerating() {
        return generating;
    }

    public int ax() {
        return getConfig().getInt("anchor.x", Layout.EX);
    }

    public int ay() {
        return getConfig().getInt("anchor.y", Layout.EY);
    }

    public int az() {
        return getConfig().getInt("anchor.z", Layout.EZ);
    }

    public String anchorString() {
        return ax() + " " + ay() + " " + az();
    }

    public World targetWorld() {
        String name = getConfig().getString("world", "");
        if (state.status != BuildState.Status.NONE && state.world != null && !state.world.isEmpty()) {
            World w = Bukkit.getWorld(state.world);
            if (w != null && (name == null || name.isEmpty() || name.equals(state.world))) {
                return w;
            }
        }
        if (name != null && !name.isEmpty()) {
            return Bukkit.getWorld(name);
        }
        return Bukkit.getWorlds().get(0);
    }

    /** Rough bounds (no plan needed) for lift checks and chunk guarding. */
    public boolean insideBunker(Location l) {
        int dx = ax() - Layout.EX, dy = ay() - Layout.EY, dz = az() - Layout.EZ;
        World w = targetWorld();
        if (w == null || !l.getWorld().getName().equals(w.getName())) {
            return false;
        }
        return l.getBlockX() >= Layout.MIN_X + dx && l.getBlockX() <= Layout.MAX_X + dx
                && l.getBlockY() >= Layout.MIN_Y + dy && l.getBlockY() <= Layout.MAX_Y + dy
                && l.getBlockZ() >= Layout.MIN_Z + dz && l.getBlockZ() <= Layout.MAX_Z + dz;
    }

    public int budget() {
        return Math.max(2, Math.min(45, getConfig().getInt("tick-budget-ms", 25)));
    }

    public void setBudget(int ms) {
        getConfig().set("tick-budget-ms", ms);
        saveConfig();
        if (job != null) {
            job.budgetMs = budget();
        }
    }

    // ---------------------------------------------------------------------------------------------
    public void message(String msg) {
        getLogger().info(ChatColor.stripColor(msg));
        if (notifyName != null) {
            Player p = Bukkit.getPlayerExact(notifyName);
            if (p != null) {
                p.sendMessage(msg);
            }
        }
    }

    public void setNotify(CommandSender s) {
        notifyName = (s instanceof Player) ? s.getName() : null;
    }

    /** Generates the plan off the main thread and runs the callback on the main thread. */
    public void withPlan(final CommandSender sender, final Runnable then) {
        final boolean loot = getConfig().getBoolean("furnish-loot", true);
        final int x = ax(), y = ay(), z = az();
        if (plan != null && plan.loot == loot && plan.dx == x - Layout.EX && plan.dy == y - Layout.EY
                && plan.dz == z - Layout.EZ) {
            then.run();
            return;
        }
        if (generating) {
            sender.sendMessage(ChatColor.YELLOW + "The SITE-7 plan is still being generated, please wait...");
            return;
        }
        generating = true;
        sender.sendMessage(ChatColor.GRAY + "Generating the SITE-7 plan (this takes a moment)...");
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            public void run() {
                final Plan p;
                try {
                    p = Plan.generate(x, y, z, loot);
                } catch (final Throwable t) {
                    generating = false;
                    getLogger().severe("Plan generation failed: " + t);
                    t.printStackTrace();
                    return;
                }
                Bukkit.getScheduler().runTask(BlacksiteBunkerPlugin.this, new Runnable() {
                    public void run() {
                        plan = p;
                        generating = false;
                        then.run();
                    }
                });
            }
        });
    }

    public Plan plan() {
        return plan;
    }

    /** Starts (or continues) building from the persisted phase/cursor. */
    public void startBuild(CommandSender sender, boolean fresh) {
        World w = targetWorld();
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "Target world not found. Check 'world' in config.yml.");
            return;
        }
        if (!fresh && state.uncleanRestart) {
            // After a crash the world on disk may be missing blocks placed before the saved cursor (and lamps,
            // frames and paintings placed against them). Re-walk every phase from the start: blocks that are
            // already correct are skipped cheaply, so this costs seconds, not a rebuild.
            state.phase = Plan.Phase.CHUNKS.name();
            state.cursor = 0;
            state.repairPass = 0;
        }
        state.uncleanRestart = false;
        if (fresh) {
            state.status = BuildState.Status.RUNNING;
            state.phase = Plan.Phase.CHUNKS.name();
            state.cursor = 0;
            state.repairPass = 0;
            state.placed = state.skipped = state.repaired = state.tiles = state.entities = 0;
            state.tileErrors = state.entityErrors = 0;
            state.startedAt = System.currentTimeMillis();
            state.finishedAt = 0;
        }
        state.designVersion = BunkerDesign.DESIGN_VERSION;
        state.world = w.getName();
        state.anchorX = ax();
        state.anchorY = ay();
        state.anchorZ = az();
        state.loot = plan.loot;
        setNotify(sender);
        job = new BuildJob(this, plan, w, state, budget(), new BuildJob.Listener() {
            public void message(String msg) {
                BlacksiteBunkerPlugin.this.message(msg);
            }

            public void finished(BuildJob j) {
                long secs = (state.finishedAt - state.startedAt) / 1000;
                BlacksiteBunkerPlugin.this.message(ChatColor.GREEN + "[SITE-7] Build complete in " + secs + " s: "
                        + state.placed + " blocks placed, " + state.skipped + " already correct, " + state.repaired
                        + " repaired, " + state.tiles + " tile entities, " + state.entities + " entities.");
                if (!j.persistentSamples.isEmpty()) {
                    BlacksiteBunkerPlugin.this.message(ChatColor.YELLOW + "[SITE-7] " + j.persistentSamples.size()
                            + " blocks changed again after repair, e.g. " + j.persistentSamples.get(0));
                }
                BlacksiteBunkerPlugin.this.message(ChatColor.GREEN + "[SITE-7] Main gate: " + anchorString()
                        + " (facing east). Try /bunker tp entrance");
                if (getConfig().getBoolean("auto-verify", true)) {
                    startVerify(Bukkit.getConsoleSender());
                }
            }
        });
        job.start();
    }

    public void pauseBuild() {
        if (job != null && job.isRunning()) {
            job.stop(BuildState.Status.PAUSED);
        }
    }

    public void startVerify(final CommandSender sender) {
        if (verifyJob != null && verifyJob.isRunning()) {
            sender.sendMessage(ChatColor.YELLOW + "Verification already running: " + verifyJob.progress());
            return;
        }
        withPlan(sender, new Runnable() {
            public void run() {
                World w = targetWorld();
                sender.sendMessage(ChatColor.GRAY + "[SITE-7] Verifying " + plan.totalBlocks
                        + " planned blocks, tile data, entities, light levels, beacon and lifts...");
                final String who = sender instanceof Player ? sender.getName() : null;
                verifyJob = new VerifyJob(BlacksiteBunkerPlugin.this, plan, w, budget(), new VerifyJob.Done() {
                    public void done(VerifyJob v) {
                        state.lastVerify = (v.passed() ? "PASS " : "ATTENTION ") + new java.util.Date();
                        state.save();
                        for (String s : v.summaryLines()) {
                            report(who, (v.passed() ? ChatColor.GREEN : ChatColor.YELLOW) + "[SITE-7 verify] " + s);
                        }
                        report(who, ChatColor.GRAY + "Full report: " + v.reportFile.getPath());
                    }
                });
                verifyJob.start();
            }
        });
    }

    public void startSelfTest(final CommandSender sender, final String filter) {
        if (selfTest != null) {
            selfTest.cancel();
        }
        withPlan(sender, new Runnable() {
            public void run() {
                final String who = sender instanceof Player ? sender.getName() : null;
                selfTest = new SelfTest(BlacksiteBunkerPlugin.this, plan, targetWorld(), filter, new SelfTest.Done() {
                    public void done(SelfTest t) {
                        report(who, (t.failed == 0 ? ChatColor.GREEN : ChatColor.YELLOW) + "[SITE-7 selftest] "
                                + t.passed + " passed, " + t.failed + " failed");
                        for (String f : t.failures) {
                            report(who, ChatColor.RED + "  FAIL " + f);
                        }
                        for (String n : t.notes) {
                            getLogger().info("[selftest] " + n);
                        }
                    }
                });
                sender.sendMessage(ChatColor.GRAY + "[SITE-7] Self-test: operating " + selfTest.total()
                        + " doors, levers, the piston bookcase and lifts...");
                selfTest.start();
            }
        });
    }

    void report(String playerName, String msg) {
        getLogger().info(ChatColor.stripColor(msg));
        if (playerName != null) {
            Player p = Bukkit.getPlayerExact(playerName);
            if (p != null) {
                p.sendMessage(msg);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    /** Keeps chunks of the construction site loaded while a build or verification runs. */
    @EventHandler(ignoreCancelled = true)
    public void onUnload(ChunkUnloadEvent e) {
        boolean active = (job != null && job.isRunning()) || (verifyJob != null && verifyJob.isRunning());
        if (!active || plan == null) {
            return;
        }
        if (!e.getWorld().getName().equals(state.world)) {
            return;
        }
        if (plan.chunkInBounds(e.getChunk().getX(), e.getChunk().getZ())) {
            e.setCancelled(true);
        }
    }

    public static boolean isConsole(CommandSender s) {
        return s instanceof ConsoleCommandSender;
    }
}
