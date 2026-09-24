package com.blacksite.bunker.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sign lifts: right-click a wall sign whose first line is [Lift Up] or [Lift Down] to travel to the next lift
 * sign above or below in the same column (any sign whose first line is [Lift], [Lift Up] or [Lift Down]).
 * The player keeps their offset relative to the sign, so arriving works like a real elevator stop.
 */
public final class LiftListener implements Listener {
    private final BlacksiteBunkerPlugin plugin;
    private final Map<UUID, Long> cooldown = new HashMap<UUID, Long>();

    public LiftListener(BlacksiteBunkerPlugin plugin) {
        this.plugin = plugin;
    }

    static String tag(Block b) {
        if (b.getType() != Material.WALL_SIGN && b.getType() != Material.SIGN_POST) {
            return null;
        }
        String l = ChatColor.stripColor(((Sign) b.getState()).getLine(0)).trim();
        if (l.equalsIgnoreCase("[Lift Up]")) {
            return "up";
        }
        if (l.equalsIgnoreCase("[Lift Down]")) {
            return "down";
        }
        if (l.equalsIgnoreCase("[Lift]")) {
            return "stop";
        }
        return null;
    }

    /** Finds the next lift sign in the column above/below. */
    static Block findTarget(Block sign, boolean up) {
        int y = sign.getY();
        int max = sign.getWorld().getMaxHeight() - 1;
        for (int dy = 1; dy < 256; dy++) {
            int ny = up ? y + dy : y - dy;
            if (ny < 1 || ny > max) {
                return null;
            }
            Block b = sign.getWorld().getBlockAt(sign.getX(), ny, sign.getZ());
            if (tag(b) != null) {
                return b;
            }
        }
        return null;
    }

    static boolean feetOk(Block b) {
        Material m = b.getType();
        if (m == Material.LAVA || m == Material.STATIONARY_LAVA) {
            return false;
        }
        return !m.isSolid() || m == Material.CARPET || m == Material.STEP || m == Material.WOOD_STEP
                || m == Material.STONE_SLAB2;
    }

    static boolean headOk(Block b) {
        Material m = b.getType();
        return !m.isSolid() && m != Material.LAVA && m != Material.STATIONARY_LAVA;
    }

    /** Destination (same x/z as the player, same height offset to the sign) or null when unsafe. */
    static Location destination(Block sign, double feetY, boolean up, Location from) {
        Block target = findTarget(sign, up);
        if (target == null) {
            return null;
        }
        double ty = target.getY() + (feetY - sign.getY());
        for (int attempt = 0; attempt < 3; attempt++) {
            double y = ty + attempt;
            Block feet = sign.getWorld().getBlockAt(from.getBlockX(), (int) Math.floor(y), from.getBlockZ());
            if (feetOk(feet) && headOk(feet.getRelative(0, 1, 0))) {
                Location l = from.clone();
                l.setY(y);
                return l;
            }
        }
        return null;
    }

    /** Self-test description of a lift sign. */
    public static String describe(Block sign) {
        String t = tag(sign);
        if (t == null) {
            return "no lift sign";
        }
        if (t.equals("stop")) {
            return "OK (arrival sign)";
        }
        Block target = findTarget(sign, t.equals("up"));
        if (target == null) {
            return "BROKEN: no destination " + t;
        }
        // simulate a player standing one block in front of the sign, feet one below the sign
        org.bukkit.block.BlockFace facing = ((org.bukkit.material.Sign) sign.getState().getData()).getFacing();
        Block stand = sign.getRelative(facing).getRelative(0, -1, 0);
        Location from = stand.getLocation().add(0.5, 0, 0.5);
        Location d = destination(sign, stand.getY(), t.equals("up"), from);
        if (d == null) {
            return "BROKEN: destination at y=" + target.getY() + " not safe";
        }
        return "OK -> y=" + target.getY() + " (arrive feet " + d.getY() + ")";
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null) {
            return;
        }
        if (!plugin.getConfig().getBoolean("lifts-enabled", true)) {
            return;
        }
        Block b = e.getClickedBlock();
        String t = tag(b);
        if (t == null || t.equals("stop")) {
            return;
        }
        Player p = e.getPlayer();
        if (!p.hasPermission("blacksitebunker.lift")) {
            return;
        }
        if (plugin.getConfig().getBoolean("lifts-inside-bunker-only", true) && !plugin.insideBunker(b.getLocation())) {
            return;
        }
        long now = System.currentTimeMillis();
        Long last = cooldown.get(p.getUniqueId());
        if (last != null && now - last < 1200) {
            return;
        }
        cooldown.put(p.getUniqueId(), now);
        Location dest = destination(b, p.getLocation().getY(), t.equals("up"), p.getLocation());
        if (dest == null) {
            p.sendMessage(ChatColor.RED + "The lift won't move: no safe stop " + (t.equals("up") ? "above." : "below."));
            return;
        }
        e.setCancelled(true);
        p.teleport(dest);
        try {
            p.playSound(dest, Sound.NOTE_PLING, 0.6f, 1.4f);
        } catch (Throwable ignored) {
            // sound names differ on forks; ignore
        }
        Block target = findTarget(b, t.equals("up"));
        if (target != null && target.getState() instanceof Sign) {
            String l2 = ChatColor.stripColor(((Sign) target.getState()).getLine(1));
            p.sendMessage(ChatColor.GRAY + "[Lift] " + ChatColor.WHITE + (l2.isEmpty() ? "arrived" : l2));
        }
    }
}
