package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/**
 * Behind the facade: armoured tube tunnel, Checkpoint Alpha, the grand vehicle ramp down to Level 1, Loading
 * Bay B (freight lift head) and the gate guard room.
 */
public final class Entrance {
    private final Kit k;
    private final Style st = Style.security();

    // tube tunnel
    static final int TUBE_X1 = -306, TUBE_X2 = -295;
    static final double TUBE_Y = 72, TUBE_R = 6.5;
    // checkpoint alpha
    static final int CPA_X1 = -316, CPA_X2 = -307, CPA_Z1 = 130, CPA_Z2 = 152;
    // ramp
    static final int RAMP_X1 = -317, RAMP_X2 = -340, RAMP_Z1 = 136, RAMP_Z2 = 146;
    // loading bay
    static final int LB_X1 = -313, LB_X2 = -295, LB_Z1 = 153, LB_Z2 = 169;
    // guard room
    static final int GR_X1 = -307, GR_X2 = -295, GR_Z1 = 112, GR_Z2 = 127;

    public Entrance(Kit k) {
        this.k = k;
    }

    public void build() {
        tube();
        checkpointAlpha();
        ramp();
        loadingBay();
        guardRoom();
    }

    // ------------------------------------------------------------------------------------------
    private void tube() {
        for (int x = TUBE_X1; x <= -293; x++) {
            boolean rib = Math.floorMod(x - TUBE_X1, 4) == 0;
            for (int y = SURF - 1; y <= 80; y++) {
                for (int z = 132; z <= 150; z++) {
                    double d = Math.sqrt((y - TUBE_Y) * (y - TUBE_Y) + (z - 141) * (z - 141));
                    if (x >= TUBE_X2 + 1 && x <= -293) {
                        // inside the facade thickness: the round opening only
                        continue;
                    }
                    if (d <= TUBE_R && y >= SURF + 1) {
                        k.set(x, y, z, B.A);
                    } else if (d <= TUBE_R + 1.2) {
                        int b = rib ? B.IRON : (y <= SURF + 1 ? B.ANDESITE_P : B.SMOOTH);
                        k.set(x, y, z, b);
                    }
                }
            }
            // floor
            for (int z = 136; z <= 146; z++) {
                int b = (z == 136 || z == 146) ? B.clay(B.YELLOW) : B.ANDESITE_P;
                if (z == 137 || z == 145) {
                    b = B.clay(B.BLACK);
                }
                k.set(x, SURF, z, b);
            }
            if (Math.floorMod(x - TUBE_X1, 4) == 2) {
                k.set(x, SURF, 141, B.GLOW);
            }
            if (rib) {
                // lamps in the rib: crown and both shoulders, powered from outside the shell
                k.lamp(x, (int) (TUBE_Y + TUBE_R), 141, Dir.UP);
                k.lamp(x, 74, 135, Dir.NORTH);
                k.lamp(x, 74, 147, Dir.SOUTH);
                k.set(x, SURF + 1, 135, B.IRON);
                k.set(x, SURF + 1, 147, B.IRON);
            }
        }
        // cable trays along the walls
        for (int x = TUBE_X1 + 1; x <= TUBE_X2; x++) {
            if (Math.floorMod(x - TUBE_X1, 4) != 0) {
                k.set(x, 70, 135, B.BARS);
                k.set(x, 70, 147, B.BARS);
            }
        }
        // join facade opening to tube: the round door opening already cut; make sure the floor is continuous
        for (int x = -294; x <= -292; x++) {
            for (int z = 138; z <= 144; z++) {
                k.set(x, SURF, z, B.IRON);
            }
        }
        k.c.room("Armoured Entry Tube", "Surface", TUBE_X1, SURF, 135, -293, 79, 147, -300, SURF + 1, 141);
    }

    // ------------------------------------------------------------------------------------------
    private void checkpointAlpha() {
        int x1 = CPA_X1, x2 = CPA_X2, z1 = CPA_Z1, z2 = CPA_Z2;
        Style s = st.copy();
        s.floorSlab = -1;
        s.floor = B.ANDESITE_P;
        k.shell(x1, z1, x2, z2, SURF, 8, s, 4);
        // connect to the tube through the east wall (round profile)
        for (int y = SURF + 1; y <= 78; y++) {
            for (int z = 135; z <= 147; z++) {
                double d = Math.sqrt((y - TUBE_Y) * (y - TUBE_Y) + (z - 141) * (z - 141));
                if (d <= TUBE_R) {
                    k.set(x2, y, z, B.A);
                }
            }
        }
        // floor pattern: lanes
        for (int x = x1 + 1; x <= x2 - 1; x++) {
            for (int z = z1 + 1; z <= z2 - 1; z++) {
                int b = B.ANDESITE_P;
                if (z == 136 || z == 146) {
                    b = B.clay(B.YELLOW);
                } else if (z >= 137 && z <= 145 && (x == x2 - 2)) {
                    b = B.clay(B.RED); // stop line
                }
                k.set(x, SURF, z, b);
            }
        }
        // guard booths north (z 131..135) and south (z 147..151) with windows onto the lanes
        booth(x1 + 1, x2 - 1, z1 + 1, 135, Dir.SOUTH);
        booth(x1 + 1, x2 - 1, 147, z2 - 1, Dir.NORTH);
        // inner gate: west wall with three turnstile lanes of iron doors at z 139, 141, 143
        for (int z = 136; z <= 146; z++) {
            k.fill(x1, SURF + 1, z, x1, SURF + 8, z, B.SB);
        }
        for (int z = 138; z <= 144; z += 2) {
            k.fill(x1, SURF + 1, z, x1, SURF + 3, z, B.IRON);
        }
        for (int z = 139; z <= 143; z += 2) {
            k.door(x1, SURF + 1, z, B.IRON_DOOR, Dir.WEST, false);
            k.set(x1 + 1, SURF + 1, z, B.of(B.STONE_PLATE));
            k.set(x1 - 1, SURF + 1, z, B.of(B.STONE_PLATE));
            k.c.marker("irondoor", x1, SURF + 1, z, "WEST");
        }
        // retracted blast shutter above the lanes (hazard edge)
        k.fill(x1, SURF + 4, 136, x1, SURF + 4, 146, B.clay(B.BLACK));
        for (int z = 136; z <= 146; z++) {
            k.set(x1 + 1, SURF + 5, z, B.clay(Math.floorMod(z, 2) == 0 ? B.YELLOW : B.BLACK));
            k.set(x1 + 1, SURF + 6, z, B.IRON);
            k.set(x1 + 1, SURF + 7, z, B.IRON);
        }
        // scanner arches over the lanes (iron bars frames) at x = x1+3
        for (int z = 138; z <= 144; z += 2) {
            k.fill(x1 + 3, SURF + 1, z, x1 + 3, SURF + 3, z, B.BARS);
        }
        k.fill(x1 + 3, SURF + 3, 138, x1 + 3, SURF + 3, 144, B.IRON);
        k.set(x1 + 3, SURF + 4, 141, B.LAMP);
        k.set(x1 + 3, SURF + 5, 141, B.RS_BLOCK);
        // lighting: ceiling lamps and wall floodlights
        for (int x = x1 + 2; x <= x2 - 1; x += 3) {
            k.lamp(x, SURF + 9, 138, Dir.UP);
            k.lamp(x, SURF + 9, 144, Dir.UP);
        }
        k.set(x1 + 6, SURF, 141, B.GLOW);
        for (int x = x1 + 1; x <= x2 - 1; x += 3) {
            k.set(x, SURF, 137, B.GLOW);
            k.set(x, SURF, 145, B.GLOW);
        }
        // signs
        k.sign(x1 + 1, SURF + 3, 137, Dir.EAST, "§lCHECKPOINT", "§lALPHA", "Present your", "credentials");
        k.sign(x1 + 1, SURF + 3, 145, Dir.EAST, "§4NO WEAPONS", "§4BEYOND", "§4THIS POINT", "");
        k.sign(x2 - 2, SURF + 4, 136, Dir.SOUTH, "§lEXIT", "Surface", "Courtyard", "§8>>> east");
        k.sign(x1 - 1, SURF + 3, 137, Dir.WEST, "§lRAMP DOWN", "Level 1", "Security &", "Command");
        k.wallBanner(x1 + 1, SURF + 7, 140, Dir.EAST, B.BLACK, "ss", "yellow", "flo", "yellow", "bo", "gray");
        k.wallBanner(x1 + 1, SURF + 7, 142, Dir.EAST, B.BLACK, "ss", "yellow", "flo", "yellow", "bo", "gray");
        k.c.room("Checkpoint Alpha", "Surface", x1, SURF, z1, x2, SURF + 9, z2, x2 - 2, SURF + 1, 141);
    }

    private void booth(int x1, int x2, int z1, int z2, Dir window) {
        // walls: 5-high, glass pane window toward the lanes
        int zw = window == Dir.SOUTH ? z2 : z1;
        int zb = window == Dir.SOUTH ? z1 : z2;
        for (int x = x1; x <= x2; x++) {
            for (int y = SURF + 1; y <= SURF + 4; y++) {
                k.set(x, y, zw, (y == SURF + 2 || y == SURF + 3) && x > x1 && x < x2 ? B.PANE : B.SB);
            }
            k.set(x, SURF + 5, zw, B.SMOOTH);
        }
        k.fill(x1, SURF + 5, z1, x2, SURF + 5, z2, B.SMOOTH);
        for (int x = x1; x <= x2; x += 3) {
            k.set(x, SURF + 5, z1, B.GLOW);
            k.set(x, SURF + 5, z2, B.GLOW);
        }
        // door on the east side (solid frame next to it for the buttons)
        k.set(x2, SURF + 1, zw, B.SB);
        k.fill(x2 - 2, SURF + 1, zw, x2 - 2, SURF + 4, zw, B.SB);
        k.door(x2 - 1, SURF + 1, zw, B.IRON_DOOR, window.opposite(), false);
        k.set(x2 - 2, SURF + 2, zw + window.dz, B.button(window, false));
        k.set(x2 - 2, SURF + 2, zw - window.dz, B.button(window.opposite(), false));
        k.c.marker("irondoor", x2 - 1, SURF + 1, zw, window.opposite().name());
        // counter + consoles behind the window
        int zc = zw - window.dz;
        for (int x = x1 + 1; x <= x2 - 3; x++) {
            k.set(x, SURF + 1, zc, B.stairs(B.QUARTZ_STAIRS, window.opposite(), true));
        }
        k.set(x1 + 1, SURF + 2, zc, B.of(B.DAYLIGHT_SENSOR));
        k.set(x1 + 3, SURF + 2, zc, B.lever(Dir.UP, false));
        k.set(x1 + 4, SURF + 2, zc, B.button(Dir.UP, false));
        k.chair(x1 + 2, SURF + 1, zc - window.dz, B.SPRUCE, window);
        k.chair(x1 + 5, SURF + 1, zc - window.dz, B.SPRUCE, window);
        // back wall: lockers & monitor wall
        int zbi = zb + window.dz;
        k.set(x1, SURF + 3, zbi, B.GLOW);
        k.fill(x1 + 2, SURF + 3, zb, x1 + 5, SURF + 4, zb, B.clay(B.BLACK));
        k.lamp(x1 + 3, SURF + 3, zb, window.opposite());
        k.set(x1 + 1, SURF + 1, zbi, B.chest(window));
        k.c.tile(new TileSpec.Inventory(x1 + 1, SURF + 1, zbi,
                Painter.spread(Loot.checkpointDesk(), 27, x1, SURF, zbi)));
        k.set(x1 + 6, SURF + 1, zbi, B.WORKBENCH_B);
    }

    // ------------------------------------------------------------------------------------------
    /** Walking height of the ramp at x (cell index i = 1..24 from the top). */
    static double rampWalk(int x) {
        int i = -316 - x;
        return 68 - 0.5 * i;
    }

    private void ramp() {
        for (int x = RAMP_X2; x <= RAMP_X1; x++) {
            int i = -316 - x; // 1..24
            double w = 68 - 0.5 * i;
            int base = (int) Math.floor(w) - 1; // full block top at floor(w)
            boolean half = (w % 1.0) != 0;
            int ceil = (int) Math.floor(w) + 6;
            boolean frame = Math.floorMod(i, 4) == 0;
            for (int z = RAMP_Z1; z <= RAMP_Z2; z++) {
                boolean wall = z == RAMP_Z1 || z == RAMP_Z2;
                // column from below the floor to the ceiling
                for (int y = base - 1; y <= ceil; y++) {
                    int b;
                    if (y == ceil) {
                        b = frame ? B.IRON : B.SMOOTH;
                    } else if (wall) {
                        if (frame) {
                            b = B.IRON;
                        } else if (y <= base + 1) {
                            b = B.ANDESITE_P;
                        } else if (y == ceil - 1) {
                            b = B.SMOOTH;
                        } else {
                            b = B.SB;
                        }
                    } else if (y <= base) {
                        b = B.ANDESITE_P;
                        if (y == base && (z == RAMP_Z1 + 1 || z == RAMP_Z2 - 1)) {
                            b = B.clay(B.YELLOW);
                        }
                        if (y == base && z == 141 && Math.floorMod(i, 4) == 2) {
                            b = B.GLOW;
                        }
                    } else {
                        b = B.A;
                    }
                    k.set(x, y, z, b);
                }
                if (!wall && half) {
                    int slab = (z == RAMP_Z1 + 1 || z == RAMP_Z2 - 1) ? B.slab(B.SLAB_SANDSTONE, false)
                            : B.slab(B.SLAB_STONE, false);
                    k.set(x, base + 1, z, slab);
                }
            }
            if (frame) {
                k.lamp(x, ceil, 141, Dir.UP);
                k.lamp(x, ceil - 2, RAMP_Z1, Dir.NORTH);
                k.lamp(x, ceil - 2, RAMP_Z2, Dir.SOUTH);
            }
        }
        // handrails
        for (int x = RAMP_X2 + 1; x <= RAMP_X1; x++) {
            double w = rampWalk(x);
            int y = (int) Math.ceil(w);
            if (Math.floorMod(-316 - x, 4) != 0) {
                k.set(x, y + 1, RAMP_Z1 + 1, B.A);
            }
        }
        k.sign(RAMP_X1, SURF + 3, RAMP_Z1 + 1, Dir.WEST, "Vehicle ramp", "§8max 15 km/h", "", "");
        k.c.room("Grand Descent Ramp", "Surface", RAMP_X2, L1, RAMP_Z1, RAMP_X1, SURF + 6, RAMP_Z2, -328, 63, 141);
    }

    // ------------------------------------------------------------------------------------------
    private void loadingBay() {
        int x1 = LB_X1, x2 = LB_X2, z1 = LB_Z1, z2 = LB_Z2;
        Style s = Style.engineering();
        s.floorSlab = -1;
        s.floor = B.SMOOTH;
        k.shell(x1, z1, x2, z2, SURF, 8, s, 4);
        // vehicle gate through the facade (z 157..163, y 68..74)
        for (int x = -294; x <= x2; x++) {
            for (int z = 157; z <= 163; z++) {
                for (int y = SURF + 1; y <= SURF + 7; y++) {
                    k.set(x, y, z, B.A);
                }
                k.set(x, SURF, z, B.clay(B.GRAY));
            }
        }
        // hazard frame on the facade around the vehicle gate + rolled-up shutter
        for (int y = SURF + 1; y <= SURF + 8; y++) {
            k.set(-291, y, 156, B.clay(Math.floorMod(y, 2) == 0 ? B.YELLOW : B.BLACK));
            k.set(-291, y, 164, B.clay(Math.floorMod(y, 2) == 0 ? B.YELLOW : B.BLACK));
        }
        for (int z = 156; z <= 164; z++) {
            k.set(-291, SURF + 8, z, B.clay(Math.floorMod(z, 2) == 0 ? B.YELLOW : B.BLACK));
        }
        for (int z = 157; z <= 163; z++) {
            k.set(-293, SURF + 7, z, B.of(B.IRON_TRAPDOOR, 3 | 8)); // hinged on the beam at x-1
        }
        k.fill(-294, SURF + 8, 156, -292, SURF + 8, 164, B.IRON);
        k.sign(-291, SURF + 6, 165, Dir.EAST, "§lLOADING BAY B", "Freight lift", "§4Clearance 6m", "");
        // floor: hazard border around the freight lift platform
        for (int x = x1 + 1; x <= x2 - 1; x++) {
            for (int z = z1 + 1; z <= z2 - 1; z++) {
                boolean joint = Math.floorMod(x, 5) == 0 || Math.floorMod(z, 5) == 0;
                k.set(x, SURF, z, joint ? B.SB : B.SMOOTH);
            }
        }
        // freight lift platform (x -310..-302, z 156..164) with hazard edge; shaft below is carved by Cores
        for (int x = FL_X1; x <= FL_X2; x++) {
            for (int z = FL_Z1; z <= FL_Z2; z++) {
                boolean edge = x == FL_X1 || x == FL_X2 || z == FL_Z1 || z == FL_Z2;
                if (edge) {
                    k.set(x, SURF, z, B.clay(Math.floorMod(x + z, 2) == 0 ? B.YELLOW : B.BLACK));
                } else {
                    k.set(x, SURF, z, Math.floorMod(x + z, 2) == 0 ? B.IRON : B.SMOOTH);
                }
            }
        }
        // gantry crane over the platform
        for (int x : new int[]{FL_X1, FL_X2}) {
            for (int z : new int[]{FL_Z1, FL_Z2}) {
                k.fill(x, SURF + 1, z, x, SURF + 7, z, B.IRON);
            }
            k.fill(x, SURF + 7, FL_Z1, x, SURF + 7, FL_Z2, B.IRON);
        }
        k.fill(FL_X1, SURF + 7, 160, FL_X2, SURF + 7, 160, B.IRON);
        k.fill(-306, SURF + 5, 160, -306, SURF + 6, 160, B.BARS);
        k.set(-306, SURF + 4, 160, B.of(B.NETHER_FENCE));
        // lift control post with the freight lift sign (column x=-303,z=157 shared with the hangar)
        k.set(-303, SURF + 1, 156, B.IRON);
        k.set(-303, SURF + 2, 156, B.IRON);
        k.sign(-303, SURF + 2, 157, Dir.SOUTH, "[Lift Down]", "Hangar", "Level 4", "§8freight");
        k.c.marker("lift", -303, SURF + 2, 157, "freight-top");
        // hatch to the service ladder (x=-310 column, z=156 wall side) - ladder attaches to the shaft wall x=-311
        k.set(-310, SURF, 156, B.trapdoor(B.IRON_TRAPDOOR, Dir.EAST, false, true));
        // dock bumpers and crates
        for (int z : new int[]{154, 168}) {
            for (int x = x1 + 2; x <= x1 + 6; x++) {
                k.set(x, SURF + 1, z, B.planks(B.SPRUCE));
            }
        }
        k.set(x1 + 2, SURF + 2, 154, B.log(B.SPRUCE, 1));
        k.set(x1 + 3, SURF + 2, 168, B.of(B.HAY_BALE, 4));
        k.chest(x1 + 1, SURF + 1, 161, Dir.EAST, Loot.loadingBay());
        k.chest(x1 + 1, SURF + 1, 162, Dir.EAST);
        k.set(x1 + 1, SURF + 1, 163, B.WORKBENCH_B);
        // forklift
        int fx = -299, fz = 166;
        k.set(fx, SURF + 1, fz, B.clay(B.YELLOW));
        k.set(fx + 1, SURF + 1, fz, B.clay(B.YELLOW));
        k.set(fx, SURF + 2, fz, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, false));
        k.set(fx - 1, SURF + 1, fz, B.of(B.IRON_TRAPDOOR, 2)); // forks, hinged on the body
        k.set(fx - 1, SURF + 2, fz, B.BARS);
        k.set(fx - 1, SURF + 3, fz, B.BARS);
        // lighting
        for (int x = x1 + 2; x <= x2 - 1; x += 4) {
            k.lamp(x, SURF + 9, z1 + 2, Dir.UP);
            k.lamp(x, SURF + 9, z2 - 2, Dir.UP);
        }
        for (int z = 157; z <= 163; z += 3) {
            k.lamp(-298, SURF + 9, z, Dir.UP);
        }
        k.set(x1 + 3, SURF, 157, B.GLOW);
        k.set(x1 + 5, SURF, 167, B.GLOW);
        k.set(-293, SURF, 139, B.GLOW);
        k.set(-293, SURF, 143, B.GLOW);
        k.set(x1 + 3, SURF, 163, B.GLOW);
        k.c.room("Loading Bay B", "Surface", x1, SURF, z1, x2, SURF + 9, z2, -298, SURF + 1, 160);
    }

    // ------------------------------------------------------------------------------------------
    private void guardRoom() {
        int x1 = GR_X1, x2 = GR_X2, z1 = GR_Z1, z2 = GR_Z2;
        Style s = st.copy();
        k.shell(x1, z1, x2, z2, SURF, 5, s, 4);
        // door through the facade (z 118..119)
        for (int x = -294; x <= -292; x++) {
            k.fill(x, SURF + 1, 118, x, SURF + 3, 119, B.A);
            k.set(x, SURF, 118, B.SMOOTH);
            k.set(x, SURF, 119, B.SMOOTH);
        }
        k.fill(-295, SURF + 1, 118, -295, SURF + 3, 119, B.A);
        k.set(-295, SURF, 118, B.GLOW);
        k.set(-296, SURF, 126, B.GLOW);
        k.set(-292, SURF + 1, 118, B.doorLower(B.IRON_DOOR, Dir.WEST, false));
        k.set(-292, SURF + 2, 118, B.doorUpper(B.IRON_DOOR, false));
        k.set(-292, SURF + 1, 119, B.doorLower(B.IRON_DOOR, Dir.WEST, false));
        k.set(-292, SURF + 2, 119, B.doorUpper(B.IRON_DOOR, true));
        k.set(-291, SURF + 2, 117, B.button(Dir.EAST, false));
        k.set(-293, SURF + 2, 117, B.button(Dir.WEST, false));
        k.set(-293, SURF + 2, 120, B.button(Dir.WEST, false));
        k.c.marker("irondoor", -292, SURF + 1, 118, "WEST");
        k.c.marker("irondoor", -292, SURF + 1, 119, "WEST");
        k.sign(-291, SURF + 3, 120, Dir.EAST, "GUARD POST", "§8Gate Section", "", "");
        // arrow-slit windows through the facade
        for (int z = 122; z <= 125; z++) {
            for (int x = -294; x <= -292; x++) {
                k.set(x, SURF + 2, z, x == -292 ? B.BARS : B.A);
            }
            k.set(-295, SURF + 2, z, B.PANE);
        }
        // furnishing
        int f = SURF;
        k.bed(x1 + 1, f + 1, z1 + 1, Dir.SOUTH);
        k.bed(x1 + 3, f + 1, z1 + 1, Dir.SOUTH);
        k.chest(x1 + 1, f + 1, z2 - 1, Dir.EAST, Loot.guardRoom());
        k.set(x1 + 1, f + 1, z2 - 2, B.WORKBENCH_B);
        k.set(x1 + 1, f + 1, z2 - 3, B.furnace(Dir.EAST));
        k.postTable(x1 + 6, f + 1, 120, B.SPRUCE_FENCE);
        k.chair(x1 + 6, f + 1, 121, B.SPRUCE, Dir.NORTH);
        k.chair(x1 + 6, f + 1, 119, B.SPRUCE, Dir.SOUTH);
        k.set(x2 - 1, f + 1, z1 + 1, B.of(B.DISPENSER, 1));
        k.set(x2 - 1, f + 2, z1 + 1, B.of(B.NETHER_FENCE));
        k.fill(x1 + 1, f + 2, z1 + 1, x1 + 4, f + 2, z1 + 1, B.A);
        k.rug(x1 + 5, z1 + 5, x1 + 8, z1 + 9, f, B.carpet(B.GRAY));
        // ladder up to the facade roof lookout (through a hatch)
        int lx = x2 - 1, lz = z2 - 1;
        k.ladderColumn(lx, f + 1, 85, lz, Dir.WEST);
        for (int y = f + 6; y <= 85; y++) {
            k.set(lx + 1, y, lz, B.SB);
            k.set(lx - 1, y, lz, y > f + 6 ? B.SB : B.A);
            k.set(lx, y, lz - 1, B.SB);
            k.set(lx, y, lz + 1, B.SB);
        }
        // lights
        k.lamp(x1 + 3, f + 6, 120, Dir.UP);
        k.lamp(x1 + 8, f + 6, 116, Dir.UP);
        k.lamp(x1 + 8, f + 6, 124, Dir.UP);
        k.lamp(x1 + 3, f + 6, 115, Dir.UP);
        k.lamp(x1 + 3, f + 6, 125, Dir.UP);
        k.c.room("Gate Guard Room", "Surface", x1, SURF, z1, x2, SURF + 6, z2, x1 + 6, SURF + 1, 122);
        // rooftop lookout on the facade crown
        lookout();
    }

    private void lookout() {
        int y = 86; // lookout deck behind the facade parapet, reached by the guard-room ladder
        int lx = GR_X2 - 1, lz = GR_Z2 - 1;
        // podium under the deck, deck, and clearance above
        for (int x = -300; x <= -294; x++) {
            for (int z = 123; z <= 130; z++) {
                k.fill(x, 79, z, x, y - 1, z, B.SB);
                k.set(x, y, z, B.SMOOTH);
                k.fill(x, y + 1, z, x, y + 4, z, B.A);
            }
        }
        k.fill(lx, 79, lz, lx, y - 1, lz, B.ladder(Dir.WEST));
        k.set(lx, y, lz, B.trapdoor(B.TRAPDOOR, Dir.WEST, false, true));
        for (int x = -300; x <= -294; x++) {
            k.set(x, y + 1, 123, B.COBWALL);
            k.set(x, y + 1, 130, B.COBWALL);
        }
        k.set(-300, y + 1, 124, B.COBWALL);
        k.set(-300, y + 1, 129, B.COBWALL);
        for (int z = 124; z <= 129; z++) {
            k.set(-293, y + 1, z, B.wool(B.BROWN));
        }
        k.set(-294, y + 1, 126, B.dispenser(Dir.EAST));
        k.set(-294, y + 2, 126, B.of(B.NETHER_FENCE));
        k.set(-297, y + 1, 124, B.GLOW);
        k.set(-297, y + 1, 129, B.GLOW);
        k.c.room("Facade Lookout", "Surface", -300, y, 123, -293, y + 3, 130, -296, y + 1, 127);
    }
}
