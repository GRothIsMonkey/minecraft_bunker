package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/**
 * The reactor chamber: a 37-block tall shaft through Levels 6 to 3 with the glowing core, the beacon beam rising
 * from its heart, catwalk rings at Levels 5, 4 and 3, a coolant pool in the pit, sealed lava heat columns,
 * control rods and observation windows.
 */
public final class Reactor {
    private final Kit k;
    static final int X1 = RC_X1, X2 = RC_X2, Z1 = RC_Z1, Z2 = RC_Z2, Y1 = RC_Y1, Y2 = RC_Y2;
    static final double CR = 4.6; // core radius

    public Reactor(Kit k) {
        this.k = k;
    }

    public void build() {
        chamber();
        pit();
        core();
        catwalk(L5 + 1, "Level 5");
        catwalk(L4 + 1, "Level 4");
        topRing();
        ladder();
        heatColumns();
        accesses();
        lights();
        k.c.room("Reactor Chamber (pit)", "Level 6", X1, Y1, Z1, X2, Y2, Z2, -330, L6 + 1, 120);
        k.c.room("Reactor Catwalk L5", "Level 5", X1, L5, Z1, X2, L5 + 6, Z2, -330, L5 + 2, 118);
        k.c.room("Reactor Catwalk L4", "Level 4", X1, L4, Z1, X2, L4 + 6, Z2, -330, L4 + 2, 118);
        k.c.room("Reactor Top Ring L3", "Level 3", X1, L3, Z1, X2, L3 + 6, Z2, -330, L3 + 2, 119);
    }

    private void chamber() {
        for (int y = Y1; y <= Y2; y++) {
            for (int x = X1; x <= X2; x++) {
                for (int z = Z1; z <= Z2; z++) {
                    boolean edge = x == X1 || x == X2 || z == Z1 || z == Z2;
                    if (y == Y1) {
                        k.set(x, y, z, B.NB);
                    } else if (y == Y2) {
                        k.set(x, y, z, B.SB);
                    } else if (edge) {
                        boolean rib = (x - X1) % 5 == 0 || (z - Z1) % 5 == 0;
                        int b;
                        if (y <= Y1 + 3) {
                            b = B.NB;
                        } else if (y == L5 || y == L4 || y == L3) {
                            b = B.clay(Math.floorMod(x + z + y, 2) == 0 ? B.YELLOW : B.BLACK);
                        } else if (rib) {
                            b = B.IRON;
                        } else {
                            b = Canvas.rand(x, y, z, 9) < 0.1 ? B.SB_CRACKED : B.SB;
                        }
                        k.set(x, y, z, b);
                    } else {
                        k.set(x, y, z, B.A);
                    }
                }
            }
        }
    }

    private void pit() {
        int f = Y1;
        for (int x = X1 + 1; x <= X2 - 1; x++) {
            for (int z = Z1 + 1; z <= Z2 - 1; z++) {
                double d = Math.sqrt((x - CX) * (x - CX) + (z - CZ) * (z - CZ));
                if (d > 5.6 && d <= 8.6) {
                    // coolant pool: water over glowstone, one block deep
                    k.set(x, f - 1, z, B.GLOW);
                    k.set(x, f, z, B.of(B.WATER));
                } else if (d > 8.6) {
                    k.set(x, f, z, B.NB);
                    k.set(x, f + 1, z, (Math.floorMod(x + z, 2) == 0) ? B.slab(B.SLAB_NETHER, false)
                            : B.slab(B.SLAB_STONEBRICK, false));
                }
            }
        }
        // pool rim: hazard ring
        k.ringXZ(CX, f + 1, CZ, 8.6, 9.5, B.slab(B.SLAB_SANDSTONE, false));
        k.ringXZ(CX, f + 2, CZ, 8.9, 9.5, B.of(B.NETHER_FENCE));
        for (int a = 0; a < 4; a++) {
            // gaps in the railing
            int x = (int) Math.round(CX + 9.2 * Math.cos(a * Math.PI / 2 + Math.PI / 4));
            int z = (int) Math.round(CZ + 9.2 * Math.sin(a * Math.PI / 2 + Math.PI / 4));
            k.set(x, f + 2, z, B.A);
        }
    }

    private void core() {
        for (int y = Y1 + 1; y <= 36; y++) {
            for (int x = CX - 5; x <= CX + 5; x++) {
                for (int z = CZ - 5; z <= CZ + 5; z++) {
                    double d = Math.sqrt((x - CX) * (x - CX) + (z - CZ) * (z - CZ));
                    if (d > CR + 0.5) {
                        continue;
                    }
                    if (x == CX && z == CZ) {
                        continue; // locked beam column / beacon
                    }
                    int b;
                    if (d > CR - 1.0) {
                        // outer shell
                        double ang = Math.atan2(z - CZ, x - CX);
                        boolean column = Math.floorMod((int) Math.round(ang * 8 / Math.PI), 2) == 0;
                        if (y <= Y1 + 2 || y % 5 == 0) {
                            b = B.IRON;
                        } else if (column) {
                            b = B.IRON;
                        } else {
                            b = B.sglass(B.LIGHT_BLUE);
                        }
                    } else if (d > 1.9) {
                        b = (y % 3 == 0) ? B.GLOW : B.SEA;
                    } else {
                        b = B.GLS;
                    }
                    if (y <= Conduit.BEACON_Y - 1 && Math.abs(x - CX) <= 2 && Math.abs(z - CZ) <= 2) {
                        continue; // beacon pyramid (locked)
                    }
                    if (y == Conduit.BEACON_Y && Math.abs(x - CX) <= 1 && Math.abs(z - CZ) <= 1) {
                        b = B.GLS;
                    }
                    k.set(x, y, z, b);
                }
            }
        }
        // base plinth
        k.ringXZ(CX, Y1 + 1, CZ, CR + 0.5, 5.6, B.NB);
        // cap / dome
        for (int y = 37; y <= 40; y++) {
            double r = CR + 0.5 - (y - 37) * 1.3;
            for (int x = CX - 5; x <= CX + 5; x++) {
                for (int z = CZ - 5; z <= CZ + 5; z++) {
                    if (x == CX && z == CZ) {
                        continue;
                    }
                    double d = Math.sqrt((x - CX) * (x - CX) + (z - CZ) * (z - CZ));
                    if (d <= r) {
                        k.set(x, y, z, y == 40 ? B.SEA : (d > r - 1.1 ? B.IRON : (y == 37 ? B.GLOW : B.IRON)));
                    }
                }
            }
        }
        // control rods from the ceiling down into the dome
        for (int a = 0; a < 4; a++) {
            int x = CX + (a % 2 == 0 ? 2 : -2), z = CZ + (a < 2 ? 2 : -2);
            for (int y = 39; y < Y2; y++) {
                if (k.get(x, y, z) == B.A || k.get(x, y, z) < 0) {
                    k.set(x, y, z, B.BARS);
                }
            }
        }
        // coolant pipes: glass tubes from the core to the walls at y 21 and 31 (north/south)
        for (int y : new int[]{21, 31}) {
            for (int z = Z1 + 1; z <= CZ - 5; z++) {
                k.set(CX - 3, y, z, B.GLS);
                k.set(CX + 3, y, z, B.GLS);
            }
            for (int z = CZ + 5; z <= Z2 - 1; z++) {
                k.set(CX - 3, y, z, B.GLS);
                k.set(CX + 3, y, z, B.GLS);
            }
        }
        k.c.marker("reactor-core", CX, 20, CZ, "");
    }

    /** Catwalk ring (bottom slabs, walking surface y+0.5) with four radial bridges. */
    private void catwalk(int y, String lvl) {
        k.ringXZ(CX, y, CZ, 6.0, 9.2, B.slab(B.SLAB_STONEBRICK, false));
        k.ringXZ(CX, y, CZ, 9.2, 9.9, B.slab(B.SLAB_SANDSTONE, false));
        // bridges
        for (int d = 9; d <= 14; d++) {
            for (int w = -1; w <= 1; w++) {
                k.set(CX - d, y, CZ + w, B.slab(B.SLAB_STONEBRICK, false)); // west
                k.set(CX + d, y, CZ + w, B.slab(B.SLAB_STONEBRICK, false)); // east
                k.set(CX + w, y, CZ - d, B.slab(B.SLAB_STONEBRICK, false)); // north
                k.set(CX + w, y, CZ + d, B.slab(B.SLAB_STONEBRICK, false)); // south
            }
        }
        // railings: inner edge and bridge sides
        for (int x = X1 + 1; x <= X2 - 1; x++) {
            for (int z = Z1 + 1; z <= Z2 - 1; z++) {
                int b = k.get(x, y, z);
                if (b < 0 || !B.isHalfSlab(b)) {
                    continue;
                }
                for (Dir dd : Dir.HORIZONTAL) {
                    int nx = x + dd.dx, nz = z + dd.dz;
                    int nb = k.get(nx, y, nz);
                    if (nb == B.A && k.get(nx, y + 1, nz) == B.A) {
                        double d = Math.sqrt((nx - CX) * (nx - CX) + (nz - CZ) * (nz - CZ));
                        if (d > CR + 0.6) {
                            k.set(nx, y + 1, nz, B.of(B.NETHER_FENCE));
                        }
                    }
                }
            }
        }
        // control booth on the east ring
        int bx = CX + 7, bz = CZ;
        k.set(bx, y, bz - 1, B.slab(B.SLAB_STONEBRICK, false));
        k.set(bx + 1, y + 1, bz - 1, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, true));
        k.set(bx + 1, y + 2, bz - 1, B.comparator(Dir.EAST));
        k.set(bx + 1, y + 1, bz + 1, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, true));
        k.set(bx + 1, y + 2, bz + 1, B.of(B.DAYLIGHT_SENSOR));
        k.set(X2 - 1, y + 3, CZ, B.GLOW);
        k.set(X1 + 1, y + 3, CZ, B.GLOW);
        k.set(CX, y + 3, Z1 + 1, B.GLOW);
        k.set(CX, y + 3, Z2 - 1, B.GLOW);
        // wall bridges end at solid wall: make sure the bridge row under the slab is supported visually
        for (int w = -1; w <= 1; w++) {
            k.set(X1 + 1, y - 1, CZ + w, B.IRON);
            k.set(X2 - 1, y - 1, CZ + w, B.IRON);
            k.set(CX + w, y - 1, Z1 + 1, B.IRON);
            k.set(CX + w, y - 1, Z2 - 1, B.IRON);
        }
        k.sign(CX + 1, y + 2, Z2 - 1, Dir.NORTH, "§eCATWALK", lvl.toUpperCase(), "§4Keep to the", "§4railings");
    }

    /** Level 3 maintenance ring around the dome, reached from the observation gallery. */
    private void topRing() {
        int y = L3 + 1;
        k.ringXZ(CX, y, CZ, 5.0, 7.5, B.slab(B.SLAB_STONEBRICK, false));
        for (int z = CZ + 7; z <= Z2 - 1; z++) {
            for (int w = -1; w <= 1; w++) {
                k.set(CX + w, y, z, B.slab(B.SLAB_STONEBRICK, false));
            }
            k.set(CX - 2, y + 1, z, B.of(B.NETHER_FENCE));
            k.set(CX + 2, y + 1, z, B.of(B.NETHER_FENCE));
        }
        k.ringXZ(CX, y + 1, CZ, 7.5, 8.3, B.of(B.NETHER_FENCE));
        for (int w = -1; w <= 1; w++) {
            k.set(CX + w, y + 1, CZ + 8, B.A);
            k.set(CX + w, y + 1, CZ + 7, B.A);
        }
        for (int w = -1; w <= 1; w++) {
            k.set(CX + w, y - 1, Z2 - 1, B.IRON);
        }
    }

    /** Service ladder on the north wall from the pit to the top ring. */
    private void ladder() {
        int x = CX, z = Z1 + 1;
        k.set(x, Y1, z, B.GLOW);
        for (int y = Y1 + 1; y <= L3 + 3; y++) {
            k.set(x, y, z, B.ladder(Dir.SOUTH));
        }
        // the north bridges reach the wall around the ladder: open the ladder cell through each deck
        k.set(x - 1, L3 + 1, z, B.slab(B.SLAB_STONEBRICK, false));
        k.set(x + 1, L3 + 1, z, B.slab(B.SLAB_STONEBRICK, false));
        for (int zz = z; zz <= CZ - 7; zz++) {
            for (int w = -1; w <= 1; w++) {
                if (w == 0 && zz == z) {
                    continue;
                }
                k.set(x + w, L3 + 1, zz, B.slab(B.SLAB_STONEBRICK, false));
            }
        }
    }

    /** Four sealed lava heat-exchange columns in the corners, behind glass (fire-safe: fully encased). */
    private void heatColumns() {
        int[][] corners = {{X1 + 1, Z1 + 1, 1, 1}, {X2 - 1, Z1 + 1, -1, 1}, {X1 + 1, Z2 - 1, 1, -1}, {X2 - 1, Z2 - 1, -1, -1}};
        for (int[] c : corners) {
            int x = c[0], z = c[1], sx = c[2], sz = c[3];
            if (x == CX) {
                continue;
            }
            for (int y = L5 + 2; y <= L3 - 2; y++) {
                k.set(x, y, z, B.of(B.LAVA));
                k.set(x + sx, y, z, B.GLS);
                k.set(x, y, z + sz, B.GLS);
                k.set(x + sx, y, z + sz, B.GLS);
            }
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int xx = x + dx, zz = z + dz;
                    if (xx < X1 || xx > X2 || zz < Z1 || zz > Z2) {
                        continue;
                    }
                    k.set(xx, L3 - 1, zz, B.IRON);
                    k.set(xx, L3, zz, B.IRON);
                    k.set(xx, L5 + 1, zz, B.IRON);
                }
            }
            k.c.marker("lava", x, L5 + 2, z, "sealed heat column");
        }
    }

    private void accesses() {
        // L6: from the OMEGA lab through the west wall (x=-345) into the pit
        k.ironDoor(X1, L6 + 1, 115, Dir.EAST, true);
        // L5: reactor control room windows (west wall) and door; plus the south access corridor from the spine
        for (int z = 105; z <= 121; z++) {
            for (int y = L5 + 2; y <= L5 + 4; y++) {
                k.set(X1, y, z, (z - 105) % 4 == 0 ? B.IRON : B.GLS);
            }
        }
        for (int y = L5 + 1; y <= L5 + 4; y++) {
            k.set(X1, y, CZ - 1, B.IRON);
            k.set(X1, y, CZ + 1, B.IRON);
        }
        k.set(X1, L5 + 3, CZ, B.IRON);
        k.ironDoor(X1, L5 + 1, CZ, Dir.EAST, true);
        Style rs = Style.reactor();
        k.shell(-334, 123, -326, 137, L5, 5, rs, 0);
        k.opening(-332, 137, -328, 137, L5, 3, rs);
        k.ironDoubleDoor(-331, L5 + 1, 123, Dir.NORTH);
        for (int z = 124; z <= 136; z++) {
            k.set(-330, L5 + 1, z, B.slab(B.SLAB_SANDSTONE, false));
            if (z % 4 == 0) {
                k.lamp(-330, L5 + 6, z, Dir.UP);
            }
        }
        k.sign(-332, L5 + 3, 136, Dir.NORTH, "§e§lREACTOR", "§eMain access", "§4Authorized", "§4only");
        k.c.room("Reactor Access Corridor", "Level 5", -334, L5, 123, -326, L5 + 6, 137, -330, L5 + 1, 130);
        // L4: from engineering control (x=-346) through the chamber wall
        for (int x = -346; x <= -345; x++) {
            for (int z = 119; z <= 120; z++) {
                k.set(x, L4 + 1, z, B.slab(B.SLAB_STONEBRICK, false));
                k.set(x, L4 + 2, z, B.A);
                k.set(x, L4 + 3, z, B.A);
            }
        }
        k.set(-346, L4 + 4, 119, B.IRON);
        k.set(-346, L4 + 4, 120, B.IRON);
        k.set(-345, L4 + 4, 119, B.IRON);
        k.set(-345, L4 + 4, 120, B.IRON);
        // bridge extension from the west catwalk bridge to the L4 doorway (z 119..120)
        for (int x = X1 + 1; x <= CX - 9; x++) {
            for (int z = CZ + 2; z <= 120; z++) {
                k.set(x, L4 + 1, z, B.slab(B.SLAB_STONEBRICK, false));
            }
        }
        // L3: gallery windows (south wall z=123) and door onto the top ring bridge
        for (int x = X1 + 1; x <= X2 - 1; x++) {
            for (int y = L3 + 2; y <= L3 + 5; y++) {
                k.set(x, y, Z2, (x - X1) % 5 == 0 ? B.IRON : B.GLS);
            }
        }
        k.ironDoubleDoor(CX, L3 + 1, Z2, Dir.NORTH);
        k.set(CX - 1, L3 + 2, Z2, B.IRON);
        k.set(CX + 2, L3 + 2, Z2, B.IRON);
        k.set(CX, L3 + 3, Z2, B.IRON);
        k.set(CX + 1, L3 + 3, Z2, B.IRON);
        for (int x = CX - 2; x <= CX + 3; x++) {
            k.set(x, L3 + 1, Z2 + 2, Style.science().floorSlab);
        }
        // L5 south bridge meets the access corridor door (x -331..-330 at z=123)
    }

    private void lights() {
        // ceiling lamps (redstone blocks above in the buffer under Level 2)
        k.ceilingGrid(X1 + 2, Z1 + 2, X2 - 2, Z2 - 2, Y2, 6, B.LAMP);
        // wall glowstone strips
        for (int y = Y1 + 4; y < Y2; y += 4) {
            for (int z = Z1 + 3; z <= Z2 - 3; z += 6) {
                if (k.get(X1, y, z) != B.GLS && !B.isDoor(k.get(X1, y, z))) {
                    k.set(X1, y, z, B.GLOW);
                }
                if (!B.isDoor(k.get(X2, y, z))) {
                    k.set(X2, y, z, B.GLOW);
                }
            }
            for (int x = X1 + 3; x <= X2 - 3; x += 6) {
                if (k.get(x, y, Z1) != B.GLS) {
                    k.set(x, y, Z1, B.GLOW);
                }
            }
        }
        // hazard signage
        k.sign(X1 + 1, L6 + 3, 116, Dir.EAST, "§4§lDANGER", "§4Reactor pit", "Coolant loop", "§8no swimming");
    }
}
