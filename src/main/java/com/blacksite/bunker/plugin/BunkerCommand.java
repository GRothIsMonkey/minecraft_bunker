package com.blacksite.bunker.plugin;

import com.blacksite.bunker.build.BuildJob;
import com.blacksite.bunker.build.BuildState;
import com.blacksite.bunker.build.Plan;
import com.blacksite.bunker.build.VerifyJob;
import com.blacksite.bunker.design.BunkerDesign;
import com.blacksite.bunker.design.Layout;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

/** /bunker (aliases /blacksite, /bsb, /site7). Works from the console without a slash. */
public final class BunkerCommand implements CommandExecutor {
    private final BlacksiteBunkerPlugin plugin;
    private String pendingSender;
    private long pendingUntil;

    private static final ChatColor H = ChatColor.DARK_AQUA, T = ChatColor.GRAY, V = ChatColor.WHITE;

    public BunkerCommand(BlacksiteBunkerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        String sub = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("tp")) {
            return tp(s, args);
        }
        if (!s.hasPermission("blacksitebunker.admin")) {
            s.sendMessage(ChatColor.RED + "You need blacksitebunker.admin (op) to manage SITE-7.");
            return true;
        }
        switch (sub) {
            case "build": build(s); break;
            case "confirm": confirm(s); break;
            case "cancel": cancel(s); break;
            case "status": status(s); break;
            case "pause": pause(s); break;
            case "resume": resume(s); break;
            case "verify": plugin.startVerify(s); break;
            case "selftest": plugin.startSelfTest(s, args.length > 1 ? args[1] : null); break;
            case "info": info(s); break;
            case "speed": speed(s, args); break;
            case "probe": probe(s, args); break;
            case "report":
                s.sendMessage(T + "Last verification: " + V + (plugin.state().lastVerify.isEmpty() ? "never"
                        : plugin.state().lastVerify));
                s.sendMessage(T + "Reports: plugins/BlacksiteBunker/reports/verify-latest.txt");
                break;
            default: help(s, label); break;
        }
        return true;
    }

    private void help(CommandSender s, String label) {
        String p = s instanceof Player ? "/" : "";
        s.sendMessage(H + "=== SITE-7 Blacksite Bunker (" + BunkerDesign.DESIGN_VERSION + ") ===");
        s.sendMessage(V + p + "bunker build" + T + "   - show the build area and ask for confirmation");
        s.sendMessage(V + p + "bunker confirm" + T + " - start building (safe batches over ticks)");
        s.sendMessage(V + p + "bunker status" + T + "  - progress, phase, ETA");
        s.sendMessage(V + p + "bunker pause" + T + " / " + V + "resume" + T + " - pause or continue (also after restart)");
        s.sendMessage(V + p + "bunker verify" + T + "  - check every block, sign, chest, entity, light level");
        s.sendMessage(V + p + "bunker selftest" + T + " - operate all doors, levers, piston door, lifts");
        s.sendMessage(V + p + "bunker info" + T + "    - entrance, facing, bounds, levels");
        s.sendMessage(V + p + "bunker tp <entrance|l1..l6|command|reactor|hangar|storage>" + T + " - jump there");
        s.sendMessage(V + p + "bunker speed <ms>" + T + " - builder time per tick (now " + plugin.budget() + " ms)");
        s.sendMessage(T + "State: " + V + plugin.state().status + (plugin.job() != null && plugin.job().isRunning()
                ? T + " (" + (int) (plugin.job().progress() * 100) + "%)" : ""));
    }

    private void info(final CommandSender s) {
        plugin.withPlan(s, new Runnable() {
            public void run() {
                Plan p = plugin.plan();
                s.sendMessage(H + "=== SITE-7 facility data ===");
                s.sendMessage(T + "Main entrance (blast door): " + V + plugin.anchorString() + T + "  facing " + V
                        + "EAST" + T + " (approach from the east, walk west inside)");
                s.sendMessage(T + "World: " + V + plugin.targetWorld().getName());
                s.sendMessage(T + "Modified bounds: " + V + p.boundsString());
                s.sendMessage(T + "Footprint: " + V + (p.maxX - p.minX + 1) + " x " + (p.maxZ - p.minZ + 1)
                        + T + " blocks, depth " + V + (plugin.ay() - p.minY) + T + " below the gate, "
                        + V + p.totalBlocks + T + " planned blocks");
                for (int i = 0; i < 6; i++) {
                    s.sendMessage(T + "Level " + (i + 1) + " (floor y=" + (Layout.LEVELS[i] + p.dy) + "): " + V
                            + Layout.LEVEL_NAMES[i]);
                }
                s.sendMessage(T + "Rooms/areas: " + V + p.canvas.rooms.size() + T + ", chests & containers: " + V
                        + p.tiles.size() + T + " tile data entries, entities: " + V + p.canvas.entities.size());
            }
        });
    }

    private void build(final CommandSender s) {
        BuildJob j = plugin.job();
        if (j != null && j.isRunning()) {
            s.sendMessage(ChatColor.YELLOW + "A build is already running. Use 'bunker status' or 'bunker pause'.");
            return;
        }
        plugin.withPlan(s, new Runnable() {
            public void run() {
                Plan p = plugin.plan();
                World w = plugin.targetWorld();
                s.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "=== WARNING: SITE-7 CONSTRUCTION ===");
                s.sendMessage(T + "This will permanently change the world " + V + w.getName() + T + " inside:");
                s.sendMessage(V + "  " + p.boundsString());
                s.sendMessage(T + "  (" + (p.maxX - p.minX + 1) + " x " + (p.maxY - p.minY + 1) + " x "
                        + (p.maxZ - p.minZ + 1) + ", " + p.totalBlocks + " blocks; only planned cells change)");
                s.sendMessage(T + "Main entrance: " + V + plugin.anchorString() + T + ", facing " + V + "EAST");
                s.sendMessage(T + "Anything built in those cells will be replaced. Players should stand clear.");
                if (plugin.state().status == BuildState.Status.COMPLETE) {
                    s.sendMessage(ChatColor.YELLOW + "SITE-7 is already built here: building again restores the "
                            + "original design (repairs damage, resets chest contents).");
                }
                int secs = plugin.getConfig().getInt("confirm-seconds", 60);
                pendingSender = s.getName();
                pendingUntil = System.currentTimeMillis() + secs * 1000L;
                String p2 = s instanceof Player ? "/" : "";
                s.sendMessage(ChatColor.GOLD + "Type " + ChatColor.BOLD + p2 + "bunker confirm" + ChatColor.GOLD
                        + " within " + secs + " seconds to start. " + p2 + "bunker cancel aborts.");
            }
        });
    }

    private void confirm(CommandSender s) {
        if (pendingSender == null || !pendingSender.equals(s.getName()) || System.currentTimeMillis() > pendingUntil) {
            s.sendMessage(ChatColor.RED + "Nothing to confirm (or it expired). Run 'bunker build' first.");
            return;
        }
        pendingSender = null;
        if (plugin.plan() == null) {
            s.sendMessage(ChatColor.RED + "Plan not ready, run 'bunker build' again.");
            return;
        }
        s.sendMessage(ChatColor.GREEN + "Confirmed. Building SITE-7 in batches of " + plugin.budget()
                + " ms per tick. Progress: 'bunker status'.");
        plugin.startBuild(s, true);
    }

    private void cancel(CommandSender s) {
        if (pendingSender != null) {
            pendingSender = null;
            s.sendMessage(T + "Pending build cancelled. Nothing was changed.");
            return;
        }
        if (plugin.job() != null && plugin.job().isRunning()) {
            plugin.pauseBuild();
            s.sendMessage(ChatColor.YELLOW + "Build paused. 'bunker resume' continues exactly where it stopped.");
            return;
        }
        s.sendMessage(T + "Nothing to cancel.");
    }

    private void status(CommandSender s) {
        BuildState st = plugin.state();
        BuildJob j = plugin.job();
        s.sendMessage(H + "=== SITE-7 status ===");
        s.sendMessage(T + "State: " + V + st.status + T + "  world: " + V + (st.world.isEmpty() ? "-" : st.world));
        if (j != null && j.isRunning()) {
            s.sendMessage(T + "Phase: " + V + j.phase().title + T + " (" + j.cursor() + ")  progress " + V
                    + String.format(Locale.ROOT, "%.1f%%", j.progress() * 100) + T + "  ETA " + V + j.eta());
        } else if (st.status == BuildState.Status.PAUSED || st.status == BuildState.Status.INTERRUPTED) {
            s.sendMessage(T + "Stopped in phase " + V + st.phase + T + " at item " + V + st.cursor + T
                    + " - use 'bunker resume'");
        }
        s.sendMessage(T + "Blocks placed: " + V + st.placed + T + ", already correct: " + V + st.skipped + T
                + ", repaired: " + V + st.repaired);
        s.sendMessage(T + "Tile entities: " + V + st.tiles + (st.tileErrors > 0 ? ChatColor.RED + " (" + st.tileErrors
                + " errors)" : "") + T + ", entities: " + V + st.entities + (st.entityErrors > 0 ? ChatColor.RED + " ("
                + st.entityErrors + " errors)" : ""));
        VerifyJob v = plugin.verifyJob();
        if (v != null && v.isRunning()) {
            s.sendMessage(T + "Verification running: " + V + v.progress());
        }
        s.sendMessage(T + "Last verification: " + V + (st.lastVerify.isEmpty() ? "never" : st.lastVerify));
        s.sendMessage(T + "Tick budget: " + V + plugin.budget() + " ms");
    }

    private void pause(CommandSender s) {
        if (plugin.job() == null || !plugin.job().isRunning()) {
            s.sendMessage(T + "No build is running.");
            return;
        }
        plugin.pauseBuild();
        s.sendMessage(ChatColor.YELLOW + "Build paused at " + plugin.state().phase + " #" + plugin.state().cursor
                + ". Use 'bunker resume'.");
    }

    private void resume(final CommandSender s) {
        final BuildState st = plugin.state();
        if (plugin.job() != null && plugin.job().isRunning()) {
            s.sendMessage(T + "The build is already running.");
            return;
        }
        if (st.status != BuildState.Status.PAUSED && st.status != BuildState.Status.INTERRUPTED) {
            s.sendMessage(T + "Nothing to resume (state " + st.status + "). Use 'bunker build' to (re)build.");
            return;
        }
        if (st.anchorX != plugin.ax() || st.anchorY != plugin.ay() || st.anchorZ != plugin.az()
                || st.loot != plugin.getConfig().getBoolean("furnish-loot", true)) {
            s.sendMessage(ChatColor.RED + "config.yml anchor/loot changed since the build started (was "
                    + st.anchorX + " " + st.anchorY + " " + st.anchorZ + "). Restore it, or run 'bunker build' again.");
            return;
        }
        plugin.withPlan(s, new Runnable() {
            public void run() {
                if (st.uncleanRestart) {
                    s.sendMessage(ChatColor.YELLOW + "The server did not shut down cleanly while SITE-7 was being built,"
                            + " so recent chunk changes may be lost. Re-checking every phase from the start"
                            + " (blocks already in place are skipped quickly).");
                } else {
                    s.sendMessage(ChatColor.GREEN + "Resuming SITE-7 at " + st.phase + " #" + st.cursor
                            + " (everything already placed is skipped).");
                }
                plugin.startBuild(s, false);
            }
        });
    }

    private void speed(CommandSender s, String[] args) {
        if (args.length < 2) {
            s.sendMessage(T + "Current tick budget: " + plugin.budget() + " ms. Usage: bunker speed <2-45>");
            return;
        }
        try {
            int ms = Integer.parseInt(args[1]);
            plugin.setBudget(Math.max(2, Math.min(45, ms)));
            s.sendMessage(T + "Tick budget set to " + V + plugin.budget() + " ms");
        } catch (NumberFormatException e) {
            s.sendMessage(ChatColor.RED + "Not a number: " + args[1]);
        }
    }

    /** Admin diagnostic: bunker probe x y z [sx sy sz] - prints block ids:data and container contents. */
    @SuppressWarnings("deprecation")
    private void probe(CommandSender s, String[] args) {
        if (args.length < 4) {
            s.sendMessage(T + "Usage: bunker probe <x> <y> <z> [sizeX sizeY sizeZ]");
            return;
        }
        int x = Integer.parseInt(args[1]), y = Integer.parseInt(args[2]), z = Integer.parseInt(args[3]);
        int sx = args.length > 4 ? Integer.parseInt(args[4]) : 1, sy = args.length > 5 ? Integer.parseInt(args[5]) : 1;
        int sz = args.length > 6 ? Integer.parseInt(args[6]) : 1;
        World w = plugin.targetWorld();
        for (int yy = y + sy - 1; yy >= y; yy--) {
            for (int zz = z; zz < z + sz; zz++) {
                StringBuilder sb = new StringBuilder("y" + yy + " z" + zz + ":");
                for (int xx = x; xx < x + sx; xx++) {
                    org.bukkit.block.Block b = w.getBlockAt(xx, yy, zz);
                    sb.append(' ').append(b.getTypeId()).append(':').append(b.getData());
                    if (b.getState() instanceof org.bukkit.inventory.InventoryHolder) {
                        int n = 0;
                        for (org.bukkit.inventory.ItemStack it : ((org.bukkit.inventory.InventoryHolder) b.getState())
                                .getInventory().getContents()) {
                            if (it != null) {
                                n += it.getAmount();
                            }
                        }
                        sb.append('[').append(n).append(']');
                    }
                }
                s.sendMessage(sb.toString());
            }
        }
    }

    private boolean tp(CommandSender s, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage("Only players can teleport.");
            return true;
        }
        if (!s.hasPermission("blacksitebunker.tp") && !s.hasPermission("blacksitebunker.admin")) {
            s.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        String where = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "entrance";
        double[] d;
        float yaw = 90f;
        switch (where) {
            case "l1": d = new double[]{-367.5, Layout.L1 + 1.6, 142.5}; break;
            case "l2": d = new double[]{-367.5, Layout.L2 + 1.6, 142.5}; break;
            case "l3": d = new double[]{-367.5, Layout.L3 + 1.6, 142.5}; break;
            case "l4": d = new double[]{-367.5, Layout.L4 + 1.6, 142.5}; break;
            case "l5": d = new double[]{-367.5, Layout.L5 + 1.6, 142.5}; break;
            case "l6": d = new double[]{-367.5, Layout.L6 + 1.6, 142.5}; break;
            case "command": d = new double[]{-329.5, Layout.L1 + 1.6, 113.5}; yaw = 180f; break;
            case "reactor": d = new double[]{-329.5, Layout.L5 + 1.6, 118.5}; yaw = 180f; break;
            case "hangar": d = new double[]{-319.5, Layout.L4 + 1.6, 160.5}; break;
            case "storage": d = new double[]{-401.5, Layout.L4 + 1.6, 147.5}; yaw = 0f; break;
            default: d = new double[]{-281.5, Layout.SURF + 1.0, 141.5}; break;
        }
        World w = plugin.targetWorld();
        int dx = plugin.ax() - Layout.EX, dy = plugin.ay() - Layout.EY, dz = plugin.az() - Layout.EZ;
        ((Player) s).teleport(new Location(w, d[0] + dx, d[1] + dy, d[2] + dz, yaw, 0f));
        s.sendMessage(T + "Welcome to SITE-7: " + V + where);
        return true;
    }
}
