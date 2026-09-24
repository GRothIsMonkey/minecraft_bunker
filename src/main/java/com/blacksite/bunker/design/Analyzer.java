package com.blacksite.bunker.design;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Static analysis of a generated design: block-light propagation, mob spawn checks, player navigation from the
 * entrance to every room, iron door activators, redstone exposure and lava safety. Used by the offline test
 * tools and summarised in the build report.
 */
public final class Analyzer {
    private final Canvas c;
    public byte[] light;

    public final List<String> darkSpawns = new ArrayList<String>();
    public final List<String> slimeSpawns = new ArrayList<String>();
    public final List<String> unreachable = new ArrayList<String>();
    public final List<String> doorProblems = new ArrayList<String>();
    public final List<String> redstoneProblems = new ArrayList<String>();
    public final List<String> lavaProblems = new ArrayList<String>();
    /** Chests touching two chests of the same kind (1.8 "triple chest"), or pairs the game would re-orient. */
    public final List<String> chestProblems = new ArrayList<String>();
    /** Attached/gravity blocks that 1.8 block physics would pop off or drop on the next neighbour update. */
    public final List<String> supportProblems = new ArrayList<String>();
    public int reachableRooms, totalRooms, darkCount, slimeCount;

    public Analyzer(Canvas c) {
        this.c = c;
    }

    public Analyzer runAll() {
        computeLight();
        spawns();
        navigation();
        doors();
        redstone();
        lava();
        chests();
        support();
        return this;
    }

    // ---------------------------------------------------------------------------------------------
    // support: the builder places without physics, so an unsupported ladder, button, rail... survives until
    // the first neighbour update (a door opening, a player placing a block) and then pops off. Rules: 1.8.8.
    // ---------------------------------------------------------------------------------------------
    /** Block.isOccluding(): opaque material, full cube, not a power source (glowstone/sea lantern are glass). */
    private static boolean occluding(int b) {
        int id = B.id(b);
        return B.isOpaqueCube(b) && id != B.GLOWSTONE && id != B.SEA_LANTERN && id != B.REDSTONE_BLOCK
                && id != B.LEAVES && id != B.LEAVES2 && id != B.ICE && id != B.TNT && id != B.SLIME;
    }

    /** World.a(IBlockAccess, pos): solid top surface for rails, doors, dust, plates, pots, torches. */
    private static boolean topSolid(int b) {
        int id = B.id(b);
        if (B.isStairs(b)) {
            return (B.data(b) & 4) != 0;
        }
        if (B.isHalfSlab(b)) {
            return (B.data(b) & 8) != 0;
        }
        if (id == B.HOPPER) {
            return true;
        }
        return B.isOpaqueCube(b) && id != B.GLOWSTONE && id != B.SEA_LANTERN && id != B.LEAVES
                && id != B.LEAVES2 && id != B.ICE && id != B.TNT && id != B.SLIME;
    }

    /** Material.isBuildable(): anything but air, liquids and "decoration" materials. */
    private static boolean buildable(int b) {
        int id = B.id(b);
        if (id == B.AIR || B.isFluid(b)) {
            return false;
        }
        switch (id) {
            case B.FIRE: case B.TORCH: case B.REDSTONE_TORCH: case B.REDSTONE_TORCH_OFF: case B.LEVER:
            case B.STONE_BUTTON: case B.WOOD_BUTTON: case B.RAIL: case B.POWERED_RAIL: case B.DETECTOR_RAIL:
            case B.ACTIVATOR_RAIL: case B.REDSTONE_WIRE: case B.LADDER: case B.REPEATER: case B.COMPARATOR:
            case B.TRIPWIRE_HOOK: case B.TALL_GRASS: case B.DEAD_BUSH: case B.DANDELION: case B.FLOWER:
            case B.BROWN_MUSHROOM: case B.RED_MUSHROOM: case B.WHEAT: case B.CARROTS: case B.POTATOES:
            case B.SUGAR_CANE: case B.VINE: case B.SNOW_LAYER: case B.NETHER_WART: case B.PUMPKIN_STEM:
            case B.MELON_STEM: case B.DOUBLE_PLANT: case B.FLOWER_POT: case B.SKULL:
                return false;
            default:
                return true;
        }
    }

    private static final int OCC = 0, TOP = 1, BUILD = 2, NONAIR = 3, TRAP = 4, DOOR = 5, FARM = 6, SOIL = 7,
            TORCHBASE = 8, PLATE = 9, WIRE = 10;
    private static final String[] NEED = {"a full opaque block", "a solid top", "a solid block", "a block",
            "a full block, slab or stairs", "the door's lower half", "farmland", "grass or dirt",
            "a solid top, fence, wall or glass", "a solid top or fence", "a solid top or glowstone"};

    private void support() {
        for (int i = 0; i < c.volume(); i++) {
            int b = c.getRaw(i);
            if (b < 0) {
                continue;
            }
            int id = B.id(b), d = B.data(b), x = c.xOf(i), y = c.yOf(i), z = c.zOf(i);
            int dx = 0, dy = -1, dz = 0, kind;
            switch (id) {
                case B.LADDER: case B.WALL_SIGN: case B.WALL_BANNER:
                    dy = 0;
                    dz = d == 2 ? 1 : d == 3 ? -1 : 0;
                    dx = d == 4 ? 1 : d == 5 ? -1 : 0;
                    kind = id == B.LADDER ? OCC : BUILD;
                    break;
                case B.TRIPWIRE_HOOK:
                    dy = 0;
                    dz = (d & 3) == 0 ? -1 : (d & 3) == 2 ? 1 : 0;
                    dx = (d & 3) == 1 ? 1 : (d & 3) == 3 ? -1 : 0;
                    kind = OCC;
                    break;
                case B.STONE_BUTTON: case B.WOOD_BUTTON: case B.LEVER: case B.TORCH: case B.REDSTONE_TORCH:
                case B.REDSTONE_TORCH_OFF: {
                    int f = d & 7;
                    boolean torch = id == B.TORCH || id == B.REDSTONE_TORCH || id == B.REDSTONE_TORCH_OFF;
                    kind = OCC;
                    if (f == 1) {
                        dx = -1; dy = 0;
                    } else if (f == 2) {
                        dx = 1; dy = 0;
                    } else if (f == 3) {
                        dz = -1; dy = 0;
                    } else if (f == 4) {
                        dz = 1; dy = 0;
                    } else if (f == 0 || (f == 7 && id == B.LEVER)) {
                        dy = torch ? -1 : 1;
                    }
                    if (dy == -1) {
                        kind = torch ? TORCHBASE : TOP;
                    }
                    break;
                }
                case B.TRAPDOOR: case B.IRON_TRAPDOOR:
                    dy = 0;
                    dz = (d & 3) == 0 ? 1 : (d & 3) == 1 ? -1 : 0;
                    dx = (d & 3) == 2 ? 1 : (d & 3) == 3 ? -1 : 0;
                    kind = TRAP;
                    break;
                case B.OAK_DOOR: case B.IRON_DOOR: case B.SPRUCE_DOOR: case B.BIRCH_DOOR: case B.DARK_OAK_DOOR:
                    kind = (d & 8) != 0 ? DOOR : TOP;
                    break;
                case B.RAIL: case B.POWERED_RAIL: case B.DETECTOR_RAIL: case B.ACTIVATOR_RAIL: case B.REPEATER:
                case B.COMPARATOR: case B.FLOWER_POT:
                    kind = TOP;
                    break;
                case B.REDSTONE_WIRE:
                    kind = WIRE;
                    break;
                case B.STONE_PLATE: case B.WOOD_PLATE: case B.GOLD_PLATE: case B.IRON_PLATE:
                    kind = PLATE;
                    break;
                case B.CARPET: case B.SAND: case B.GRAVEL: case B.ANVIL: case B.DRAGON_EGG:
                    kind = NONAIR;
                    break;
                case B.SIGN_POST: case B.STANDING_BANNER: case B.CAKE:
                    kind = BUILD;
                    break;
                case B.WHEAT: case B.CARROTS: case B.POTATOES: case B.PUMPKIN_STEM: case B.MELON_STEM:
                    kind = FARM;
                    break;
                case B.DANDELION: case B.FLOWER: case B.TALL_GRASS:
                    kind = SOIL;
                    break;
                case B.DOUBLE_PLANT:
                    kind = (d & 8) != 0 ? NONAIR : SOIL;
                    break;
                default:
                    continue;
            }
            int s = c.get(x + dx, y + dy, z + dz);
            if (s < 0) {
                continue; // natural terrain: unknown
            }
            int sid = B.id(s);
            boolean ok;
            switch (kind) {
                case OCC: ok = occluding(s); break;
                case TOP: ok = topSolid(s); break;
                case BUILD: ok = buildable(s); break;
                case NONAIR: ok = sid != B.AIR && !B.isFluid(s); break;
                case TRAP: ok = (topSolid(s) && !B.isStairs(s) && !B.isHalfSlab(s)) || sid == B.GLOWSTONE
                        || B.isStairs(s) || B.isHalfSlab(s); break;
                case DOOR: ok = sid == id; break;
                case FARM: ok = sid == B.FARMLAND; break;
                case SOIL: ok = sid == B.GRASS || sid == B.DIRT || sid == B.FARMLAND; break;
                case TORCHBASE: ok = topSolid(s) || sid == B.FENCE || sid == B.NETHER_FENCE || sid == B.SPRUCE_FENCE
                        || sid == B.DARK_OAK_FENCE || sid == B.COBBLE_WALL || sid == B.GLASS
                        || sid == B.STAINED_GLASS; break;
                case PLATE: ok = topSolid(s) || sid == B.FENCE || sid == B.NETHER_FENCE || sid == B.SPRUCE_FENCE
                        || sid == B.DARK_OAK_FENCE; break;
                case WIRE: ok = topSolid(s) || sid == B.GLOWSTONE; break;
                default: ok = true;
            }
            if (!ok) {
                supportProblems.add(x + " " + y + " " + z + " " + id + ":" + d + " needs " + NEED[kind] + " at "
                        + (x + dx) + " " + (y + dy) + " " + (z + dz) + " (has " + sid + ":" + B.data(s) + ")");
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // chests: 1.8 joins every touching chest of the same kind, and BlockChest re-orients pairs on placement
    // ---------------------------------------------------------------------------------------------
    private boolean opaqueAt(int x, int y, int z) {
        int b = c.get(x, y, z);
        return b >= 0 && B.isOpaqueCube(b);
    }

    private void chests() {
        for (int i = 0; i < c.volume(); i++) {
            int b = c.getRaw(i);
            if (b < 0 || (B.id(b) != B.CHEST && B.id(b) != B.TRAPPED_CHEST)) {
                continue;
            }
            int x = c.xOf(i), y = c.yOf(i), z = c.zOf(i), id = B.id(b);
            boolean n = B.id(c.get(x, y, z - 1)) == id && c.get(x, y, z - 1) >= 0;
            boolean s = B.id(c.get(x, y, z + 1)) == id && c.get(x, y, z + 1) >= 0;
            boolean w = B.id(c.get(x - 1, y, z)) == id && c.get(x - 1, y, z) >= 0;
            boolean e = B.id(c.get(x + 1, y, z)) == id && c.get(x + 1, y, z) >= 0;
            int count = (n ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0) + (e ? 1 : 0);
            String at = x + " " + y + " " + z;
            if (count > 1) {
                chestProblems.add(at + " touches " + count + " chests of the same kind (merged triple chest)");
                continue;
            }
            if (count == 1) {
                int ox = w ? x - 1 : e ? x + 1 : x, oz = n ? z - 1 : s ? z + 1 : z;
                int other = c.get(ox, y, oz);
                int ocount = 0;
                for (int[] d : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                    int nb = c.get(ox + d[0], y, oz + d[1]);
                    if (nb >= 0 && B.id(nb) == id) {
                        ocount++;
                    }
                }
                if (ocount > 1) {
                    chestProblems.add(at + " pairs with a chest that touches " + ocount + " chests");
                    continue;
                }
                // emulate BlockChest.e(): the facing the game will settle on
                int want;
                if (w || e) {
                    want = B.data(other) == 2 ? 2 : 3;
                    boolean fn = opaqueAt(x, y, z - 1), fs = opaqueAt(x, y, z + 1);
                    boolean on = opaqueAt(ox, y, z - 1), os = opaqueAt(ox, y, z + 1);
                    if ((fn || on) && !fs && !os) {
                        want = 3;
                    }
                    if ((fs || os) && !fn && !on) {
                        want = 2;
                    }
                } else {
                    want = B.data(other) == 4 ? 4 : 5;
                    boolean fw = opaqueAt(x - 1, y, z), fe = opaqueAt(x + 1, y, z);
                    boolean ow = opaqueAt(x - 1, y, oz), oe = opaqueAt(x + 1, y, oz);
                    if ((fw || ow) && !fe && !oe) {
                        want = 5;
                    }
                    if ((fe || oe) && !fw && !ow) {
                        want = 4;
                    }
                }
                if (want != B.data(b)) {
                    chestProblems.add(at + " pair facing " + B.data(b) + " would be turned to " + want);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // light
    // ---------------------------------------------------------------------------------------------
    public void computeLight() {
        int n = c.volume();
        light = new byte[n];
        int[] queue = new int[1 << 22];
        int head = 0, tail = 0;
        for (int i = 0; i < n; i++) {
            int b = c.getRaw(i);
            if (b < 0) {
                continue;
            }
            int e = B.emission(b);
            if (e > 0) {
                light[i] = (byte) e;
                queue[tail++ & (queue.length - 1)] = i;
            }
        }
        int sx = c.sx, sxz = c.sx * c.sz;
        while (head != tail) {
            int i = queue[head++ & (queue.length - 1)];
            int l = light[i];
            if (l <= 1) {
                continue;
            }
            int x = i % sx, z = (i / sx) % c.sz, y = i / sxz;
            int[] nb = {x > 0 ? i - 1 : -1, x < c.sx - 1 ? i + 1 : -1, z > 0 ? i - sx : -1, z < c.sz - 1 ? i + sx : -1,
                    y > 0 ? i - sxz : -1, y < c.sy - 1 ? i + sxz : -1};
            for (int j : nb) {
                if (j < 0) {
                    continue;
                }
                int b = c.getRaw(j);
                if (b < 0) {
                    continue; // unknown terrain: treat as opaque
                }
                int op = B.opacity(b);
                if (op >= 15) {
                    continue;
                }
                int nl = l - Math.max(1, op);
                if (nl > light[j]) {
                    light[j] = (byte) nl;
                    queue[tail++ & (queue.length - 1)] = j;
                }
            }
        }
    }

    /** Adds a light source and propagates it incrementally (light only increases). */
    public void addLight(int x, int y, int z, int level) {
        int start = c.index(x, y, z);
        light[start] = (byte) level;
        int[] queue = new int[1 << 16];
        int head = 0, tail = 0;
        queue[tail++] = start;
        int sx = c.sx, sxz = c.sx * c.sz;
        while (head != tail) {
            int i = queue[head++ & (queue.length - 1)];
            int l = light[i];
            if (l <= 1) {
                continue;
            }
            int xx = i % sx, zz = (i / sx) % c.sz, yy = i / sxz;
            int[] nb = {xx > 0 ? i - 1 : -1, xx < c.sx - 1 ? i + 1 : -1, zz > 0 ? i - sx : -1,
                    zz < c.sz - 1 ? i + sx : -1, yy > 0 ? i - sxz : -1, yy < c.sy - 1 ? i + sxz : -1};
            for (int j : nb) {
                if (j < 0) {
                    continue;
                }
                int b = c.getRaw(j);
                if (b < 0) {
                    continue;
                }
                int op = B.opacity(b);
                if (op >= 15) {
                    continue;
                }
                int nl = l - Math.max(1, op);
                if (nl > light[j]) {
                    light[j] = (byte) nl;
                    queue[tail++ & (queue.length - 1)] = j;
                }
            }
        }
    }

    public int lightAt(int x, int y, int z) {
        if (!c.inBounds(x, y, z)) {
            return 0;
        }
        return light[c.index(x, y, z)];
    }

    // ---------------------------------------------------------------------------------------------
    // spawning
    // ---------------------------------------------------------------------------------------------
    boolean analysedArea(int x, int y, int z) {
        if (y <= 66) {
            return true;
        }
        for (Canvas.Room r : c.rooms) {
            if (x >= r.x1 && x <= r.x2 && y >= r.y1 && y <= r.y2 && z >= r.z1 && z <= r.z2) {
                return true;
            }
        }
        // courtyard pad and approach road
        if (y != Layout.SURF + 1) {
            return false;
        }
        return (x >= -291 && x <= -249 && z >= 111 && z <= 171) || (x >= -247 && x <= -222 && z >= 133 && z <= 149);
    }

    public void spawns() {
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
                    if (!analysedArea(x, y, z)) {
                        continue;
                    }
                    int l = light[c.index(x, y, z)];
                    if (y < 40) {
                        slimeCount++;
                        if (slimeSpawns.size() < 400) {
                            slimeSpawns.add(x + " " + y + " " + z + " on " + B.id(below) + ":" + B.data(below) + " L" + l);
                        }
                    } else if (l < 8) {
                        darkCount++;
                        if (darkSpawns.size() < 400) {
                            darkSpawns.add(x + " " + y + " " + z + " on " + B.id(below) + ":" + B.data(below) + " L" + l);
                        }
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // navigation
    // ---------------------------------------------------------------------------------------------
    /** Top height of a block's collision box (relative, 0..1), or -1 when it cannot be stood on. */
    static double top(int b) {
        int id = B.id(b);
        if (!B.hasCollision(b)) {
            return -1;
        }
        switch (id) {
            case B.FENCE: case B.SPRUCE_FENCE: case B.DARK_OAK_FENCE: case B.NETHER_FENCE: case B.COBBLE_WALL:
            case B.FENCE_GATE: case B.SPRUCE_FENCE_GATE: case B.DARK_OAK_FENCE_GATE: case B.IRON_BARS:
            case B.GLASS_PANE: case B.STAINED_PANE:
            case B.OAK_DOOR: case B.IRON_DOOR: case B.SPRUCE_DOOR: case B.BIRCH_DOOR: case B.DARK_OAK_DOOR:
                return -1;
            case B.CARPET: return 0.0625;
            case B.BED: return 0.5625;
            case B.CHEST: case B.TRAPPED_CHEST: case B.ENDER_CHEST: return 0.875;
            case B.ENCHANT_TABLE: return 0.75;
            case B.DAYLIGHT_SENSOR: return 0.375;
            case B.END_PORTAL_FRAME: return 0.8125;
            case B.SOUL_SAND: return 0.875;
            case B.FARMLAND: return 0.9375;
            case B.SKULL: return 0.5;
            case B.FLOWER_POT: return 0.375;
            case B.CAKE: return 0.5;
            case B.REPEATER: case B.COMPARATOR: return 0.125;
            case B.BREWING_STAND: return 0.875;
            case B.TRAPDOOR: case B.IRON_TRAPDOOR:
                if ((B.data(b) & 4) != 0) {
                    return -1;
                }
                return (B.data(b) & 8) != 0 ? 1.0 : 0.1875;
            default:
                if (B.isHalfSlab(b)) {
                    return (B.data(b) & 8) != 0 ? 1.0 : 0.5;
                }
                return 1.0;
        }
    }

    /** Can the body occupy this cell (no collision, or openable)? */
    boolean passable(int x, int y, int z, BitSet forcedOpen) {
        int b = c.get(x, y, z);
        if (b < 0) {
            return false;
        }
        if (forcedOpen.get(c.index(x, y, z))) {
            return true;
        }
        int id = B.id(b);
        if (id == B.LAVA || id == B.LAVA_FLOW) {
            return false;
        }
        if (B.isDoor(b) || id == B.FENCE_GATE || id == B.SPRUCE_FENCE_GATE || id == B.DARK_OAK_FENCE_GATE) {
            return true;
        }
        if ((id == B.TRAPDOOR || id == B.IRON_TRAPDOOR)) {
            return true;
        }
        return !B.hasCollision(b);
    }

    boolean ladder(int x, int y, int z) {
        int b = c.get(x, y, z);
        return b >= 0 && B.id(b) == B.LADDER;
    }

    /** Standing height if a player can stand on the support at (x,y,z), else NaN. */
    double stand(int x, int y, int z, BitSet open) {
        int b = c.get(x, y, z);
        if (b < 0 || open.get(c.index(x, y, z))) {
            return Double.NaN;
        }
        double t = top(b);
        if (t < 0) {
            return Double.NaN;
        }
        double h = y + t;
        int from = (t >= 1.0) ? y + 1 : y + 1;
        int to = (int) Math.floor(h + 1.79);
        for (int yy = from; yy <= to; yy++) {
            if (!passable(x, yy, z, open)) {
                return Double.NaN;
            }
        }
        return h;
    }

    public void navigation() {
        BitSet open = new BitSet(c.volume());
        for (Canvas.Marker m : c.markers) {
            if (m.type.equals("pistondoor")) {
                open.set(c.index(m.x, m.y, m.z));
                open.set(c.index(m.x, m.y + 1, m.z));
            }
        }
        BitSet seen = new BitSet(c.volume());      // support nodes
        BitSet seenL = new BitSet(c.volume());     // ladder nodes (cell index of ladder)
        int[] q = new int[1 << 23];
        int qh = 0, qt = 0;
        // start: courtyard in front of the blast door (support block at y=67)
        int start = c.index(-285, Layout.SURF, 141);
        seen.set(start);
        q[qt++] = start;
        List<int[]> lifts = c.liftLinks;
        while (qh < qt) {
            int node = q[qh++];
            boolean isLadder = node < 0;
            int idx = isLadder ? -node - 1 : node;
            int x = c.xOf(idx), y = c.yOf(idx), z = c.zOf(idx);
            double h;
            if (isLadder) {
                h = y;
            } else {
                h = stand(x, y, z, open);
                if (Double.isNaN(h)) {
                    continue;
                }
            }
            // horizontal moves
            for (Dir d : Dir.HORIZONTAL) {
                int nx = x + d.dx, nz = z + d.dz;
                int base = (int) Math.floor(h);
                for (int ny = base - 4; ny <= base + 1; ny++) {
                    if (!c.inBounds(nx, ny, nz)) {
                        continue;
                    }
                    double nh = stand(nx, ny, nz, open);
                    if (Double.isNaN(nh)) {
                        continue;
                    }
                    double dh = nh - h;
                    if (dh > 1.25 || dh < -4.0) {
                        continue;
                    }
                    boolean stairStep = B.isStairs(c.get(nx, ny, nz)) && (B.data(c.get(nx, ny, nz)) & 4) == 0
                            && dh <= 1.01;
                    if (dh > 0.6 && !stairStep) {
                        // jump: need headroom above the current position
                        boolean ok = true;
                        for (int yy = (int) Math.floor(h + 1.8); yy <= (int) Math.floor(h + 1.8 + dh); yy++) {
                            if (!passable(x, yy, z, open) && !(isLadder && ladder(x, yy, z))) {
                                ok = false;
                            }
                        }
                        if (!ok) {
                            continue;
                        }
                    }
                    if (dh < -0.6) {
                        // falling: the column we step into must be clear down to the target
                        boolean ok = true;
                        for (int yy = (int) Math.floor(nh + 1.8); yy <= (int) Math.floor(h + 1.7); yy++) {
                            if (!passable(nx, yy, nz, open)) {
                                ok = false;
                            }
                        }
                        if (!ok) {
                            continue;
                        }
                    }
                    int ni = c.index(nx, ny, nz);
                    if (!seen.get(ni)) {
                        seen.set(ni);
                        q[qt++] = ni;
                    }
                }
                // entering a ladder beside us
                for (int ny = (int) Math.floor(h) - 1; ny <= (int) Math.floor(h) + 1; ny++) {
                    if (c.inBounds(nx, ny, nz) && ladder(nx, ny, nz) && passable(nx, ny + 1, nz, open)) {
                        int li = c.index(nx, ny, nz);
                        if (!seenL.get(li)) {
                            seenL.set(li);
                            q[qt++] = -li - 1;
                        }
                    }
                }
            }
            // ladder cell in our own column (standing on the ground in front of/inside a ladder)
            if (!isLadder) {
                int fy = (int) Math.floor(h);
                if (ladder(x, fy, z) || ladder(x, fy + 1, z)) {
                    int ly = ladder(x, fy, z) ? fy : fy + 1;
                    int li = c.index(x, ly, z);
                    if (!seenL.get(li)) {
                        seenL.set(li);
                        q[qt++] = -li - 1;
                    }
                }
            } else {
                // climb up/down
                for (int dy = -1; dy <= 1; dy += 2) {
                    int ny = y + dy;
                    if (c.inBounds(x, ny, z) && ladder(x, ny, z)) {
                        int li = c.index(x, ny, z);
                        if (!seenL.get(li)) {
                            seenL.set(li);
                            q[qt++] = -li - 1;
                        }
                    }
                }
                // leave the ladder at the top onto the block it's attached to? handled by horizontal moves.
                // step off onto ground in this column (bottom of the ladder or top exit through a hatch)
                for (int ny = y - 2; ny <= y + 1; ny++) {
                    if (!c.inBounds(x, ny, z)) {
                        continue;
                    }
                    double nh = stand(x, ny, z, open);
                    if (!Double.isNaN(nh) && Math.abs(nh - h) <= 1.3) {
                        int ni = c.index(x, ny, z);
                        if (!seen.get(ni)) {
                            seen.set(ni);
                            q[qt++] = ni;
                        }
                    }
                }
            }
            // lifts: teleport links between (x,feetY,z) points
            if (!isLadder) {
                for (int[] L : lifts) {
                    for (int side = 0; side < 2; side++) {
                        int ax = L[side * 3], ay = L[side * 3 + 1], az = L[side * 3 + 2];
                        int bx = L[(1 - side) * 3], by = L[(1 - side) * 3 + 1], bz = L[(1 - side) * 3 + 2];
                        if (Math.abs(x - ax) <= 1 && Math.abs(z - az) <= 1 && Math.abs(h - ay) <= 1.0) {
                            for (int ny = by - 2; ny <= by; ny++) {
                                if (!Double.isNaN(stand(bx, ny, bz, open))) {
                                    int ni = c.index(bx, ny, bz);
                                    if (!seen.get(ni)) {
                                        seen.set(ni);
                                        q[qt++] = ni;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (qt >= q.length - 64) {
                break;
            }
        }
        totalRooms = c.rooms.size();
        reachableRooms = 0;
        for (Canvas.Room r : c.rooms) {
            boolean ok = false;
            for (int dx = -1; dx <= 1 && !ok; dx++) {
                for (int dz = -1; dz <= 1 && !ok; dz++) {
                    for (int y = r.ny - 3; y <= r.ny + 1 && !ok; y++) {
                        if (c.inBounds(r.nx + dx, y, r.nz + dz) && seen.get(c.index(r.nx + dx, y, r.nz + dz))) {
                            ok = true;
                        }
                        if (c.inBounds(r.nx + dx, y, r.nz + dz) && seenL.get(c.index(r.nx + dx, y, r.nz + dz))) {
                            ok = true;
                        }
                    }
                }
            }
            if (ok) {
                reachableRooms++;
            } else {
                unreachable.add(r.name + " @" + r.nx + " " + r.ny + " " + r.nz);
            }
        }
        this.reached = seen;
    }

    public BitSet reached;

    // ---------------------------------------------------------------------------------------------
    // doors / redstone / lava
    // ---------------------------------------------------------------------------------------------
    public void doors() {
        for (Canvas.Marker m : c.markers) {
            if (!m.type.startsWith("irondoor")) {
                continue;
            }
            int lower = c.get(m.x, m.y, m.z), upper = c.get(m.x, m.y + 1, m.z);
            if (lower < 0 || B.id(lower) != B.IRON_DOOR || upper < 0 || B.id(upper) != B.IRON_DOOR
                    || (B.data(lower) & 8) != 0 || (B.data(upper) & 8) == 0) {
                doorProblems.add("broken iron door at " + m.x + " " + m.y + " " + m.z);
                continue;
            }
            int activators = 0;
            for (int x = m.x - 2; x <= m.x + 2; x++) {
                for (int y = m.y - 1; y <= m.y + 3; y++) {
                    for (int z = m.z - 2; z <= m.z + 2; z++) {
                        int b = c.get(x, y, z);
                        if (b < 0) {
                            continue;
                        }
                        int id = B.id(b);
                        int[] att = null;
                        if (id == B.STONE_BUTTON || id == B.WOOD_BUTTON || id == B.LEVER) {
                            att = attachedTo(x, y, z, b);
                        } else if (id != B.STONE_PLATE && id != B.WOOD_PLATE && id != B.GOLD_PLATE
                                && id != B.IRON_PLATE) {
                            continue;
                        }
                        if (adjacentToDoor(x, y, z, m) || (att != null && adjacentToDoor(att[0], att[1], att[2], m)
                                && B.isOpaqueCube(c.get(att[0], att[1], att[2])))) {
                            activators++;
                        }
                    }
                }
            }
            if (activators == 0) {
                doorProblems.add("iron door without activator at " + m.x + " " + m.y + " " + m.z);
            }
        }
    }

    static boolean adj(int x, int y, int z, int ax, int ay, int az) {
        return Math.abs(x - ax) + Math.abs(y - ay) + Math.abs(z - az) == 1;
    }

    static boolean adjacentToDoor(int x, int y, int z, Canvas.Marker m) {
        return adj(x, y, z, m.x, m.y, m.z) || adj(x, y, z, m.x, m.y + 1, m.z);
    }

    /** Block a button/lever is attached to. */
    static int[] attachedTo(int x, int y, int z, int b) {
        int id = B.id(b), d = B.data(b) & 7;
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

    public void redstone() {
        int[][] n6 = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
        for (int i = 0; i < c.volume(); i++) {
            int b = c.getRaw(i);
            if (b < 0 || B.id(b) != B.REDSTONE_BLOCK) {
                continue;
            }
            int x = c.xOf(i), y = c.yOf(i), z = c.zOf(i);
            for (int[] d : n6) {
                int nb = c.get(x + d[0], y + d[1], z + d[2]);
                if (nb < 0) {
                    continue;
                }
                int id = B.id(nb);
                if (id == B.TNT || B.isDoor(nb) || id == B.PISTON || id == B.STICKY_PISTON || id == B.HOPPER
                        || id == B.DISPENSER || id == B.DROPPER || id == B.NOTE_BLOCK || id == B.TRAPDOOR
                        || id == B.IRON_TRAPDOOR || id == B.FENCE_GATE || id == B.SPRUCE_FENCE_GATE) {
                    redstoneProblems.add("redstone block at " + x + " " + y + " " + z + " powers " + id);
                }
                if (nb == B.A && y <= 66) {
                    redstoneProblems.add("exposed redstone block at " + x + " " + y + " " + z);
                }
            }
        }
    }

    public void lava() {
        for (int i = 0; i < c.volume(); i++) {
            int b = c.getRaw(i);
            if (b < 0 || !(B.id(b) == B.LAVA || B.id(b) == B.LAVA_FLOW)) {
                continue;
            }
            int x = c.xOf(i), y = c.yOf(i), z = c.zOf(i);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int nb = c.get(x + dx, y + dy, z + dz);
                        if (nb >= 0 && B.isFlammable(nb)) {
                            lavaProblems.add("flammable " + B.id(nb) + " near lava " + x + " " + y + " " + z);
                        }
                    }
                    int above = c.get(x + dx, y + 1, z + dz);
                    if (above < 0 || above == B.A) {
                        lavaProblems.add("air above lava neighbourhood " + x + " " + y + " " + z);
                    }
                }
            }
            int[][] n6 = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
            for (int[] d : n6) {
                int nb = c.get(x + d[0], y + d[1], z + d[2]);
                if (nb < 0 || nb == B.A || (!B.isOpaqueCube(nb) && B.id(nb) != B.GLASS && B.id(nb) != B.LAVA)) {
                    lavaProblems.add("lava not encased at " + x + " " + y + " " + z);
                }
            }
        }
    }

    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("rooms reachable: ").append(reachableRooms).append("/").append(totalRooms).append('\n');
        sb.append("dark hostile spawn spots (y>=40, light<8): ").append(darkCount).append('\n');
        sb.append("slime-capable spawn spots (y<40): ").append(slimeCount).append('\n');
        sb.append("door problems: ").append(doorProblems.size()).append('\n');
        sb.append("redstone problems: ").append(redstoneProblems.size()).append('\n');
        sb.append("lava problems: ").append(lavaProblems.size()).append('\n');
        sb.append("chest problems: ").append(chestProblems.size()).append('\n');
        sb.append("support problems: ").append(supportProblems.size()).append('\n');
        return sb.toString();
    }
}
