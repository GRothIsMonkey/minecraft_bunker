package com.blacksite.bunker.build;

import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.Canvas;
import com.blacksite.bunker.plugin.LiftListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * In-world mechanism self test. It operates every security door's button/lever/pressure plate exactly like the
 * game does (block state + neighbour updates), waits for redstone to react, checks the door opened, releases it
 * and checks it closed again. It also cycles the hidden piston bookcase, the lever-controlled lamps and checks
 * every lift sign has a safe destination. Everything is restored to its original state.
 */
public final class SelfTest implements Runnable {
    public interface Done {
        void done(SelfTest t);
    }

    private final Plugin plugin;
    private final Plan plan;
    private final World world;
    private final Done done;
    private int taskId = -1;
    private final LinkedList<Case> queue = new LinkedList<Case>();
    private Case current;
    private int wait;
    public int passed, failed;
    public final List<String> failures = new ArrayList<String>();
    public final List<String> notes = new ArrayList<String>();
    private Method applyPhysics, getById;
    private Constructor<?> bpCtor;
    private Object nmsWorld;
    public boolean physicsAvailable;

    private abstract class Case {
        final String name;
        int step;

        Case(String name) {
            this.name = name;
        }

        /** performs the step; returns ticks to wait before the next step, or -1 when finished */
        abstract int step(int s);
    }

    public SelfTest(Plugin plugin, Plan plan, World world, String filter, Done done) {
        this.plugin = plugin;
        this.plan = plan;
        this.world = world;
        this.done = done;
        initPhysics();
        buildCases();
        if (filter != null) {
            java.util.Iterator<Case> it = queue.iterator();
            while (it.hasNext()) {
                if (!it.next().name.toLowerCase().contains(filter.toLowerCase())) {
                    it.remove();
                }
            }
        }
    }

    private void initPhysics() {
        try {
            String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            nmsWorld = world.getClass().getMethod("getHandle").invoke(world);
            Class<?> bp = Class.forName("net.minecraft.server." + ver + ".BlockPosition");
            Class<?> blk = Class.forName("net.minecraft.server." + ver + ".Block");
            bpCtor = bp.getConstructor(int.class, int.class, int.class);
            getById = blk.getMethod("getById", int.class);
            applyPhysics = nmsWorld.getClass().getMethod("applyPhysics", bp, blk);
            physicsAvailable = true;
        } catch (Exception e) {
            physicsAvailable = false;
            notes.add("neighbour-update reflection unavailable (" + e.getClass().getSimpleName()
                    + "): mechanism tests skipped");
        }
    }

    private void physics(int x, int y, int z, int blockId) {
        if (!physicsAvailable) {
            return;
        }
        try {
            applyPhysics.invoke(nmsWorld, bpCtor.newInstance(x, y, z), getById.invoke(null, blockId));
        } catch (Exception e) {
            // ignore; the test will fail visibly
        }
    }

    @SuppressWarnings("deprecation")
    private int data(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getData();
    }

    @SuppressWarnings("deprecation")
    private int id(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getTypeId();
    }

    /** Presses (on=true) or releases an activator the way the game does. */
    @SuppressWarnings("deprecation")
    private void actuate(int x, int y, int z, boolean on) {
        Block b = world.getBlockAt(x, y, z);
        int id = b.getTypeId(), d = b.getData();
        if (id == B.STONE_PLATE || id == B.WOOD_PLATE) {
            b.setData((byte) (on ? 1 : 0), true);
            physics(x, y - 1, z, id);
            return;
        }
        b.setData((byte) (on ? (d | 8) : (d & 7)), true);
        int[] att = attached(x, y, z, id, d & 7);
        physics(att[0], att[1], att[2], id);
    }

    static int[] attached(int x, int y, int z, int id, int d) {
        if (id == B.LEVER) {
            switch (d) {
                case 0: case 7: return new int[]{x, y + 1, z};
                case 1: return new int[]{x - 1, y, z};
                case 2: return new int[]{x + 1, y, z};
                case 3: return new int[]{x, y, z - 1};
                case 4: return new int[]{x, y, z + 1};
                default: return new int[]{x, y - 1, z};
            }
        }
        switch (d) {
            case 0: return new int[]{x, y + 1, z};
            case 1: return new int[]{x - 1, y, z};
            case 2: return new int[]{x + 1, y, z};
            case 3: return new int[]{x, y, z - 1};
            case 4: return new int[]{x, y, z + 1};
            default: return new int[]{x, y - 1, z};
        }
    }

    private void buildCases() {
        if (!physicsAvailable) {
            return;
        }
        for (Canvas.Marker m : plan.canvas.markers) {
            final int x = m.x + plan.dx, y = m.y + plan.dy, z = m.z + plan.dz;
            if (m.type.startsWith("irondoor")) {
                final int[] act = findActivator(m);
                if (act == null) {
                    queue.add(new Case("door " + x + " " + y + " " + z) {
                        int step(int s) {
                            fail(name + ": no activator found");
                            return -1;
                        }
                    });
                    continue;
                }
                final int ax = act[0] + plan.dx, ay = act[1] + plan.dy, az = act[2] + plan.dz;
                queue.add(new Case("door " + x + " " + y + " " + z + " (" + m.note + ")") {
                    int step(int s) {
                        switch (s) {
                            case 0:
                                if ((data(x, y, z) & 4) != 0) {
                                    notes.add(name + " was already open");
                                }
                                actuate(ax, ay, az, true);
                                return 3;
                            case 1:
                                boolean open = id(x, y, z) == B.IRON_DOOR && (data(x, y, z) & 4) != 0;
                                actuate(ax, ay, az, false);
                                if (!open) {
                                    fail(name + ": did not open");
                                    return -2;
                                }
                                return 3;
                            default:
                                boolean closed = (data(x, y, z) & 4) == 0;
                                if (closed) {
                                    pass();
                                } else {
                                    fail(name + ": did not close again");
                                }
                                return -1;
                        }
                    }
                });
            } else if (m.type.equals("pistondoor")) {
                String[] p = m.note.substring(m.note.indexOf(':') + 1).split(",");
                final int lx = Integer.parseInt(p[0]) + plan.dx, ly = Integer.parseInt(p[1]) + plan.dy,
                        lz = Integer.parseInt(p[2]) + plan.dz;
                queue.add(new Case("piston bookcase " + x + " " + y + " " + z) {
                    boolean opened;

                    int step(int s) {
                        switch (s) {
                            case 0:
                                if (id(x, y, z) != B.BOOKSHELF || id(x, y + 1, z) != B.BOOKSHELF) {
                                    fail(name + ": not closed before test");
                                    return -1;
                                }
                                actuate(lx, ly, lz, false);
                                return 8;
                            case 1:
                                opened = id(x, y, z) == 0 && id(x, y + 1, z) == 0;
                                actuate(lx, ly, lz, true);
                                if (!opened) {
                                    fail(name + ": did not open (found " + id(x, y, z) + "/" + id(x, y + 1, z) + ")");
                                }
                                return 8;
                            default:
                                boolean closed = id(x, y, z) == B.BOOKSHELF && id(x, y + 1, z) == B.BOOKSHELF;
                                if (opened && closed) {
                                    pass();
                                } else if (!closed) {
                                    fail(name + ": did not close again");
                                }
                                return -1;
                        }
                    }
                });
            } else if (m.type.equals("lighttoggle") || m.type.equals("scram")) {
                queue.add(new Case(m.type + " lever " + x + " " + y + " " + z) {
                    int before;

                    int step(int s) {
                        switch (s) {
                            case 0:
                                before = countLitNear(x, y, z);
                                boolean on = (data(x, y, z) & 8) != 0;
                                actuate(x, y, z, !on);
                                return 6;
                            case 1:
                                int after = countLitNear(x, y, z);
                                boolean on2 = (data(x, y, z) & 8) != 0;
                                actuate(x, y, z, !on2);
                                if (after == before) {
                                    fail(name + ": lamps did not react (" + before + " lit before and after)");
                                    return 6;
                                }
                                notes.add(name + ": lit lamps " + before + " -> " + after);
                                return 6;
                            default:
                                if (countLitNear(x, y, z) == before) {
                                    pass();
                                } else {
                                    fail(name + ": lamps not restored");
                                }
                                return -1;
                        }
                    }
                });
            } else if (m.type.equals("smelter")) {
                queue.add(new Case("super smelter (real items through the hopper chain)") {
                    final java.util.Map<String, ItemStack[]> snap = new java.util.HashMap<String, ItemStack[]>();

                    Inventory inv(int bx, int by, int bz) {
                        BlockState st = world.getBlockAt(bx, by, bz).getState();
                        if (st instanceof Chest) {
                            return ((Chest) st).getBlockInventory();
                        }
                        return st instanceof InventoryHolder ? ((InventoryHolder) st).getInventory() : null;
                    }

                    List<int[]> cells() {
                        List<int[]> l = new ArrayList<int[]>();
                        for (int i = -1; i <= 12; i++) {
                            for (int dy = -1; dy <= 3; dy++) {
                                for (int dz = -1; dz <= 1; dz++) {
                                    l.add(new int[]{x + i, y + dy, z + dz});
                                }
                            }
                        }
                        return l;
                    }

                    int step(int s) {
                        if (s == 0) {
                            for (int[] c : cells()) {
                                Inventory v = inv(c[0], c[1], c[2]);
                                if (v != null) {
                                    snap.put(c[0] + "," + c[1] + "," + c[2], v.getContents().clone());
                                }
                            }
                            for (int pair = 0; pair < 3; pair++) {
                                Inventory ore = inv(x + pair * 2, y + 2, z), fuel = inv(x + pair * 2, y + 1, z - 1);
                                if (ore == null || fuel == null) {
                                    fail(name + ": loading chests missing");
                                    return -1;
                                }
                                ore.addItem(new ItemStack(Material.IRON_ORE, 8));
                                fuel.addItem(new ItemStack(Material.COAL, 8));
                            }
                            return 440;
                        }
                        int ingots = 0, used = 0;
                        for (int i = 0; i < 12; i++) {
                            Inventory f = inv(x + i, y, z);
                            if (f instanceof FurnaceInventory) {
                                FurnaceInventory fi = (FurnaceInventory) f;
                                if (fi.getSmelting() != null || fi.getResult() != null) {
                                    used++;
                                }
                                if (fi.getResult() != null && fi.getResult().getType() == Material.IRON_INGOT) {
                                    ingots += fi.getResult().getAmount();
                                }
                            }
                        }
                        for (int dz = 0; dz <= 1; dz++) {
                            Inventory out = inv(x + 12, y - 1, z + dz);
                            if (out != null) {
                                for (ItemStack it : out.getContents()) {
                                    if (it != null && it.getType() == Material.IRON_INGOT) {
                                        ingots += it.getAmount();
                                    }
                                }
                            }
                        }
                        int outIngots = 0;
                        for (int dz = 0; dz <= 1; dz++) {
                            Inventory out = inv(x + 12, y - 1, z + dz);
                            if (out != null) {
                                for (ItemStack it : out.getContents()) {
                                    if (it != null && it.getType() == Material.IRON_INGOT) {
                                        outIngots += it.getAmount();
                                    }
                                }
                            }
                        }
                        notes.add(name + ": " + used + " furnaces working, " + ingots + " ingots smelted, " + outIngots
                                + " already delivered to the output chest");
                        // restore every container exactly
                        for (java.util.Map.Entry<String, ItemStack[]> e : snap.entrySet()) {
                            String[] p = e.getKey().split(",");
                            Inventory v = inv(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                            if (v != null) {
                                v.setContents(e.getValue());
                            }
                        }
                        if (used >= 3 && outIngots >= 1) {
                            pass();
                        } else {
                            fail(name + ": only " + used + " furnaces received ore, " + outIngots + " ingots delivered");
                        }
                        return -1;
                    }
                });
            } else if (m.type.equals("lift")) {
                queue.add(new Case("lift sign " + m.note) {
                    int step(int s) {
                        String r = LiftListener.describe(world.getBlockAt(x, y, z));
                        if (r.startsWith("OK")) {
                            pass();
                        } else {
                            fail(name + ": " + r);
                        }
                        return -1;
                    }
                });
            }
        }
    }

    private int countLitNear(int x, int y, int z) {
        int n = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (id(x + dx, y + dy, z + dz) == B.LAMP_ON) {
                        n++;
                    }
                }
            }
        }
        return n;
    }

    /** Design-space activator (button, lever or plate) that powers the door marker. */
    private int[] findActivator(Canvas.Marker m) {
        Canvas c = plan.canvas;
        for (int x = m.x - 2; x <= m.x + 2; x++) {
            for (int y = m.y - 1; y <= m.y + 3; y++) {
                for (int z = m.z - 2; z <= m.z + 2; z++) {
                    int b = c.get(x, y, z);
                    if (b < 0) {
                        continue;
                    }
                    int id = B.id(b);
                    if (id == B.STONE_PLATE || id == B.WOOD_PLATE) {
                        if (adjDoor(x, y, z, m)) {
                            return new int[]{x, y, z};
                        }
                    } else if (id == B.STONE_BUTTON || id == B.WOOD_BUTTON || id == B.LEVER) {
                        int[] a = attached(x, y, z, id, B.data(b) & 7);
                        if (adjDoor(x, y, z, m) || adjDoor(a[0], a[1], a[2], m)) {
                            return new int[]{x, y, z};
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean adjDoor(int x, int y, int z, Canvas.Marker m) {
        int d1 = Math.abs(x - m.x) + Math.abs(y - m.y) + Math.abs(z - m.z);
        int d2 = Math.abs(x - m.x) + Math.abs(y - (m.y + 1)) + Math.abs(z - m.z);
        return d1 == 1 || d2 == 1;
    }

    private void pass() {
        passed++;
    }

    private void fail(String s) {
        failed++;
        failures.add(s);
    }

    public int total() {
        return queue.size() + (current != null ? 1 : 0) + passed + failed;
    }

    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L).getTaskId();
    }

    public void cancel() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    @Override
    public void run() {
        if (wait > 0) {
            wait--;
            return;
        }
        if (current == null) {
            current = queue.poll();
            if (current == null) {
                cancel();
                done.done(this);
                return;
            }
            current.step = 0;
        }
        int r;
        try {
            r = current.step(current.step++);
        } catch (RuntimeException e) {
            fail(current.name + ": " + e);
            r = -1;
        }
        if (r == -2) {
            // failed early but still restore state: continue with a final wait then drop
            wait = 3;
            current = null;
        } else if (r < 0) {
            current = null;
        } else {
            wait = r;
        }
    }
}
