package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/**
 * Vertical cores shared by all levels: the main switchback stairwell, the personnel lift (plugin sign lift),
 * the freight lift shaft from Loading Bay B to the hangar, and the escape rail from Level 6 to the pump house.
 */
public final class Cores {
    private final Kit k;

    public Cores(Kit k) {
        this.k = k;
    }

    public void build() {
        stairwell();
        personnelLift();
        freightShaft();
        escapeRail();
    }

    // ------------------------------------------------------------------------------------------
    private void stairwell() {
        int x1 = SW_X1, x2 = SW_X2, z1 = SW_Z1, z2 = SW_Z2;
        int yb = L6, yt = L1 + 7;
        // shell
        for (int y = yb; y <= yt; y++) {
            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    boolean edge = x == x1 || x == x2 || z == z1 || z == z2;
                    if (y == yb || y == yt) {
                        k.set(x, y, z, B.SMOOTH);
                    } else if (edge) {
                        boolean corner = (x == x1 || x == x2) && (z == z1 || z == z2);
                        k.set(x, y, z, corner ? B.IRON : (Math.floorMod(y, 5) == 0 ? B.SMOOTH : B.SB));
                    } else {
                        k.set(x, y, z, B.A);
                    }
                }
            }
        }
        // divider wall between the two strips
        for (int y = yb + 1; y < yt; y++) {
            for (int x = -373; x <= -369; x++) {
                k.set(x, y, 149, Math.floorMod(y, 5) == 0 ? B.IRON : B.SB);
            }
        }
        int[] floors = LEVELS;
        for (int li = 0; li < floors.length; li++) {
            int f = floors[li];
            // main landing (east): x -368..-367
            for (int x = -368; x <= -367; x++) {
                for (int z = 146; z <= 152; z++) {
                    k.set(x, f, z, B.SMOOTH);
                    k.set(x, f + 1, z, B.carpet(B.GRAY));
                }
            }
            // north strip flight down to the mid landing (f-5)
            if (f > L6) {
                for (int i = 0; i < 5; i++) {
                    int x = -369 - i, y = f - i;
                    for (int z = 146; z <= 148; z++) {
                        k.set(x, y, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.EAST, false));
                        k.set(x, y - 1, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.WEST, true));
                    }
                }
                int m = f - 5;
                for (int x = -375; x <= -374; x++) {
                    for (int z = 146; z <= 152; z++) {
                        k.set(x, m, z, B.SMOOTH);
                        k.set(x, m + 1, z, B.carpet(B.GRAY));
                    }
                }
                // south strip flight from mid landing down to the next main landing (f-10)
                for (int i = 0; i < 5; i++) {
                    int x = -373 + i, y = m - i;
                    for (int z = 150; z <= 152; z++) {
                        k.set(x, y, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.WEST, false));
                        if (y - 1 > L6) {
                            k.set(x, y - 1, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.EAST, true));
                        }
                    }
                }
                // glowstone at the mid landing
                k.set(x1, m + 3, 150, B.GLOW);
                k.set(x1, m + 3, 148, B.GLOW);
            }
            // lights at the main landing
            k.set(x2, f + 3, 147, B.GLOW);
            k.set(x2, f + 3, 151, B.GLOW);
            // door to the spine (x -368..-367 at z=145), 3 high
            for (int x = -368; x <= -367; x++) {
                k.set(x, f + 1, z1, B.carpet(B.GRAY));
                k.set(x, f + 2, z1, B.A);
                k.set(x, f + 3, z1, B.A);
                k.set(x, f + 4, z1, B.IRON);
            }
            k.set(-369, f + 4, z1, B.IRON);
            k.set(-366, f + 4, z1, B.IRON);
            // level signage on the landing wall and outside
            int lvl = li + 1;
            k.sign(-367, f + 3, 152, Dir.NORTH, "§l=LEVEL " + lvl + "=", LEVEL_NAMES[li].split(" & ")[0],
                    LEVEL_NAMES[li].contains("&") ? "& " + LEVEL_NAMES[li].split(" & ")[1] : "", "§8stairwell A");
            k.sign(-369, f + 3, 144, Dir.NORTH, "§lSTAIRS", "§8all levels", "Level " + lvl, "");
            k.c.room("Main Stairwell (L" + lvl + ")", "Level " + lvl, x1, f, z1, x2, f + 6, z2, -367, f + 1, 149);
        }
        // Level 1: floor over the south strip so nobody falls into the shaft from the top landing
        for (int x = -373; x <= -369; x++) {
            for (int z = 150; z <= 152; z++) {
                k.set(x, L1, z, B.SMOOTH);
                k.set(x, L1 + 1, z, B.carpet(B.GRAY));
            }
        }
        k.fill(-373, L1 + 1, 149, -369, L1 + 1, 149, B.SB);
    }

    // ------------------------------------------------------------------------------------------
    private void personnelLift() {
        int x1 = PL_X1, x2 = PL_X2, z1 = PL_Z1, z2 = PL_Z2;
        for (int li = 0; li < LEVELS.length; li++) {
            int f = LEVELS[li];
            int lvl = li + 1;
            for (int y = f; y <= f + 5; y++) {
                for (int x = x1; x <= x2; x++) {
                    for (int z = z1; z <= z2; z++) {
                        boolean edge = x == x1 || x == x2 || z == z1 || z == z2;
                        if (y == f || y == f + 5) {
                            k.set(x, y, z, B.IRON);
                        } else if (edge) {
                            k.set(x, y, z, (x == x1 || x == x2) && (z == z1 || z == z2) ? B.IRON : B.QUARTZ);
                        } else {
                            k.set(x, y, z, B.A);
                        }
                    }
                }
            }
            k.fill(x1 + 1, f + 1, z1 + 1, x2 - 1, f + 1, z2 - 1, B.carpet(B.BLACK));
            k.set(-379, f + 4, 147, B.GLOW);
            k.set(-379, f + 5, 147, B.IRON);
            // doorway
            k.set(-379, f + 1, z1, B.carpet(B.BLACK));
            k.set(-379, f + 2, z1, B.A);
            k.set(-380, f + 1, z1, B.IRON);
            k.set(-378, f + 1, z1, B.IRON);
            // lift signs on the back wall
            if (lvl > 1) {
                k.sign(-380, f + 2, 148, Dir.NORTH, "[Lift Up]", "to Level " + (lvl - 1), "§8right-click", "");
            } else {
                k.sign(-380, f + 2, 148, Dir.NORTH, "[Lift]", "Level 1", "Security", "§8top floor");
            }
            if (lvl < 6) {
                k.sign(-378, f + 2, 148, Dir.NORTH, "[Lift Down]", "to Level " + (lvl + 1), "§8right-click", "");
            } else {
                k.sign(-378, f + 2, 148, Dir.NORTH, "[Lift]", "Level 6", "Containment", "§8bottom");
            }
            k.sign(-379, f + 3, 148, Dir.NORTH, "§lLEVEL " + lvl, LEVEL_NAMES[li].split(" & ")[0], "", "");
            k.sign(-379, f + 3, 144, Dir.NORTH, "§1PERSONNEL", "§1LIFT", "", "");
            k.c.marker("lift", -380, f + 2, 148, "pl-up-L" + lvl);
            k.c.marker("lift", -378, f + 2, 148, "pl-down-L" + lvl);
            k.c.room("Personnel Lift (L" + lvl + ")", "Level " + lvl, x1, f, z1, x2, f + 5, z2, -379, f + 1, 147);
            if (lvl > 1) {
                k.c.liftLinks.add(new int[]{-379, LEVELS[li - 1] + 1, 147, -379, f + 1, 147});
            }
        }
    }

    // ------------------------------------------------------------------------------------------
    private void freightShaft() {
        int top = SURF - 1; // shaft interior up to 66; platform at 67
        int bottom = L4 + 14; // hangar ceiling y=39
        for (int y = bottom; y <= top; y++) {
            for (int x = FL_X1; x <= FL_X2; x++) {
                for (int z = FL_Z1; z <= FL_Z2; z++) {
                    boolean edge = x == FL_X1 || x == FL_X2 || z == FL_Z1 || z == FL_Z2;
                    boolean corner = (x == FL_X1 || x == FL_X2) && (z == FL_Z1 || z == FL_Z2);
                    if (edge) {
                        k.set(x, y, z, corner ? B.IRON : (Math.floorMod(y, 6) == 0 ? B.clay(B.YELLOW) : B.SB));
                    } else {
                        k.set(x, y, z, B.A);
                    }
                }
            }
            // guide rails
            if (y % 2 == 0) {
                k.set(FL_X1 + 1, y, FL_Z1 + 1, B.BARS);
                k.set(FL_X2 - 1, y, FL_Z2 - 1, B.BARS);
                k.set(FL_X2 - 1, y, FL_Z1 + 1, B.BARS);
                k.set(FL_X1 + 1, y, FL_Z2 - 1, B.BARS);
            }
            if (Math.floorMod(y, 6) == 3) {
                k.set(FL_X2, y, 160, B.GLOW);
                k.set(FL_X1, y, 162, B.GLOW);
            }
        }
        // service ladder from the hangar floor up to the platform hatch (column x=-310, z=156)
        k.set(-310, L4, 156, B.GLOW);
        for (int y = L4 + 1; y <= top; y++) {
            k.set(-310, y, 156, B.ladder(Dir.EAST));
            if (y <= bottom) {
                k.set(-311, y, 156, B.IRON);
            }
        }
        k.set(-310, bottom, 156, B.ladder(Dir.EAST));
        k.c.marker("lift", -303, L4 + 2, 157, "freight-bottom");
        k.c.liftLinks.add(new int[]{-303, SURF + 1, 158, -303, L4 + 1, 158});
    }

    // ------------------------------------------------------------------------------------------
    /** Director's private egress: rail from the L6 station (x=-404) east along z=88, then up to the pump house. */
    private void escapeRail() {
        int z = ESC_Z, f = L6;
        int xs = -404, xe = -300;
        // flat tunnel: walkway z=87, rail z=88, walls z=86/89, floor f, ceiling f+4
        for (int x = xs; x <= xe; x++) {
            for (int y = f; y <= f + 4 && x >= -398; y++) {
                for (int zz = z - 2; zz <= z + 1; zz++) {
                    boolean wall = zz == z - 2 || zz == z + 1 || y == f || y == f + 4;
                    k.set(x, y, zz, wall ? (y == f ? B.SB : B.SB_CRACKED) : B.A);
                }
            }
            if (x >= -398) {
                k.set(x, f + 1, z - 1, B.slab(B.SLAB_COBBLE, false));
            }
            boolean powered = Math.floorMod(x - xs, 8) == 0;
            if (powered) {
                k.set(x, f, z, B.RS_BLOCK);
                k.set(x, f + 1, z, B.poweredRail(1, true));
                if (x >= -398) {
                    k.set(x, f + 3, z + 1, B.LAMP);
                    k.set(x, f + 3, z + 2, B.RS_BLOCK);
                }
            } else {
                k.set(x, f + 1, z, B.rail(1));
            }
        }
        k.set(xs, f + 1, z, B.poweredRail(1, true)); // launcher next to the bumper
        k.set(xs - 1, f + 1, z, B.IRON);
        // incline: ascending rail at (xe+kk, f+kk) for kk=1..62, stairs alongside at z-1, then flat at SURF+1
        int kkMax = SURF - f; // 62
        for (int kk = 1; kk <= kkMax; kk++) {
            int x = xe + kk;
            int base = f - 1 + kk; // support level for rail and stair
            for (int y = base; y <= base + 4; y++) {
                for (int zz = z - 2; zz <= z + 1; zz++) {
                    boolean shell = zz == z - 2 || zz == z + 1 || y == base + 4;
                    boolean insideHut = y >= SURF && x >= Surface.PH_X1;
                    if (shell && insideHut) {
                        continue; // the pump-house floor/room takes over here
                    }
                    k.set(x, y, zz, shell ? B.SB : B.A);
                }
            }
            boolean rs = kk % 8 == 0;
            k.set(x, base, z, rs ? B.RS_BLOCK : B.SB);
            k.set(x, base, z - 1, B.SB);
            k.set(x, base + 1, z, B.poweredRail(2, true));
            k.set(x, base + 1, z - 1, B.stairs(B.STONE_BRICK_STAIRS, Dir.EAST, false));
            if (kk % 4 == 0 && !(base + 2 >= SURF && x >= Surface.PH_X1)) {
                k.lamp(x, base + 2, z + 1, Dir.SOUTH);
            }
        }
        // top station inside the pump house: flat rails and bumper
        int ty = SURF + 1;
        int tx = xe + kkMax + 1; // -237
        k.set(tx, ty, z, B.rail(1));
        k.set(tx + 1, ty, z, B.rail(1));
        k.set(tx + 2, ty, z, B.poweredRail(1, true));
        k.set(tx + 2, ty - 1, z, B.RS_BLOCK);
        k.set(tx, ty, z - 1, B.A);
        k.set(tx - 2, ty, z + 1, B.COBWALL);
        k.set(tx - 3, ty, z + 1, B.COBWALL);
        k.set(tx - 4, ty, z + 1, B.COBWALL);
        k.set(tx - 3, ty, z - 2, B.COBWALL);
        k.set(tx - 4, ty, z - 2, B.COBWALL);
        k.minecart(tx + 1, ty, z);
        k.sign(tx + 2, ty + 2, z, Dir.WEST, "\u00a74EMERGENCY", "EGRESS RAIL", "to Level 6", "\u00a78ride west");
        k.c.marker("rail-top", tx + 1, ty, z, "");
        k.c.room("Escape Rail Incline", "Secret", xe, f, z - 2, tx - 5, SURF - 2, z + 1, xe + 30, f + 31, z - 1);
    }
}
