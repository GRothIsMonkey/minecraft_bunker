package com.blacksite.bunker.design;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Final passes: sealing the underground envelope, tile cleanup, spawn-proofing and safety checks. */
public final class Finisher {
    private Finisher() {
    }

    public static void finish(Kit k) {
        Canvas c = k.c;
        List<String> warnings = new ArrayList<String>();
        seal(c);
        cleanTiles(c, warnings);
        lavaSafety(c, warnings);
        anchorSigns(c, warnings);
        spawnProof(c, warnings);
        c.meta.put("warnings", warnings);
        c.unlockAll();
    }

    /**
     * A wall sign or banner pops off on its next block update unless a solid block is behind it. Signs mounted
     * over a tall opening get a one-block lintel copied from the wall above (or below) the gap.
     */
    static void anchorSigns(Canvas c, List<String> warnings) {
        int fixed = 0;
        for (int i = 0; i < c.volume(); i++) {
            int b = c.getRaw(i);
            if (b < 0 || (B.id(b) != B.WALL_SIGN && B.id(b) != B.WALL_BANNER)) {
                continue;
            }
            int x = c.xOf(i), y = c.yOf(i), z = c.zOf(i), d = B.data(b);
            int bx = x + (d == 4 ? 1 : d == 5 ? -1 : 0), bz = z + (d == 2 ? 1 : d == 3 ? -1 : 0);
            if (c.get(bx, y, bz) != B.A) {
                continue;
            }
            int m = -1;
            for (int cand : new int[] {c.get(bx, y + 1, bz), c.get(bx, y - 1, bz)}) {
                if (cand >= 0 && anchor(cand)) {
                    m = cand;
                    break;
                }
            }
            if (m < 0) {
                warnings.add("wall sign without support at " + x + "," + y + "," + z);
                continue;
            }
            c.set(bx, y, bz, m);
            fixed++;
        }
        c.meta.put("signAnchors", fixed);
    }

    private static boolean anchor(int b) {
        int id = B.id(b);
        return B.isOpaqueCube(b) && !B.hasTile(b) && id != B.GLOWSTONE && id != B.SEA_LANTERN && id != B.LAMP_ON
                && id != B.LAMP_OFF && id != B.REDSTONE_BLOCK && id != B.TNT;
    }

    /**
     * Any see-through or passable planned cell below the surface pad gets its unplanned neighbours filled with
     * stone, so natural caves, water, lava and gravel can never leak into the facility.
     */
    static void seal(Canvas c) {
        int[] dx = {1, -1, 0, 0, 0, 0}, dy = {0, 0, 1, -1, 0, 0}, dz = {0, 0, 0, 0, 1, -1};
        List<int[]> add = new ArrayList<int[]>();
        for (int y = c.minY; y <= 66; y++) {
            for (int z = c.minZ; z <= c.maxZ; z++) {
                for (int x = c.minX; x <= c.maxX; x++) {
                    int b = c.get(x, y, z);
                    if (b < 0 || B.isOpaqueCube(b)) {
                        continue;
                    }
                    for (int i = 0; i < 6; i++) {
                        int nx = x + dx[i], ny = y + dy[i], nz = z + dz[i];
                        if (c.inBounds(nx, ny, nz) && !c.isSet(nx, ny, nz)) {
                            add.add(new int[]{nx, ny, nz});
                        }
                    }
                }
            }
        }
        for (int[] p : add) {
            c.setIfUnset(p[0], p[1], p[2], B.STN);
        }
        c.meta.put("sealed", add.size());
    }

    /**
     * Spawn-proofing safety net: any remaining indoor spot where a mob could spawn (a solid-topped block with
     * free space above) that is either below y=40 (slimes ignore light there) or darker than light level 8
     * gets a carpet, which blocks spawning. Floors are already slab or carpet; this mainly covers furniture tops.
     */
    static void spawnProof(Canvas c, List<String> warnings) {
        Analyzer a = new Analyzer(c);
        a.computeLight();
        int added = 0;
        for (int y = c.minY + 1; y <= 66; y++) {
            for (int z = c.minZ; z <= c.maxZ; z++) {
                for (int x = c.minX; x <= c.maxX; x++) {
                    int feet = c.get(x, y, z);
                    if (feet != B.A) {
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
                    int l = a.lightAt(x, y, z);
                    if (y >= 40 && l >= 8) {
                        continue;
                    }
                    c.set(x, y, z, B.carpet(carpetFor(y)));
                    added++;
                }
            }
        }
        c.meta.put("toppers", added);
        // surface pads (courtyard, road, loading bay, guard room...): dark spots get a flush floor light below
        int lights = 0;
        for (int z = c.minZ; z <= c.maxZ; z++) {
            for (int x = c.minX; x <= c.maxX; x++) {
                int y = Layout.SURF + 1;
                int feet = c.get(x, y, z);
                if (feet < 0 || B.hasCollision(feet) || B.isLiquid(feet)) {
                    continue;
                }
                int below = c.get(x, y - 1, z);
                if (below < 0 || !B.solidTop(below) || a.lightAt(x, y, z) >= 8) {
                    continue;
                }
                if (!surfacePad(c, x, z)) {
                    continue;
                }
                int id = B.id(below);
                if (id == B.DOUBLE_SLAB || id == B.STONE_BRICK || id == B.STAINED_CLAY || id == B.STONE || id == B.IRON_BLOCK
                        || id == B.COBBLE) {
                    c.set(x, y - 1, z, B.GLOW);
                    a.addLight(x, y - 1, z, 15);
                    lights++;
                }
            }
        }
        c.meta.put("floorLights", lights);
    }

    static boolean surfacePad(Canvas c, int x, int z) {
        if (x >= -291 && x <= -249 && z >= 111 && z <= 171) {
            return true;
        }
        if (x >= -247 && x <= -222 && z >= 133 && z <= 149) {
            return true;
        }
        for (Canvas.Room r : c.rooms) {
            if (r.level.equals("Surface") && x >= r.x1 && x <= r.x2 && z >= r.z1 && z <= r.z2 && r.y1 <= Layout.SURF
                    && r.y2 >= Layout.SURF + 1) {
                return true;
            }
        }
        return false;
    }

    static int carpetFor(int y) {
        if (y > Layout.L1) {
            return B.GRAY;
        } else if (y > Layout.L2) {
            return B.BROWN;
        } else if (y > Layout.L3) {
            return B.WHITE;
        } else if (y > Layout.L4) {
            return B.GRAY;
        } else if (y > Layout.L5) {
            return B.BLACK;
        }
        return B.BLACK;
    }

    static void cleanTiles(Canvas c, List<String> warnings) {
        Iterator<Map.Entry<Long, TileSpec>> it = c.tiles.entrySet().iterator();
        while (it.hasNext()) {
            TileSpec t = it.next().getValue();
            int b = c.get(t.x, t.y, t.z);
            int id = b < 0 ? -1 : B.id(b);
            boolean ok;
            if (t instanceof TileSpec.SignText) {
                ok = id == B.WALL_SIGN || id == B.SIGN_POST;
            } else if (t instanceof TileSpec.Inventory) {
                ok = id == B.CHEST || id == B.TRAPPED_CHEST || id == B.FURNACE || id == B.LIT_FURNACE
                        || id == B.DISPENSER || id == B.DROPPER || id == B.HOPPER || id == B.BREWING_STAND;
            } else if (t instanceof TileSpec.Head) {
                ok = id == B.SKULL;
            } else if (t instanceof TileSpec.Banner) {
                ok = id == B.WALL_BANNER || id == B.STANDING_BANNER;
            } else {
                ok = false;
            }
            if (!ok) {
                warnings.add("dropped stale tile at " + t.x + "," + t.y + "," + t.z);
                it.remove();
            }
        }
    }

    /** Lava must be fully encased by non-flammable, non-air blocks. */
    static void lavaSafety(Canvas c, List<String> warnings) {
        int[] dx = {1, -1, 0, 0, 0, 0}, dy = {0, 0, 1, -1, 0, 0}, dz = {0, 0, 0, 0, 1, -1};
        for (int y = c.minY; y <= c.maxY; y++) {
            for (int z = c.minZ; z <= c.maxZ; z++) {
                for (int x = c.minX; x <= c.maxX; x++) {
                    int b = c.get(x, y, z);
                    if (b < 0 || !(B.id(b) == B.LAVA || B.id(b) == B.LAVA_FLOW)) {
                        continue;
                    }
                    for (int i = 0; i < 6; i++) {
                        int n = c.get(x + dx[i], y + dy[i], z + dz[i]);
                        if (n < 0 || n == B.A || B.isFlammable(n)) {
                            warnings.add("unsafe lava at " + x + "," + y + "," + z);
                        }
                    }
                }
            }
        }
    }
}
