package com.blacksite.bunker.design;

/** Material palette for a zone. All blocks are pre-encoded ({@link B#of}). */
public final class Style {
    public int wall = B.SB;
    public int wallLow = B.SB;
    public int wallHigh = B.SB;
    public int pillar = B.SB_CHISELED;
    public int floor = B.ANDESITE_P;
    /** Slab placed on top of the floor as a spawn-proof walking surface; -1 = none (full floor). */
    public int floorSlab = B.slab(B.SLAB_STONE, false);
    public int ceiling = B.SMOOTH;
    public int accent = B.clay(B.GRAY);
    public int trim = B.IRON;
    public int glass = B.PANE;
    /** light type used by helpers: GLOW, SEA, LAMP */
    public int light = B.GLOW;
    public int carpet = B.carpet(B.GRAY);
    public int door = B.IRON_DOOR;
    public int wood = B.SPRUCE;

    public Style copy() {
        Style s = new Style();
        s.wall = wall;
        s.wallLow = wallLow;
        s.wallHigh = wallHigh;
        s.pillar = pillar;
        s.floor = floor;
        s.floorSlab = floorSlab;
        s.ceiling = ceiling;
        s.accent = accent;
        s.trim = trim;
        s.glass = glass;
        s.light = light;
        s.carpet = carpet;
        s.door = door;
        s.wood = wood;
        return s;
    }

    // --- zone palettes ------------------------------------------------------------------------

    /** Level 1 - Security & Command: stone brick, polished andesite, red accents, redstone lamps. */
    public static Style security() {
        Style s = new Style();
        s.wall = B.SB;
        s.wallLow = B.ANDESITE_P;
        s.wallHigh = B.SMOOTH;
        s.pillar = B.SB_CHISELED;
        s.floor = B.ANDESITE_P;
        s.floorSlab = B.slab(B.SLAB_STONE, false);
        s.ceiling = B.SMOOTH;
        s.accent = B.clay(B.RED);
        s.trim = B.IRON;
        s.light = B.LAMP;
        s.carpet = B.carpet(B.RED);
        return s;
    }

    /** Level 2 - Operations & Living: warm granite, spruce and dark oak, brick. */
    public static Style living() {
        Style s = new Style();
        s.wall = B.SMOOTH;
        s.wallLow = B.planks(B.DARK_OAK);
        s.wallHigh = B.planks(B.SPRUCE);
        s.pillar = B.log(B.SPRUCE, 0);
        s.floor = B.GRANITE_P;
        s.floorSlab = B.woodSlab(B.SPRUCE, false);
        s.ceiling = B.SMOOTH;
        s.accent = B.clay(B.GREEN);
        s.trim = B.planks(B.DARK_OAK);
        s.light = B.GLOW;
        s.carpet = B.carpet(B.GREEN);
        s.wood = B.SPRUCE;
        return s;
    }

    /** Level 3 - Science & Medical: quartz, diorite, white clay, sea lanterns, light blue glass. */
    public static Style science() {
        Style s = new Style();
        s.wall = B.QUARTZ;
        s.wallLow = B.clay(B.WHITE);
        s.wallHigh = B.QUARTZ;
        s.pillar = B.QUARTZ_PILLAR;
        s.floor = B.DIORITE_P;
        s.floorSlab = B.slab(B.SLAB_QUARTZ, false);
        s.ceiling = B.QUARTZ;
        s.accent = B.clay(B.LIGHT_BLUE);
        s.trim = B.DIORITE_P;
        s.glass = B.spane(B.LIGHT_BLUE);
        s.light = B.SEA;
        s.carpet = B.carpet(B.LIGHT_BLUE);
        return s;
    }

    /** Level 4 - Storage & Engineering: stone brick, cobble, iron, orange/yellow stripes. */
    public static Style engineering() {
        Style s = new Style();
        s.wall = B.SB;
        s.wallLow = B.COB;
        s.wallHigh = B.SB;
        s.pillar = B.IRON;
        s.floor = B.STN;
        s.floorSlab = B.slab(B.SLAB_STONE, false);
        s.ceiling = B.SB;
        s.accent = B.clay(B.ORANGE);
        s.trim = B.IRON;
        s.light = B.GLOW;
        s.carpet = B.carpet(B.ORANGE);
        return s;
    }

    /** Level 5 - Reactor & Power: iron, stone brick, black/yellow hazard, nether brick in restricted cores. */
    public static Style reactor() {
        Style s = new Style();
        s.wall = B.SB;
        s.wallLow = B.clay(B.BLACK);
        s.wallHigh = B.SB;
        s.pillar = B.IRON;
        s.floor = B.SB;
        s.floorSlab = B.slab(B.SLAB_STONEBRICK, false);
        s.ceiling = B.SB;
        s.accent = B.clay(B.YELLOW);
        s.trim = B.IRON;
        s.light = B.LAMP;
        s.carpet = B.carpet(B.YELLOW);
        return s;
    }

    /** Level 6 - Containment: dark clay, cracked stone brick, nether brick, obsidian. */
    public static Style containment() {
        Style s = new Style();
        s.wall = B.SB_CRACKED;
        s.wallLow = B.clay(B.BLACK);
        s.wallHigh = B.SB;
        s.pillar = B.NB;
        s.floor = B.SB;
        s.floorSlab = B.slab(B.SLAB_NETHER, false);
        s.ceiling = B.SB_CRACKED;
        s.accent = B.clay(B.PURPLE);
        s.trim = B.OBBY;
        s.light = B.LAMP;
        s.carpet = B.carpet(B.PURPLE);
        return s;
    }
}
