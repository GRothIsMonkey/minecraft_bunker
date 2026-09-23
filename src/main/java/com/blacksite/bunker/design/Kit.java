package com.blacksite.bunker.design;

/** Room shells, openings, lighting and furniture built on top of {@link Painter}. */
public class Kit extends Painter {

    public Kit(Canvas c) {
        super(c);
    }

    // ---------------------------------------------------------------------------------------------
    // shells
    // ---------------------------------------------------------------------------------------------

    /**
     * Room shell. Walls on the box boundary x1..x2 / z1..z2 from floor {@code f} to ceiling {@code f+h+1}; the
     * interior (f+1..f+h) is air; a spawn-proof slab layer is laid at f+1 when the style defines one.
     */
    public void shell(int x1, int z1, int x2, int z2, int f, int h, Style s) {
        shell(x1, z1, x2, z2, f, h, s, 6);
    }

    public void shell(int x1, int z1, int x2, int z2, int f, int h, Style s, int pillarEvery) {
        int ax = Math.min(x1, x2), bx = Math.max(x1, x2), az = Math.min(z1, z2), bz = Math.max(z1, z2);
        int top = f + h + 1;
        fill(ax, f, az, bx, f, bz, s.floor);
        fill(ax, top, az, bx, top, bz, s.ceiling);
        for (int y = f + 1; y < top; y++) {
            int wb = y == f + 1 ? s.wallLow : (y == top - 1 ? s.wallHigh : s.wall);
            for (int x = ax; x <= bx; x++) {
                set(x, y, az, pick(wb, s, x - ax, pillarEvery, x == ax || x == bx));
                set(x, y, bz, pick(wb, s, x - ax, pillarEvery, x == ax || x == bx));
            }
            for (int z = az + 1; z < bz; z++) {
                set(ax, y, z, pick(wb, s, z - az, pillarEvery, false));
                set(bx, y, z, pick(wb, s, z - az, pillarEvery, false));
            }
        }
        fill(ax + 1, f + 1, az + 1, bx - 1, top - 1, bz - 1, B.A);
        if (s.floorSlab >= 0) {
            fill(ax + 1, f + 1, az + 1, bx - 1, f + 1, bz - 1, s.floorSlab);
        }
    }

    private static int pick(int wb, Style s, int along, int every, boolean corner) {
        if (corner) {
            return s.pillar;
        }
        if (every > 0 && along % every == 0) {
            return s.pillar;
        }
        return wb;
    }

    /** Re-lays the slab walking layer over an interior area (only where the cell is currently air). */
    public void slabFloor(int x1, int z1, int x2, int z2, int f, int slab) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                if (get(x, f + 1, z) == B.A) {
                    set(x, f + 1, z, slab);
                }
            }
        }
    }

    /**
     * Carves an opening through a wall: air from f+1 to f+h, keeping a slab threshold on levels that use slab
     * floors.
     */
    public void opening(int x1, int z1, int x2, int z2, int f, int h, Style s) {
        fill(x1, f + 1, z1, x2, f + h, z2, B.A);
        if (s != null && s.floorSlab >= 0) {
            fill(x1, f + 1, z1, x2, f + 1, z2, s.floorSlab);
        }
    }

    /** Opening framed with trim blocks on both sides and a lintel. {@code alongX}: the wall runs along x. */
    public void framedOpening(int x1, int z1, int x2, int z2, int f, int h, Style s, int frame) {
        int ax = Math.min(x1, x2), bx = Math.max(x1, x2), az = Math.min(z1, z2), bz = Math.max(z1, z2);
        boolean wallAlongX = (bz - az) < (bx - ax) || (az == bz && ax != bx);
        if (wallAlongX) {
            fill(ax - 1, f + 1, az, ax - 1, f + h + 1, bz, frame);
            fill(bx + 1, f + 1, az, bx + 1, f + h + 1, bz, frame);
        } else {
            fill(ax, f + 1, az - 1, bx, f + h + 1, az - 1, frame);
            fill(ax, f + 1, bz + 1, bx, f + h + 1, bz + 1, frame);
        }
        fill(ax, f + h + 1, az, bx, f + h + 1, bz, frame);
        opening(ax, az, bx, bz, f, h, s);
    }

    /** Door in a wall cell; for slab levels the door stands on the floor block (f+1 = lower half). */
    public void doorAt(int x, int z, int f, int id, Dir facing, boolean rightHinge) {
        door(x, f + 1, z, id, facing, rightHinge);
        set(x, f + 3, z, get(x, f + 3, z) == B.A ? B.SB : get(x, f + 3, z));
    }

    // ---------------------------------------------------------------------------------------------
    // lighting
    // ---------------------------------------------------------------------------------------------

    /** Places a light block; lamps get a redstone block on {@code powerSide}. */
    public void light(int x, int y, int z, int lightBlock, Dir powerSide) {
        if (lightBlock == B.LAMP) {
            lamp(x, y, z, powerSide);
        } else {
            set(x, y, z, lightBlock);
        }
    }

    /** Ceiling light grid at height {@code y} (lamps are powered from above). */
    public void ceilingGrid(int x1, int z1, int x2, int z2, int y, int step, int lightBlock) {
        int ax = Math.min(x1, x2), bx = Math.max(x1, x2), az = Math.min(z1, z2), bz = Math.max(z1, z2);
        int w = bx - ax, d = bz - az;
        int ox = ax + (w % step) / 2, oz = az + (d % step) / 2;
        for (int x = ox; x <= bx; x += step) {
            for (int z = oz; z <= bz; z += step) {
                light(x, y, z, lightBlock, Dir.UP);
            }
        }
    }

    /** Wall-mounted lights on the inner face of a wall line (wall cells), every {@code step}. */
    public void wallLightsX(int x1, int x2, int y, int zWall, int step, int lightBlock, Dir intoWall) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x += step) {
            light(x, y, zWall, lightBlock, intoWall);
        }
    }

    public void wallLightsZ(int z1, int z2, int y, int xWall, int step, int lightBlock, Dir intoWall) {
        for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z += step) {
            light(xWall, y, z, lightBlock, intoWall);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // furniture
    // ---------------------------------------------------------------------------------------------

    /** Chair: stairs the sitter faces {@code facing} from. */
    public void chair(int x, int y, int z, int wood, Dir facing) {
        set(x, y, z, B.woodStairs(wood, facing.opposite(), false));
    }

    public void stoneChair(int x, int y, int z, int stairsId, Dir facing) {
        set(x, y, z, B.stairs(stairsId, facing.opposite(), false));
    }

    /** Fence-post table with a pressure plate top. */
    public void postTable(int x, int y, int z, int fenceId) {
        set(x, y, z, B.of(fenceId));
        set(x, y + 1, z, B.of(B.WOOD_PLATE));
    }

    /** Slab-topped table surface (top half slab) on posts at corners. */
    public void slabTable(int x1, int z1, int x2, int z2, int y, int topSlab, int post) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                set(x, y, z, topSlab);
            }
        }
    }

    /** Bed with the foot at (x,y,z) and the head one block toward {@code head}. */
    public void bed(int x, int y, int z, Dir head) {
        set(x, y, z, B.bed(head, false));
        set(x + head.dx, y, z + head.dz, B.bed(head, true));
    }

    /** Pair of chests (double chest) along the axis perpendicular to front. */
    public void doubleChest(int x, int y, int z, Dir front, ItemSpec[] left, ItemSpec[] right) {
        Dir r = front.cw();
        chest(x, y, z, front, left == null ? new ItemSpec[0] : left);
        chest(x + r.dx, y, z + r.dz, front, right == null ? new ItemSpec[0] : right);
    }

    /**
     * Storage wall: a row of double chests two high, separated by a divider block, with a label sign above each
     * pair on the wall. Chests stand at y and y+1 along x1..x2 (or z) at the given line; they face {@code front}.
     * Returns the number of double chests.
     */
    public int storageRow(int a1, int a2, int line, int y, Dir front, int divider, String[] labels, int layers) {
        boolean alongX = !front.alongX();
        int lo = Math.min(a1, a2), hi = Math.max(a1, a2);
        int pairs = 0;
        int a = lo;
        int li = 0;
        while (a + 1 <= hi) {
            for (int k = 0; k < layers; k++) {
                int yy = y + k;
                if (alongX) {
                    set(a, yy, line, B.chest(front));
                    set(a + 1, yy, line, B.chest(front));
                } else {
                    set(line, yy, a, B.chest(front));
                    set(line, yy, a + 1, B.chest(front));
                }
            }
            if (labels != null && li < labels.length && labels[li] != null) {
                // sign on the wall above the pair: wall is behind the chests
                Dir back = front.opposite();
                int sy = y + layers;
                if (alongX) {
                    sign(a, sy, line, front, "§1[STORAGE]", labels[li], "", "");
                    set(a, sy, line + back.dz, get(a, sy, line + back.dz) < 0 ? B.SB : get(a, sy, line + back.dz));
                } else {
                    sign(line, sy, a, front, "§1[STORAGE]", labels[li], "", "");
                }
            }
            li++;
            pairs++;
            a += 2;
            if (a <= hi) {
                for (int k = 0; k < layers; k++) {
                    if (alongX) {
                        set(a, y + k, line, divider);
                    } else {
                        set(line, y + k, a, divider);
                    }
                }
                a++;
            }
        }
        return pairs;
    }

    /** A wall console: base block, control surface on top. Faces {@code facing} (toward the operator). */
    public void console(int x, int y, int z, Dir facing, int variant) {
        int base = variant % 3 == 0 ? B.IRON : variant % 3 == 1 ? B.of(B.NOTE_BLOCK) : B.of(B.DISPENSER, B.face(facing));
        set(x, y, z, base);
        switch (variant % 4) {
            case 0: set(x, y + 1, z, B.of(B.DAYLIGHT_SENSOR)); break;
            case 1: set(x, y + 1, z, B.lever(Dir.UP, (variant & 1) == 0)); break;
            case 2: set(x, y + 1, z, B.comparator(facing)); break;
            default: set(x, y + 1, z, B.button(Dir.UP, false)); break;
        }
    }

    /** Framed picture-like wall screen: black glass with lit lamps behind isn't possible, use clay mosaic. */
    public void screen(int x1, int y1, int z1, int x2, int y2, int z2, int frame, int face) {
        fill(x1, y1, z1, x2, y2, z2, frame);
        int ax = Math.min(x1, x2), bx = Math.max(x1, x2), ay = Math.min(y1, y2), by = Math.max(y1, y2);
        int az = Math.min(z1, z2), bz = Math.max(z1, z2);
        if (ax == bx) {
            fill(ax, ay + 1, az + 1, ax, by - 1, bz - 1, face);
        } else {
            fill(ax + 1, ay + 1, az, bx - 1, by - 1, az, face);
        }
    }

    /** Glass pane partition wall with a frame block on top and bottom. */
    public void partition(int x1, int z1, int x2, int z2, int f, int h, int frame, int pane) {
        fill(x1, f + 1, z1, x2, f + h, z2, pane);
        fill(x1, f + 1, z1, x2, f + 1, z2, frame);
        fill(x1, f + h, z1, x2, f + h, z2, frame);
    }

    /** A "support beam" across the ceiling along x at height y. */
    public void beamX(int x1, int x2, int y, int z, int b) {
        fill(x1, y, z, x2, y, z, b);
    }

    public void beamZ(int z1, int z2, int y, int x, int b) {
        fill(x, y, z1, x, y, z2, b);
    }

    /** Enchanting setup: table at (x,y,z) with 15 bookshelves around (1 block gap), leaving one side open. */
    public void enchantingNook(int x, int y, int z, Dir open) {
        set(x, y, z, B.of(B.ENCHANT_TABLE));
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) != 2 && Math.abs(dz) != 2) {
                    continue;
                }
                // leave the opening side's middle three free
                boolean onOpen = (open == Dir.NORTH && dz == -2 && Math.abs(dx) <= 1)
                        || (open == Dir.SOUTH && dz == 2 && Math.abs(dx) <= 1)
                        || (open == Dir.WEST && dx == -2 && Math.abs(dz) <= 1)
                        || (open == Dir.EAST && dx == 2 && Math.abs(dz) <= 1);
                if (onOpen) {
                    continue;
                }
                set(x + dx, y, z + dz, B.BOOKS);
                set(x + dx, y + 1, z + dz, B.BOOKS);
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    set(x + dx, y, z + dz, B.A);
                    set(x + dx, y + 1, z + dz, B.A);
                }
            }
        }
    }

    /** Hanging light: fence chain from the ceiling down to a light block at y. */
    public void hangingLight(int x, int y, int z, int ceilY, int chain, int lightBlock) {
        for (int yy = y + 1; yy < ceilY; yy++) {
            set(x, yy, z, chain);
        }
        set(x, y, z, lightBlock);
    }

    /** Carpet rectangle (on top of the floor block f: carpet occupies f+1). */
    public void rug(int x1, int z1, int x2, int z2, int f, int carpet) {
        fill(x1, f + 1, z1, x2, f + 1, z2, carpet);
    }
}
