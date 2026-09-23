package com.blacksite.bunker.design;

/**
 * Minecraft 1.8.8 block ids and metadata encoders.
 *
 * A block is encoded as a single int: {@code (id << 4) | data}. Only ids and data values that exist in
 * Minecraft Java Edition 1.8.8 are used anywhere in the design.
 */
public final class B {
    private B() {
    }

    // ---- ids (1.8.8) -------------------------------------------------------------------------
    public static final int AIR = 0, STONE = 1, GRASS = 2, DIRT = 3, COBBLE = 4, PLANKS = 5, BEDROCK = 7,
            WATER_FLOW = 8, WATER = 9, LAVA_FLOW = 10, LAVA = 11, SAND = 12, GRAVEL = 13, LOG = 17,
            LEAVES = 18, SPONGE = 19, GLASS = 20, LAPIS_BLOCK = 22, DISPENSER = 23, SANDSTONE = 24,
            NOTE_BLOCK = 25, BED = 26, POWERED_RAIL = 27, DETECTOR_RAIL = 28, STICKY_PISTON = 29, WEB = 30,
            TALL_GRASS = 31, DEAD_BUSH = 32, PISTON = 33, PISTON_HEAD = 34, WOOL = 35, DANDELION = 37,
            FLOWER = 38, BROWN_MUSHROOM = 39, RED_MUSHROOM = 40, GOLD_BLOCK = 41, IRON_BLOCK = 42,
            DOUBLE_SLAB = 43, SLAB = 44, BRICK = 45, TNT = 46, BOOKSHELF = 47, MOSSY_COBBLE = 48,
            OBSIDIAN = 49, TORCH = 50, FIRE = 51, SPAWNER = 52, OAK_STAIRS = 53, CHEST = 54,
            REDSTONE_WIRE = 55, DIAMOND_BLOCK = 57, WORKBENCH = 58, WHEAT = 59, FARMLAND = 60, FURNACE = 61,
            LIT_FURNACE = 62, SIGN_POST = 63, OAK_DOOR = 64, LADDER = 65, RAIL = 66, COBBLE_STAIRS = 67,
            WALL_SIGN = 68, LEVER = 69, STONE_PLATE = 70, IRON_DOOR = 71, WOOD_PLATE = 72,
            REDSTONE_TORCH_OFF = 75, REDSTONE_TORCH = 76, STONE_BUTTON = 77, SNOW_LAYER = 78, ICE = 79,
            SNOW_BLOCK = 80, CACTUS = 81, CLAY = 82, SUGAR_CANE = 83, JUKEBOX = 84, FENCE = 85,
            PUMPKIN = 86, NETHERRACK = 87, SOUL_SAND = 88, GLOWSTONE = 89, JACK_O_LANTERN = 91, CAKE = 92,
            REPEATER = 93, STAINED_GLASS = 95, TRAPDOOR = 96, STONE_BRICK = 98, IRON_BARS = 101,
            GLASS_PANE = 102, MELON = 103, PUMPKIN_STEM = 104, MELON_STEM = 105, VINE = 106,
            FENCE_GATE = 107, BRICK_STAIRS = 108, STONE_BRICK_STAIRS = 109, NETHER_BRICK = 112,
            NETHER_FENCE = 113, NETHER_BRICK_STAIRS = 114, NETHER_WART = 115, ENCHANT_TABLE = 116,
            BREWING_STAND = 117, CAULDRON = 118, END_PORTAL_FRAME = 120, END_STONE = 121, DRAGON_EGG = 122,
            LAMP_OFF = 123, LAMP_ON = 124, DOUBLE_WOOD_SLAB = 125, WOOD_SLAB = 126, SANDSTONE_STAIRS = 128,
            ENDER_CHEST = 130, TRIPWIRE_HOOK = 131, EMERALD_BLOCK = 133, SPRUCE_STAIRS = 134,
            BIRCH_STAIRS = 135, JUNGLE_STAIRS = 136, BEACON = 138, COBBLE_WALL = 139, FLOWER_POT = 140,
            CARROTS = 141, POTATOES = 142, WOOD_BUTTON = 143, SKULL = 144, ANVIL = 145, TRAPPED_CHEST = 146,
            GOLD_PLATE = 147, IRON_PLATE = 148, COMPARATOR = 149, DAYLIGHT_SENSOR = 151,
            REDSTONE_BLOCK = 152, HOPPER = 154, QUARTZ_BLOCK = 155, QUARTZ_STAIRS = 156, ACTIVATOR_RAIL = 157,
            DROPPER = 158, STAINED_CLAY = 159, STAINED_PANE = 160, LEAVES2 = 161, LOG2 = 162,
            ACACIA_STAIRS = 163, DARK_OAK_STAIRS = 164, SLIME = 165, IRON_TRAPDOOR = 167, PRISMARINE = 168,
            SEA_LANTERN = 169, HAY_BALE = 170, CARPET = 171, HARDENED_CLAY = 172, COAL_BLOCK = 173,
            PACKED_ICE = 174, DOUBLE_PLANT = 175, STANDING_BANNER = 176, WALL_BANNER = 177,
            RED_SANDSTONE = 179, RED_SANDSTONE_STAIRS = 180, RED_SANDSTONE_SLAB = 182, SPRUCE_FENCE_GATE = 183,
            DARK_OAK_FENCE_GATE = 186, SPRUCE_FENCE = 188, DARK_OAK_FENCE = 191, SPRUCE_DOOR = 193,
            BIRCH_DOOR = 194, DARK_OAK_DOOR = 197;

    // ---- colours (wool / clay / glass / carpet) -------------------------------------------------
    public static final int WHITE = 0, ORANGE = 1, MAGENTA = 2, LIGHT_BLUE = 3, YELLOW = 4, LIME = 5, PINK = 6,
            GRAY = 7, SILVER = 8, CYAN = 9, PURPLE = 10, BLUE = 11, BROWN = 12, GREEN = 13, RED = 14, BLACK = 15;

    // ---- wood types ----------------------------------------------------------------------------
    public static final int OAK = 0, SPRUCE = 1, BIRCH = 2, JUNGLE = 3, ACACIA = 4, DARK_OAK = 5;

    public static int of(int id, int data) {
        return (id << 4) | (data & 15);
    }

    public static int of(int id) {
        return id << 4;
    }

    public static int id(int b) {
        return (b >> 4) & 0xFFF;
    }

    public static int data(int b) {
        return b & 15;
    }

    // ---- common pre-encoded blocks --------------------------------------------------------------
    public static final int A = of(AIR);
    public static final int STN = of(STONE);
    public static final int GRANITE_P = of(STONE, 2);
    public static final int DIORITE_P = of(STONE, 4);
    public static final int ANDESITE = of(STONE, 5);
    public static final int ANDESITE_P = of(STONE, 6);
    public static final int DIORITE = of(STONE, 3);
    public static final int GRANITE = of(STONE, 1);
    public static final int SB = of(STONE_BRICK, 0);
    public static final int SB_MOSSY = of(STONE_BRICK, 1);
    public static final int SB_CRACKED = of(STONE_BRICK, 2);
    public static final int SB_CHISELED = of(STONE_BRICK, 3);
    public static final int SMOOTH = of(DOUBLE_SLAB, 8);
    public static final int SMOOTH_DOUBLE = of(DOUBLE_SLAB, 0);
    public static final int COB = of(COBBLE);
    public static final int IRON = of(IRON_BLOCK);
    public static final int GOLD = of(GOLD_BLOCK);
    public static final int QUARTZ = of(QUARTZ_BLOCK, 0);
    public static final int QUARTZ_CHISELED = of(QUARTZ_BLOCK, 1);
    public static final int QUARTZ_PILLAR = of(QUARTZ_BLOCK, 2);
    public static final int GLOW = of(GLOWSTONE);
    public static final int SEA = of(SEA_LANTERN);
    public static final int LAMP = of(LAMP_ON);
    public static final int RS_BLOCK = of(REDSTONE_BLOCK);
    public static final int OBBY = of(OBSIDIAN);
    public static final int NB = of(NETHER_BRICK);
    public static final int COAL = of(COAL_BLOCK);
    public static final int GLS = of(GLASS);
    public static final int PANE = of(GLASS_PANE);
    public static final int BARS = of(IRON_BARS);
    public static final int BOOKS = of(BOOKSHELF);
    public static final int DIRT_B = of(DIRT);
    public static final int GRASS_B = of(GRASS);
    public static final int BRICKS = of(BRICK);
    public static final int PRIS = of(PRISMARINE, 0);
    public static final int PRIS_BRICK = of(PRISMARINE, 1);
    public static final int PRIS_DARK = of(PRISMARINE, 2);
    public static final int HCLAY = of(HARDENED_CLAY);
    public static final int WORKBENCH_B = of(WORKBENCH);
    public static final int COBWALL = of(COBBLE_WALL);
    public static final int WEB_B = of(WEB);

    public static int wool(int c) {
        return of(WOOL, c);
    }

    public static int clay(int c) {
        return of(STAINED_CLAY, c);
    }

    public static int sglass(int c) {
        return of(STAINED_GLASS, c);
    }

    public static int spane(int c) {
        return of(STAINED_PANE, c);
    }

    public static int carpet(int c) {
        return of(CARPET, c);
    }

    public static int planks(int wood) {
        return of(PLANKS, wood);
    }

    /** Log with axis: 0 = vertical, 1 = east-west, 2 = north-south. */
    public static int log(int wood, int axis) {
        int ax = axis == 1 ? 4 : axis == 2 ? 8 : 0;
        if (wood >= 4) {
            return of(LOG2, (wood - 4) | ax);
        }
        return of(LOG, wood | ax);
    }

    // ---- slabs ---------------------------------------------------------------------------------
    public static final int SLAB_STONE = 0, SLAB_SANDSTONE = 1, SLAB_COBBLE = 3, SLAB_BRICK = 4, SLAB_STONEBRICK = 5,
            SLAB_NETHER = 6, SLAB_QUARTZ = 7;

    public static int slab(int type, boolean top) {
        return of(SLAB, type | (top ? 8 : 0));
    }

    public static int woodSlab(int wood, boolean top) {
        return of(WOOD_SLAB, wood | (top ? 8 : 0));
    }

    public static int doubleSlab(int type) {
        return of(DOUBLE_SLAB, type);
    }

    // ---- stairs --------------------------------------------------------------------------------
    /**
     * Stairs whose high (back) side points toward {@code ascend}: walking toward {@code ascend} goes up.
     */
    public static int stairs(int id, Dir ascend, boolean upsideDown) {
        int d;
        switch (ascend) {
            case EAST: d = 0; break;
            case WEST: d = 1; break;
            case SOUTH: d = 2; break;
            default: d = 3; break;
        }
        return of(id, d | (upsideDown ? 4 : 0));
    }

    public static int woodStairs(int wood, Dir ascend, boolean upsideDown) {
        int id;
        switch (wood) {
            case SPRUCE: id = SPRUCE_STAIRS; break;
            case BIRCH: id = BIRCH_STAIRS; break;
            case JUNGLE: id = JUNGLE_STAIRS; break;
            case ACACIA: id = ACACIA_STAIRS; break;
            case DARK_OAK: id = DARK_OAK_STAIRS; break;
            default: id = OAK_STAIRS; break;
        }
        return stairs(id, ascend, upsideDown);
    }

    // ---- facing blocks: chest, furnace, dispenser, dropper, ladder, wall sign, ender chest ----------
    /** Facing code 2..5 used by chests, furnaces, ladders, wall signs, dispensers, droppers. */
    public static int face(Dir d) {
        switch (d) {
            case NORTH: return 2;
            case SOUTH: return 3;
            case WEST: return 4;
            case EAST: return 5;
            case UP: return 1;
            default: return 0;
        }
    }

    public static int chest(Dir front) {
        return of(CHEST, face(front));
    }

    public static int trappedChest(Dir front) {
        return of(TRAPPED_CHEST, face(front));
    }

    public static int enderChest(Dir front) {
        return of(ENDER_CHEST, face(front));
    }

    public static int furnace(Dir front) {
        return of(FURNACE, face(front));
    }

    public static int litFurnace(Dir front) {
        return of(LIT_FURNACE, face(front));
    }

    public static int dispenser(Dir front) {
        return of(DISPENSER, face(front));
    }

    public static int dropper(Dir front) {
        return of(DROPPER, face(front));
    }

    /** Ladder on the side {@code facing} of the block it hangs on (it is attached to the block behind it). */
    public static int ladder(Dir facing) {
        return of(LADDER, face(facing));
    }

    /** Wall sign whose text faces {@code facing}; it is attached to the block opposite of facing. */
    public static int wallSign(Dir facing) {
        return of(WALL_SIGN, face(facing));
    }

    /** Standing sign rotation 0..15 (0 = facing south, 4 = west, 8 = north, 12 = east). */
    public static int signPost(int rot) {
        return of(SIGN_POST, rot & 15);
    }

    public static int signPost(Dir facing) {
        switch (facing) {
            case SOUTH: return signPost(0);
            case WEST: return signPost(4);
            case NORTH: return signPost(8);
            default: return signPost(12);
        }
    }

    public static int hopper(Dir out) {
        int d;
        switch (out) {
            case DOWN: d = 0; break;
            case NORTH: d = 2; break;
            case SOUTH: d = 3; break;
            case WEST: d = 4; break;
            default: d = 5; break;
        }
        return of(HOPPER, d);
    }

    // ---- torches / buttons / levers -------------------------------------------------------------
    /** Torch-like code: 1 east, 2 west, 3 south, 4 north, 5 up. The block points toward {@code facing}. */
    private static int torchCode(Dir facing) {
        switch (facing) {
            case EAST: return 1;
            case WEST: return 2;
            case SOUTH: return 3;
            case NORTH: return 4;
            default: return 5;
        }
    }

    public static int torch(Dir facing) {
        return of(TORCH, torchCode(facing));
    }

    public static int redstoneTorch(Dir facing) {
        return of(REDSTONE_TORCH, torchCode(facing));
    }

    /** Button on the wall face pointing to {@code facing} (attached to the block behind). */
    public static int button(Dir facing, boolean wood) {
        int d;
        switch (facing) {
            case DOWN: d = 0; break;
            case EAST: d = 1; break;
            case WEST: d = 2; break;
            case SOUTH: d = 3; break;
            case NORTH: d = 4; break;
            default: d = 5; break;
        }
        return of(wood ? WOOD_BUTTON : STONE_BUTTON, d);
    }

    /** Wall lever pointing toward {@code facing}. */
    public static int lever(Dir facing, boolean on) {
        int d;
        switch (facing) {
            case EAST: d = 1; break;
            case WEST: d = 2; break;
            case SOUTH: d = 3; break;
            case NORTH: d = 4; break;
            case UP: d = 5; break;
            default: d = 0; break;
        }
        return of(LEVER, d | (on ? 8 : 0));
    }

    // ---- doors ----------------------------------------------------------------------------------
    /** Lower door half. {@code facing}: direction a player looks when walking through toward it. */
    public static int doorLower(int id, Dir facing, boolean open) {
        int d;
        switch (facing) {
            case EAST: d = 0; break;
            case SOUTH: d = 1; break;
            case WEST: d = 2; break;
            default: d = 3; break;
        }
        return of(id, d | (open ? 4 : 0));
    }

    public static int doorUpper(int id, boolean rightHinge) {
        return of(id, 8 | (rightHinge ? 1 : 0));
    }

    // ---- beds -----------------------------------------------------------------------------------
    /** Bed part; {@code dir} points from foot to head. */
    public static int bed(Dir dir, boolean head) {
        int d;
        switch (dir) {
            case SOUTH: d = 0; break;
            case WEST: d = 1; break;
            case NORTH: d = 2; break;
            default: d = 3; break;
        }
        return of(BED, d | (head ? 8 : 0));
    }

    // ---- trapdoors ------------------------------------------------------------------------------
    /**
     * Trapdoor. In 1.8 the trapdoor hangs on the block opposite of {@code facing} (that block must be solid).
     */
    public static int trapdoor(int id, Dir facing, boolean open, boolean top) {
        int d;
        switch (facing) {
            case NORTH: d = 0; break;
            case SOUTH: d = 1; break;
            case WEST: d = 2; break;
            default: d = 3; break;
        }
        return of(id, d | (open ? 4 : 0) | (top ? 8 : 0));
    }

    /** Pumpkin / jack o'lantern facing. */
    public static int pumpkinFace(int id, Dir facing) {
        int d;
        switch (facing) {
            case SOUTH: d = 0; break;
            case WEST: d = 1; break;
            case NORTH: d = 2; break;
            default: d = 3; break;
        }
        return of(id, d);
    }

    /** Anvil, long axis along x (east-west) or z. */
    public static int anvil(boolean alongX) {
        return of(ANVIL, alongX ? 0 : 1);
    }

    /** Fence gate; facing = direction the gate faces when closed (0 south,1 west,2 north,3 east). */
    public static int fenceGate(int id, Dir facing, boolean open) {
        int d;
        switch (facing) {
            case SOUTH: d = 0; break;
            case WEST: d = 1; break;
            case NORTH: d = 2; break;
            default: d = 3; break;
        }
        return of(id, d | (open ? 4 : 0));
    }

    /** Repeater pointing its output toward {@code out}, delay 1-4. */
    public static int repeater(Dir out, int delay) {
        int d;
        switch (out) {
            case SOUTH: d = 0; break; // input north, output south
            case WEST: d = 1; break;
            case NORTH: d = 2; break;
            default: d = 3; break;
        }
        return of(REPEATER, d | ((delay - 1) << 2));
    }

    public static int comparator(Dir out) {
        int d;
        switch (out) {
            case SOUTH: d = 0; break;
            case WEST: d = 1; break;
            case NORTH: d = 2; break;
            default: d = 3; break;
        }
        return of(COMPARATOR, d);
    }

    /** End portal frame (facing = direction the frame faces), optionally with eye. */
    public static int endFrame(Dir facing, boolean eye) {
        int d;
        switch (facing) {
            case SOUTH: d = 0; break;
            case WEST: d = 1; break;
            case NORTH: d = 2; break;
            default: d = 3; break;
        }
        return of(END_PORTAL_FRAME, d | (eye ? 4 : 0));
    }

    /** Rail shapes: 0 NS, 1 EW, 2 ascend east, 3 ascend west, 4 ascend north, 5 ascend south. */
    public static int poweredRail(int shape, boolean powered) {
        return of(POWERED_RAIL, shape | (powered ? 8 : 0));
    }

    public static int rail(int shape) {
        return of(RAIL, shape);
    }

    public static int skull() {
        return of(SKULL, 1);
    }

    public static int wallSkull(Dir facing) {
        return of(SKULL, face(facing));
    }

    // ---- classification helpers -------------------------------------------------------------------

    /** Blocks that emit light (block light level), 1.8 values. */
    public static int emission(int b) {
        switch (id(b)) {
            case GLOWSTONE: case SEA_LANTERN: case LAMP_ON: case JACK_O_LANTERN: case BEACON: case LAVA: case LAVA_FLOW:
            case FIRE:
                return 15;
            case TORCH: return 14;
            case LIT_FURNACE: return 13;
            case END_PORTAL_FRAME: return 1;
            case REDSTONE_TORCH: return 7;
            case BREWING_STAND: return 1;
            case DRAGON_EGG: return 1;
            case ENDER_CHEST: return 7;
            case BROWN_MUSHROOM: return 1;
            default: return 0;
        }
    }

    /** Light opacity as used by the 1.8 lighting engine (0..255). */
    public static int opacity(int b) {
        int id = id(b);
        switch (id) {
            case AIR: case GLASS: case STAINED_GLASS: case GLASS_PANE: case STAINED_PANE: case IRON_BARS: case FENCE:
            case SPRUCE_FENCE: case DARK_OAK_FENCE: case NETHER_FENCE: case COBBLE_WALL: case FENCE_GATE:
            case SPRUCE_FENCE_GATE: case DARK_OAK_FENCE_GATE: case TORCH: case REDSTONE_TORCH: case REDSTONE_TORCH_OFF:
            case LADDER: case WALL_SIGN: case SIGN_POST: case LEVER: case STONE_BUTTON: case WOOD_BUTTON:
            case STONE_PLATE: case WOOD_PLATE: case GOLD_PLATE: case IRON_PLATE: case CARPET: case RAIL:
            case POWERED_RAIL: case DETECTOR_RAIL: case ACTIVATOR_RAIL: case OAK_DOOR: case IRON_DOOR: case SPRUCE_DOOR:
            case BIRCH_DOOR: case DARK_OAK_DOOR: case TRAPDOOR: case IRON_TRAPDOOR: case BED: case CHEST:
            case TRAPPED_CHEST: case ENDER_CHEST: case BREWING_STAND: case CAULDRON: case FLOWER_POT: case SKULL:
            case ANVIL: case ENCHANT_TABLE: case END_PORTAL_FRAME: case DRAGON_EGG: case HOPPER: case DAYLIGHT_SENSOR:
            case REPEATER: case COMPARATOR: case REDSTONE_WIRE: case WHEAT: case CARROTS: case POTATOES:
            case SUGAR_CANE: case TALL_GRASS: case FLOWER: case DANDELION: case RED_MUSHROOM: case BROWN_MUSHROOM:
            case WEB: case BEACON: case CAKE: case TRIPWIRE_HOOK: case STANDING_BANNER: case WALL_BANNER: case VINE:
            case PISTON_HEAD: case SNOW_LAYER: case DOUBLE_PLANT: case DEAD_BUSH: case PUMPKIN_STEM: case MELON_STEM:
            case NETHER_WART: case FIRE:
                return 0;
            case LEAVES: case LEAVES2:
                return 1;
            case WATER: case WATER_FLOW: case ICE:
                return 3;
            case SLIME:
                return 0;
            default:
                return 255;
        }
    }

    /** Full opaque cube (occluding) — what 1.8 calls a "normal cube". */
    public static boolean isOpaqueCube(int b) {
        int id = id(b);
        if (opacity(b) < 255) {
            return false;
        }
        switch (id) {
            case SLAB: case WOOD_SLAB: case RED_SANDSTONE_SLAB: case OAK_STAIRS: case COBBLE_STAIRS: case BRICK_STAIRS:
            case STONE_BRICK_STAIRS: case NETHER_BRICK_STAIRS: case SANDSTONE_STAIRS: case SPRUCE_STAIRS:
            case BIRCH_STAIRS: case JUNGLE_STAIRS: case QUARTZ_STAIRS: case ACACIA_STAIRS: case DARK_OAK_STAIRS:
            case RED_SANDSTONE_STAIRS: case FARMLAND: case SOUL_SAND: case LAVA: case LAVA_FLOW: case PISTON:
            case STICKY_PISTON: case CACTUS:
                return false;
            default:
                return true;
        }
    }

    public static boolean isStairs(int b) {
        switch (id(b)) {
            case OAK_STAIRS: case COBBLE_STAIRS: case BRICK_STAIRS: case STONE_BRICK_STAIRS: case NETHER_BRICK_STAIRS:
            case SANDSTONE_STAIRS: case SPRUCE_STAIRS: case BIRCH_STAIRS: case JUNGLE_STAIRS: case QUARTZ_STAIRS:
            case ACACIA_STAIRS: case DARK_OAK_STAIRS: case RED_SANDSTONE_STAIRS:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHalfSlab(int b) {
        int id = id(b);
        return id == SLAB || id == WOOD_SLAB || id == RED_SANDSTONE_SLAB;
    }

    /**
     * 1.8 World.doesBlockHaveSolidTopSurface: mobs may stand/spawn on top of this block.
     */
    public static boolean solidTop(int b) {
        int id = id(b);
        if (isOpaqueCube(b)) {
            // glowstone / sea lantern use the translucent "glass" material in 1.8: not a spawn surface
            return id != BEDROCK && id != 166 && id != GLOWSTONE && id != SEA_LANTERN;
        }
        if (isStairs(b)) {
            return (data(b) & 4) != 0;
        }
        if (isHalfSlab(b)) {
            return (data(b) & 8) != 0;
        }
        if (id == HOPPER) {
            return true;
        }
        if (id == SNOW_LAYER) {
            return (data(b) & 7) == 7;
        }
        // retracted pistons are full cubes for spawning purposes
        return id == PISTON || id == STICKY_PISTON;
    }

    /**
     * Whether an entity bounding box placed at the bottom of this cell would collide with the block (used by
     * the spawn analyser: mobs cannot spawn inside a cell that has any collision box).
     */
    public static boolean hasCollision(int b) {
        int id = id(b);
        switch (id) {
            case AIR: case TORCH: case REDSTONE_TORCH: case REDSTONE_TORCH_OFF: case WALL_SIGN: case SIGN_POST:
            case LEVER: case STONE_BUTTON: case WOOD_BUTTON: case STONE_PLATE: case WOOD_PLATE: case GOLD_PLATE:
            case IRON_PLATE: case RAIL: case POWERED_RAIL: case DETECTOR_RAIL: case ACTIVATOR_RAIL: case REDSTONE_WIRE:
            case WHEAT: case CARROTS: case POTATOES: case SUGAR_CANE: case TALL_GRASS: case FLOWER: case DANDELION:
            case RED_MUSHROOM: case BROWN_MUSHROOM: case WEB: case TRIPWIRE_HOOK: case STANDING_BANNER: case WALL_BANNER:
            case VINE: case DOUBLE_PLANT: case DEAD_BUSH: case PUMPKIN_STEM: case MELON_STEM: case NETHER_WART:
            case FIRE: case WATER: case WATER_FLOW: case LAVA: case LAVA_FLOW: case LADDER:
                return false;
            default:
                return true;
        }
    }

    /** Liquid, or occluding: a spawn needs the feet and head cells to be neither. */
    public static boolean isLiquid(int b) {
        int id = id(b);
        return id == WATER || id == WATER_FLOW || id == LAVA || id == LAVA_FLOW;
    }

    /** Blocks that need a supporting neighbour and must be placed after the structure. */
    public static boolean isAttachable(int b) {
        switch (id(b)) {
            case TORCH: case REDSTONE_TORCH: case REDSTONE_TORCH_OFF: case WALL_SIGN: case SIGN_POST: case LEVER:
            case STONE_BUTTON: case WOOD_BUTTON: case STONE_PLATE: case WOOD_PLATE: case GOLD_PLATE: case IRON_PLATE:
            case RAIL: case POWERED_RAIL: case DETECTOR_RAIL: case ACTIVATOR_RAIL: case REDSTONE_WIRE: case LADDER:
            case OAK_DOOR: case IRON_DOOR: case SPRUCE_DOOR: case BIRCH_DOOR: case DARK_OAK_DOOR: case TRAPDOOR:
            case IRON_TRAPDOOR: case BED: case CARPET: case FLOWER_POT: case SKULL: case WHEAT: case CARROTS:
            case POTATOES: case SUGAR_CANE: case TALL_GRASS: case FLOWER: case DANDELION: case RED_MUSHROOM:
            case BROWN_MUSHROOM: case TRIPWIRE_HOOK: case STANDING_BANNER: case WALL_BANNER: case VINE:
            case DOUBLE_PLANT: case DEAD_BUSH: case PUMPKIN_STEM: case MELON_STEM: case NETHER_WART: case REPEATER:
            case COMPARATOR: case CAKE: case DRAGON_EGG: case SNOW_LAYER: case DAYLIGHT_SENSOR: case BREWING_STAND:
            case ANVIL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFluid(int b) {
        return isLiquid(b);
    }

    public static boolean isLightSolid(int b) {
        switch (id(b)) {
            case GLOWSTONE: case SEA_LANTERN: case LAMP_ON: case JACK_O_LANTERN: case BEACON: case LIT_FURNACE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFlammable(int b) {
        switch (id(b)) {
            case PLANKS: case LOG: case LOG2: case LEAVES: case LEAVES2: case WOOL: case CARPET: case BOOKSHELF:
            case TNT: case OAK_STAIRS: case SPRUCE_STAIRS: case BIRCH_STAIRS: case JUNGLE_STAIRS: case ACACIA_STAIRS:
            case DARK_OAK_STAIRS: case WOOD_SLAB: case DOUBLE_WOOD_SLAB: case FENCE: case SPRUCE_FENCE:
            case DARK_OAK_FENCE: case FENCE_GATE: case SPRUCE_FENCE_GATE: case DARK_OAK_FENCE_GATE: case HAY_BALE:
            case VINE: case TALL_GRASS: case DOUBLE_PLANT: case DEAD_BUSH: case COAL_BLOCK: case STANDING_BANNER:
            case WALL_BANNER: case BED:
                return true;
            default:
                return false;
        }
    }

    /** Tile-entity blocks. */
    public static boolean hasTile(int b) {
        switch (id(b)) {
            case CHEST: case TRAPPED_CHEST: case FURNACE: case LIT_FURNACE: case DISPENSER: case DROPPER: case HOPPER:
            case BREWING_STAND: case WALL_SIGN: case SIGN_POST: case SKULL: case STANDING_BANNER: case WALL_BANNER:
            case FLOWER_POT: case BEACON: case ENCHANT_TABLE: case ENDER_CHEST: case NOTE_BLOCK: case JUKEBOX:
            case SPAWNER: case COMPARATOR: case DAYLIGHT_SENSOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDoor(int b) {
        int id = id(b);
        return id == OAK_DOOR || id == IRON_DOOR || id == SPRUCE_DOOR || id == BIRCH_DOOR || id == DARK_OAK_DOOR;
    }
}
