package com.blacksite.bunker.design;

import java.util.List;

/** Tile-entity data attached to a block position. */
public abstract class TileSpec {
    public final int x, y, z;

    protected TileSpec(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Sign text (up to 4 lines, max 15 chars each in 1.8). */
    public static final class SignText extends TileSpec {
        public final String[] lines;

        public SignText(int x, int y, int z, String[] lines) {
            super(x, y, z);
            this.lines = lines;
        }
    }

    /** Inventory contents for chests, furnaces, dispensers, droppers, hoppers, brewing stands. */
    public static final class Inventory extends TileSpec {
        /** slot index -> item; null entries allowed. */
        public final ItemSpec[] slots;

        public Inventory(int x, int y, int z, ItemSpec[] slots) {
            super(x, y, z);
            this.slots = slots;
        }
    }

    /** Mob head: type 0 skeleton, 1 wither skeleton, 2 zombie, 3 player(steve), 4 creeper; rotation 0-15. */
    public static final class Head extends TileSpec {
        public final int type;
        public final int rotation;

        public Head(int x, int y, int z, int type, int rotation) {
            super(x, y, z);
            this.type = type;
            this.rotation = rotation;
        }
    }

    /** Banner: base dye colour (1.8 dye/wool colour index) and patterns as (patternId, colour) pairs. */
    public static final class Banner extends TileSpec {
        public final int baseColor;
        public final List<String[]> patterns;

        public Banner(int x, int y, int z, int baseColor, List<String[]> patterns) {
            super(x, y, z);
            this.baseColor = baseColor;
            this.patterns = patterns;
        }
    }
}
