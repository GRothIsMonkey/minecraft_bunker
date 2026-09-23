package com.blacksite.bunker.design;

import java.util.ArrayList;
import java.util.List;

/** Drawing primitives and reusable fixtures on top of the {@link Canvas}. */
public class Painter {
    public final Canvas c;

    public Painter(Canvas c) {
        this.c = c;
    }

    // ---------------------------------------------------------------------------------------------
    // primitives
    // ---------------------------------------------------------------------------------------------

    public void set(int x, int y, int z, int b) {
        c.set(x, y, z, b);
    }

    public int get(int x, int y, int z) {
        return c.get(x, y, z);
    }

    public void fill(int x1, int y1, int z1, int x2, int y2, int z2, int b) {
        int ax = Math.min(x1, x2), bx = Math.max(x1, x2);
        int ay = Math.min(y1, y2), by = Math.max(y1, y2);
        int az = Math.min(z1, z2), bz = Math.max(z1, z2);
        for (int y = ay; y <= by; y++) {
            for (int z = az; z <= bz; z++) {
                for (int x = ax; x <= bx; x++) {
                    c.set(x, y, z, b);
                }
            }
        }
    }

    public void fillIfUnset(int x1, int y1, int z1, int x2, int y2, int z2, int b) {
        int ax = Math.min(x1, x2), bx = Math.max(x1, x2);
        int ay = Math.min(y1, y2), by = Math.max(y1, y2);
        int az = Math.min(z1, z2), bz = Math.max(z1, z2);
        for (int y = ay; y <= by; y++) {
            for (int z = az; z <= bz; z++) {
                for (int x = ax; x <= bx; x++) {
                    c.setIfUnset(x, y, z, b);
                }
            }
        }
    }

    public void replace(int x1, int y1, int z1, int x2, int y2, int z2, int from, int to) {
        int ax = Math.min(x1, x2), bx = Math.max(x1, x2);
        int ay = Math.min(y1, y2), by = Math.max(y1, y2);
        int az = Math.min(z1, z2), bz = Math.max(z1, z2);
        for (int y = ay; y <= by; y++) {
            for (int z = az; z <= bz; z++) {
                for (int x = ax; x <= bx; x++) {
                    if (c.get(x, y, z) == from) {
                        c.set(x, y, z, to);
                    }
                }
            }
        }
    }

    /** Vertical perimeter walls of a box (no floor/ceiling). */
    public void walls(int x1, int y1, int z1, int x2, int y2, int z2, int b) {
        fill(x1, y1, z1, x2, y2, z1, b);
        fill(x1, y1, z2, x2, y2, z2, b);
        fill(x1, y1, z1, x1, y2, z2, b);
        fill(x2, y1, z1, x2, y2, z2, b);
    }

    /** Hollow box: shell of {@code wall} with floor and ceiling, interior filled with air. */
    public void hollow(int x1, int y1, int z1, int x2, int y2, int z2, int wall, int floor, int ceil) {
        int ax = Math.min(x1, x2), bx = Math.max(x1, x2);
        int ay = Math.min(y1, y2), by = Math.max(y1, y2);
        int az = Math.min(z1, z2), bz = Math.max(z1, z2);
        walls(ax, ay, az, bx, by, bz, wall);
        fill(ax, ay, az, bx, ay, bz, floor);
        fill(ax, by, az, bx, by, bz, ceil);
        fill(ax + 1, ay + 1, az + 1, bx - 1, by - 1, bz - 1, B.A);
    }

    public void line(int x1, int y1, int z1, int x2, int y2, int z2, int b) {
        int dx = Integer.signum(x2 - x1), dy = Integer.signum(y2 - y1), dz = Integer.signum(z2 - z1);
        int n = Math.max(Math.abs(x2 - x1), Math.max(Math.abs(y2 - y1), Math.abs(z2 - z1)));
        for (int i = 0; i <= n; i++) {
            c.set(x1 + dx * i, y1 + dy * i, z1 + dz * i, b);
        }
    }

    /** Filled disk in the YZ plane (vertical, facing east-west). */
    public void diskYZ(int x, double cy, double cz, double r, int b) {
        for (int y = (int) Math.floor(cy - r); y <= (int) Math.ceil(cy + r); y++) {
            for (int z = (int) Math.floor(cz - r); z <= (int) Math.ceil(cz + r); z++) {
                double d = (y - cy) * (y - cy) + (z - cz) * (z - cz);
                if (d <= r * r) {
                    c.set(x, y, z, b);
                }
            }
        }
    }

    /** Horizontal filled disk (XZ plane). */
    public void diskXZ(double cx, int y, double cz, double r, int b) {
        for (int x = (int) Math.floor(cx - r); x <= (int) Math.ceil(cx + r); x++) {
            for (int z = (int) Math.floor(cz - r); z <= (int) Math.ceil(cz + r); z++) {
                double d = (x - cx) * (x - cx) + (z - cz) * (z - cz);
                if (d <= r * r) {
                    c.set(x, y, z, b);
                }
            }
        }
    }

    /** Horizontal ring between radii. */
    public void ringXZ(double cx, int y, double cz, double r1, double r2, int b) {
        for (int x = (int) Math.floor(cx - r2); x <= (int) Math.ceil(cx + r2); x++) {
            for (int z = (int) Math.floor(cz - r2); z <= (int) Math.ceil(cz + r2); z++) {
                double d = Math.sqrt((x - cx) * (x - cx) + (z - cz) * (z - cz));
                if (d >= r1 && d <= r2) {
                    c.set(x, y, z, b);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // tile helpers
    // ---------------------------------------------------------------------------------------------

    /** Wall sign whose text faces {@code facing} (attached to the block behind). */
    public void sign(int x, int y, int z, Dir facing, String... lines) {
        c.set(x, y, z, B.wallSign(facing));
        c.tile(new TileSpec.SignText(x, y, z, pad(lines)));
    }

    public void signPost(int x, int y, int z, Dir facing, String... lines) {
        c.set(x, y, z, B.signPost(facing));
        c.tile(new TileSpec.SignText(x, y, z, pad(lines)));
    }

    private static String[] pad(String[] lines) {
        String[] out = new String[4];
        for (int i = 0; i < 4; i++) {
            String s = i < lines.length && lines[i] != null ? lines[i] : "";
            if (s.replaceAll("§.", "").length() > 15) {
                throw new IllegalArgumentException("Sign line too long (>15): '" + s + "'");
            }
            out[i] = s;
        }
        return out;
    }

    public void chest(int x, int y, int z, Dir front, ItemSpec... items) {
        c.set(x, y, z, B.chest(front));
        if (items != null && items.length > 0) {
            c.tile(new TileSpec.Inventory(x, y, z, spread(items, 27, x, y, z)));
        }
    }

    public void trappedChest(int x, int y, int z, Dir front, ItemSpec... items) {
        c.set(x, y, z, B.trappedChest(front));
        if (items != null && items.length > 0) {
            c.tile(new TileSpec.Inventory(x, y, z, spread(items, 27, x, y, z)));
        }
    }

    /** Places items deterministically but not all in the first slots, so chests look "lived in". */
    public static ItemSpec[] spread(ItemSpec[] items, int size, int x, int y, int z) {
        ItemSpec[] slots = new ItemSpec[size];
        int n = 0;
        for (ItemSpec it : items) {
            if (it == null) {
                continue;
            }
            int s = (int) (Canvas.rand(x, y, z, 900 + n) * size);
            int guard = 0;
            while (slots[s] != null && guard++ < size) {
                s = (s + 1) % size;
            }
            if (slots[s] == null) {
                slots[s] = it;
            }
            n++;
        }
        return slots;
    }

    public void inventory(int x, int y, int z, ItemSpec... slots) {
        c.tile(new TileSpec.Inventory(x, y, z, slots));
    }

    public void head(int x, int y, int z, int type, int rotation) {
        c.set(x, y, z, B.skull());
        c.tile(new TileSpec.Head(x, y, z, type, rotation));
    }

    public void wallBanner(int x, int y, int z, Dir facing, int base, String... patternColorPairs) {
        c.set(x, y, z, B.of(B.WALL_BANNER, B.face(facing)));
        List<String[]> pats = new ArrayList<String[]>();
        for (int i = 0; i + 1 < patternColorPairs.length; i += 2) {
            pats.add(new String[]{patternColorPairs[i], patternColorPairs[i + 1]});
        }
        c.tile(new TileSpec.Banner(x, y, z, base, pats));
    }

    public void standingBanner(int x, int y, int z, int rot, int base, String... patternColorPairs) {
        c.set(x, y, z, B.of(B.STANDING_BANNER, rot & 15));
        List<String[]> pats = new ArrayList<String[]>();
        for (int i = 0; i + 1 < patternColorPairs.length; i += 2) {
            pats.add(new String[]{patternColorPairs[i], patternColorPairs[i + 1]});
        }
        c.tile(new TileSpec.Banner(x, y, z, base, pats));
    }

    // ---------------------------------------------------------------------------------------------
    // doors & mechanisms
    // ---------------------------------------------------------------------------------------------

    /** Single door (two halves). */
    public void door(int x, int y, int z, int id, Dir facing, boolean rightHinge) {
        c.set(x, y, z, B.doorLower(id, facing, false));
        c.set(x, y + 1, z, B.doorUpper(id, rightHinge));
    }

    /**
     * Double door across two cells. {@code a} is the left cell when looking toward {@code facing}; {@code b} is
     * the right cell (a + right vector).
     */
    public void doubleDoor(int x, int y, int z, int id, Dir facing) {
        Dir right = facing.cw();
        door(x, y, z, id, facing, false);
        door(x + right.dx, y, z + right.dz, id, facing, true);
    }

    /**
     * Iron door in a wall with a button on both sides. The door cell is (x,y,z); the wall runs perpendicular
     * to {@code facing}. Buttons are placed at head height on the wall block to the right of the door.
     */
    public void ironDoor(int x, int y, int z, Dir facing, boolean buttonsOnRight) {
        door(x, y, z, B.IRON_DOOR, facing, buttonsOnRight);
        Dir side = buttonsOnRight ? facing.cw() : facing.ccw();
        int wx = x + side.dx, wz = z + side.dz;
        c.set(x + side.dx + facing.dx, y + 1, z + side.dz + facing.dz, B.button(facing, false));
        Dir back = facing.opposite();
        c.set(x + side.dx + back.dx, y + 1, z + side.dz + back.dz, B.button(back, false));
        c.marker("irondoor", x, y, z, facing.name());
        c.marker("activator", wx, y + 1, wz, "button-wall");
    }

    /** Iron double door with buttons on both outer sides. */
    public void ironDoubleDoor(int x, int y, int z, Dir facing) {
        Dir right = facing.cw();
        door(x, y, z, B.IRON_DOOR, facing, false);
        door(x + right.dx, y, z + right.dz, B.IRON_DOOR, facing, true);
        Dir back = facing.opposite();
        // left wall block (x - right), right wall block (x + 2 right)
        int lx = x - right.dx, lz = z - right.dz;
        int rx = x + 2 * right.dx, rz = z + 2 * right.dz;
        c.set(lx + facing.dx, y + 1, lz + facing.dz, B.button(facing, false));
        c.set(lx + back.dx, y + 1, lz + back.dz, B.button(back, false));
        c.set(rx + facing.dx, y + 1, rz + facing.dz, B.button(facing, false));
        c.set(rx + back.dx, y + 1, rz + back.dz, B.button(back, false));
        c.marker("irondoor", x, y, z, facing.name());
        c.marker("irondoor", x + right.dx, y, z + right.dz, facing.name());
    }

    /** Lamp that stays lit: a redstone block is hidden behind it (at lamp + back). */
    public void lamp(int x, int y, int z, Dir back) {
        c.set(x + back.dx, y + back.dy, z + back.dz, B.RS_BLOCK);
        c.set(x, y, z, B.LAMP);
    }

    public void ladderColumn(int x, int y1, int y2, int z, Dir facing) {
        for (int y = y1; y <= y2; y++) {
            c.set(x, y, z, B.ladder(facing));
        }
    }

    // ---------------------------------------------------------------------------------------------
    // decorative helpers
    // ---------------------------------------------------------------------------------------------

    /** Yellow/black hazard stripes along a line (alternating every block, diagonal-looking in rows). */
    public void hazardLine(int x1, int y, int z1, int x2, int z2, boolean wool) {
        int dx = Integer.signum(x2 - x1), dz = Integer.signum(z2 - z1);
        int n = Math.max(Math.abs(x2 - x1), Math.abs(z2 - z1));
        for (int i = 0; i <= n; i++) {
            int x = x1 + dx * i, z = z1 + dz * i;
            int col = ((x + z) & 1) == 0 ? B.YELLOW : B.BLACK;
            c.set(x, y, z, wool ? B.wool(col) : B.clay(col));
        }
    }

    public void hazardArea(int x1, int y, int z1, int x2, int z2) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                int col = (Math.floorMod(x + z, 4) < 2) ? B.YELLOW : B.BLACK;
                c.set(x, y, z, B.clay(col));
            }
        }
    }

    /** 3x5 pixel font, used for large facade lettering. */
    private static final String[] FONT_CHARS = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-", " ", "."};
    private static final String[] FONT = {
            "010101111101101", "110101110101110", "011100100100011", "110101101101110", "111100110100111",
            "111100110100100", "011100101101011", "101101111101101", "111010010010111", "001001001101010",
            "101101110101101", "100100100100111", "101111111101101", "110101101101101", "010101101101010",
            "110101110100100", "010101101110011", "110101110101101", "011100010001110", "111010010010010",
            "101101101101111", "101101101101010", "101101111111101", "101101010101101", "101101010010010",
            "111001010100111", "111101101101111", "010110010010111", "110001010100111", "110001010001110",
            "101101111001001", "111100110001110", "011100110101010", "111001010010010", "010101010101010",
            "010101011001110", "000000111000000", "000000000000000", "000000000000010"};

    /**
     * Writes text on a vertical plane. The text reads along {@code along} (e.g. SOUTH), glyph height 5, starting
     * at (x,y,z) = top-left pixel. Returns the width in blocks.
     */
    public int text(String s, int x, int y, int z, Dir along, int ink) {
        int col = 0;
        for (char ch : s.toUpperCase().toCharArray()) {
            int gi = -1;
            for (int i = 0; i < FONT_CHARS.length; i++) {
                if (FONT_CHARS[i].charAt(0) == ch) {
                    gi = i;
                    break;
                }
            }
            if (gi < 0) {
                gi = FONT_CHARS.length - 2;
            }
            String g = FONT[gi];
            for (int row = 0; row < 5; row++) {
                for (int k = 0; k < 3; k++) {
                    if (g.charAt(row * 3 + k) == '1') {
                        int px = x + along.dx * (col + k);
                        int pz = z + along.dz * (col + k);
                        c.set(px, y - row, pz, ink);
                    }
                }
            }
            col += 4;
        }
        return col - 1;
    }

    public static int textWidth(String s) {
        return s.length() * 4 - 1;
    }

    // ---------------------------------------------------------------------------------------------
    // entities
    // ---------------------------------------------------------------------------------------------

    public EntitySpec armorStand(int x, int y, int z, Dir facing) {
        EntitySpec e = new EntitySpec(EntitySpec.Kind.ARMOR_STAND, x, y, z, facing);
        c.entities.add(e);
        return e;
    }

    /** Item frame hanging in cell (x,y,z) on the block behind it, its front facing {@code facing}. */
    public EntitySpec itemFrame(int x, int y, int z, Dir facing, ItemSpec item) {
        EntitySpec e = new EntitySpec(EntitySpec.Kind.ITEM_FRAME, x, y, z, facing);
        e.frameItem = item;
        c.entities.add(e);
        return e;
    }

    public EntitySpec painting(int x, int y, int z, Dir facing, String art) {
        EntitySpec e = new EntitySpec(EntitySpec.Kind.PAINTING, x, y, z, facing);
        e.art = art;
        c.entities.add(e);
        return e;
    }

    public EntitySpec minecart(int x, int y, int z) {
        EntitySpec e = new EntitySpec(EntitySpec.Kind.MINECART, x, y, z, Dir.NORTH);
        c.entities.add(e);
        return e;
    }
}
