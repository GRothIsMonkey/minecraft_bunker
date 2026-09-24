package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/**
 * Surface complex: artificial berm (the "hill" the facility is dug into), monumental facade with the round
 * SITE-07 vault door, secure courtyard, perimeter wall, gate towers, approach road, rooftop vents and the
 * disguised pump house that hides the emergency escape rail.
 */
public final class Surface {
    private final Kit k;

    // courtyard
    static final int CY_X1 = -291, CY_X2 = -249, CY_Z1 = 111, CY_Z2 = 171;
    // facade
    static final int FA_X = -292; // front face
    static final int FA_Z1 = 111, FA_Z2 = 171;
    // round door
    static final double DOOR_Y = 73, DOOR_Z = 141, DOOR_R = 5.5;
    // berm
    static final int BE_X1 = -376, BE_X2 = -295, BE_Z1 = 99, BE_Z2 = 183;
    // road
    static final int RD_X1 = -247, RD_X2 = -222;
    // pump house (escape exit)
    static final int PH_X1 = -242, PH_X2 = -234, PH_Z1 = 84, PH_Z2 = 92;

    public Surface(Kit k) {
        this.k = k;
    }

    public void build() {
        berm();
        courtyard();
        road();
        perimeter();
        gateTowers();
        facade();
        roundDoor();
        rooftop();
        pumpHouse();
    }

    // ------------------------------------------------------------------------------------------
    /** Height of the berm surface (top grass block y) at x,z, or -1 outside. */
    static int bermTop(int x, int z) {
        if (x < BE_X1 || x > BE_X2 || z < BE_Z1 || z > BE_Z2) {
            return -1;
        }
        // distance to the west / north / south edges (east edge is the facade)
        double dw = x - BE_X1;
        double dn = z - BE_Z1;
        double ds = BE_Z2 - z;
        double d = Math.min(dw, Math.min(dn, ds));
        // soften the corners
        double cw = Math.min(dw, 24), cn = Math.min(Math.min(dn, ds), 24);
        double corner = 24 - Math.sqrt((24 - cw) * (24 - cw) + (24 - cn) * (24 - cn));
        d = Math.min(d, Math.max(corner, 0));
        double t = Math.min(1.0, d / 22.0);
        double s = t * t * (3 - 2 * t); // smoothstep
        // gentle undulation so the hill looks natural
        double wob = 1.2 * Math.sin(x * 0.21) * Math.cos(z * 0.17);
        int h = (int) Math.round(67 + s * 18 + wob * s);
        return Math.max(67, h);
    }

    private void berm() {
        for (int x = BE_X1; x <= BE_X2; x++) {
            for (int z = BE_Z1; z <= BE_Z2; z++) {
                int top = bermTop(x, z);
                if (top < 0) {
                    continue;
                }
                for (int y = 58; y <= top; y++) {
                    int b;
                    if (y == top) {
                        b = B.GRASS_B;
                    } else if (y >= top - 3) {
                        b = B.DIRT_B;
                    } else {
                        b = Canvas.rand(x, y, z, 5) < 0.15 ? B.ANDESITE : B.STN;
                    }
                    k.set(x, y, z, b);
                }
                // clear anything natural above the hill (trees, overhangs) for a clean silhouette
                for (int y = top + 1; y <= top + 12 && y <= MAX_Y; y++) {
                    k.set(x, y, z, B.A);
                }
            }
        }
        // a few trees with persistent leaves, and some grass tufts
        int[][] trees = {{-362, 118}, {-356, 168}, {-345, 176}, {-370, 150}, {-338, 104}, {-366, 132},
                {-322, 178}, {-352, 108}};
        for (int[] t : trees) {
            tree(t[0], bermTop(t[0], t[1]) + 1, t[1], (t[0] + t[1]) % 3 == 0 ? B.SPRUCE : B.OAK);
        }
        for (int x = BE_X1 + 2; x <= BE_X2 - 4; x++) {
            for (int z = BE_Z1 + 2; z <= BE_Z2 - 2; z++) {
                double r = Canvas.rand(x, 0, z, 77);
                int top = bermTop(x, z);
                if (top < 0 || k.get(x, top + 1, z) != B.A) {
                    continue;
                }
                if (r < 0.07) {
                    k.set(x, top + 1, z, B.of(B.TALL_GRASS, 1));
                } else if (r < 0.075) {
                    k.set(x, top + 1, z, B.of(B.FLOWER, (int) (Canvas.rand(x, 1, z, 3) * 9)));
                } else if (r < 0.08) {
                    k.set(x, top + 1, z, B.of(B.DANDELION));
                }
            }
        }
    }

    private void tree(int x, int y, int z, int wood) {
        int h = 5 + (int) (Canvas.rand(x, y, z, 11) * 2);
        int leaf = wood == B.SPRUCE ? B.of(B.LEAVES, 1 | 4) : B.of(B.LEAVES, 4);
        if (wood == B.SPRUCE) {
            for (int dy = 1; dy <= h + 1; dy++) {
                int r = dy >= h ? 0 : (dy % 2 == 0 ? 2 : 1);
                if (dy < 2) {
                    continue;
                }
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        if (Math.abs(dx) + Math.abs(dz) <= r + 1) {
                            k.set(x + dx, y + dy, z + dz, leaf);
                        }
                    }
                }
            }
        } else {
            for (int dy = h - 3; dy <= h + 1; dy++) {
                int r = dy >= h ? 1 : 2;
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        if (r == 2 && Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                            continue;
                        }
                        k.set(x + dx, y + dy, z + dz, leaf);
                    }
                }
            }
        }
        for (int dy = 0; dy < h; dy++) {
            k.set(x, y + dy, z, B.log(wood, 0));
        }
        k.set(x, y - 1, z, B.DIRT_B);
    }

    // ------------------------------------------------------------------------------------------
    private void courtyard() {
        // foundation and clearing
        k.fill(CY_X1, 58, CY_Z1, CY_X2, 66, CY_Z2, B.STN);
        k.fill(CY_X1, 68, CY_Z1, CY_X2, 95, CY_Z2, B.A);
        // base paving: smooth stone "concrete" with stone-brick expansion joints
        for (int x = CY_X1; x <= CY_X2; x++) {
            for (int z = CY_Z1; z <= CY_Z2; z++) {
                boolean joint = Math.floorMod(x + 291, 7) == 0 || Math.floorMod(z - 111, 7) == 0;
                k.set(x, SURF, z, joint ? B.SB : B.SMOOTH);
            }
        }
        // main road lane from gate to blast door: z 137..145
        for (int x = CY_X1; x <= CY_X2; x++) {
            for (int z = 136; z <= 146; z++) {
                int b = B.clay(B.GRAY);
                if (z == 136 || z == 146) {
                    b = B.clay(B.WHITE);
                } else if (z == 141 && Math.floorMod(x, 4) < 2) {
                    b = B.clay(B.YELLOW);
                }
                k.set(x, SURF, z, b);
            }
        }
        // loading lane south to the vehicle gate (z 157..163)
        for (int x = -291; x <= -262; x++) {
            for (int z = 147; z <= 165; z++) {
                boolean inLane = z >= 155 && z <= 165 || x >= -270 && z >= 147;
                if (!inLane) {
                    continue;
                }
                k.set(x, SURF, z, B.clay(B.GRAY));
            }
        }
        // loading apron with hazard stripes in front of the vehicle gate
        k.hazardArea(-291, SURF, 155, -289, 165);
        // helipad (north), centre -270,124
        k.diskXZ(-270, SURF, 124, 7.5, B.clay(B.GRAY));
        k.ringXZ(-270, SURF, 124, 6.2, 7.4, B.clay(B.WHITE));
        for (int x = -273; x <= -267; x++) {
            for (int z = 121; z <= 127; z++) {
                boolean h = x == -273 || x == -267 || z == 124;
                if (h) {
                    k.set(x, SURF, z, B.clay(B.WHITE));
                }
            }
        }
        // helipad edge lights and courtyard runway lights (flush glowstone)
        for (int a = 0; a < 16; a++) {
            double ang = a * Math.PI / 8;
            int x = (int) Math.round(-270 + 8.3 * Math.cos(ang));
            int z = (int) Math.round(124 + 8.3 * Math.sin(ang));
            k.set(x, SURF, z, B.GLOW);
        }
        for (int x = -288; x <= -252; x += 6) {
            k.set(x, SURF, 136, B.GLOW);
            k.set(x, SURF, 146, B.GLOW);
            k.set(x + 3, SURF, 141, B.GLOW);
        }
        int[][] extra = {{-289, 118}, {-289, 133}, {-289, 149}, {-251, 132}, {-251, 150}, {-251, 113}, {-251, 169},
                {-289, 113}, {-289, 169}, {-270, 113}, {-270, 169}, {-285, 131}, {-285, 151}};
        for (int[] e : extra) {
            k.set(e[0], SURF, e[1], B.GLOW);
        }
        // floor light grid across the rest of the courtyard (spawn-proof at night)
        for (int x = -286; x <= -252; x += 6) {
            for (int z = 114; z <= 169; z += 6) {
                if (z >= 134 && z <= 148) {
                    continue;
                }
                int cur = k.get(x, SURF, z);
                if (cur != B.GLOW) {
                    k.set(x, SURF, z, B.GLOW);
                }
            }
        }
        // parked armoured truck in the loading area
        truck(-276, 158);
        // cargo crates
        crates(-266, 166);
        crates(-282, 168);
        // fuel tanks
        fuelTank(-256, 164);
        fuelTank(-256, 158);
        // lamp posts along the road
        for (int x = -286; x <= -256; x += 10) {
            lampPost(x, 134);
            lampPost(x, 148);
        }
        // sandbag guard post near the facade (north) and generator shed (south-east corner)
        sandbags(-287, 130);
        sandbags(-287, 150);
        generatorShed();
        // warning signs
        k.signPost(-289, 68, 134, Dir.EAST, "§4§lWARNING", "Blast door zone", "Keep lane clear", "");
        k.signPost(-289, 68, 148, Dir.EAST, "§lLOADING BAY B", "Freight lift", "to Hangar L4", "§8>>>");
        k.signPost(-278, 68, 118, Dir.EAST, "§lHELIPAD", "Rotor hazard", "Stay clear", "");
    }

    private void truck(int x, int z) {
        // military truck facing east, 9 long (x), 4 wide (z), wheels of coal blocks
        int y = SURF + 1;
        for (int dx = 0; dx < 9; dx++) {
            for (int dz = 0; dz < 4; dz++) {
                boolean wheel = (dx == 1 || dx == 2 || dx == 6 || dx == 7) && (dz == 0 || dz == 3);
                k.set(x + dx, y, z + dz, wheel ? B.COAL : B.clay(B.GREEN));
            }
        }
        // cargo box (rear, west part)
        k.fill(x, y + 1, z, x + 5, y + 3, z + 3, B.wool(B.GREEN));
        k.fill(x, y + 1, z + 1, x, y + 2, z + 2, B.of(B.TRAPDOOR, 2 | 4)); // rear doors on the box
        // cab (east part)
        k.fill(x + 6, y + 1, z, x + 8, y + 2, z + 3, B.clay(B.GREEN));
        k.fill(x + 8, y + 2, z, x + 8, y + 2, z + 3, B.PANE);
        k.fill(x + 7, y + 2, z, x + 7, y + 2, z, B.PANE);
        k.fill(x + 7, y + 2, z + 3, x + 7, y + 2, z + 3, B.PANE);
        k.fill(x + 6, y + 3, z, x + 8, y + 3, z + 3, B.slab(B.SLAB_STONE, false));
        k.set(x + 9, y, z, B.of(B.TRAPDOOR, 3 | 4));
        k.set(x + 9, y, z + 3, B.of(B.TRAPDOOR, 3 | 4));
        k.set(x + 9, y + 1, z + 1, B.GLOW);
        k.set(x + 9, y + 1, z + 2, B.GLOW);
    }

    private void crates(int x, int z) {
        int y = SURF + 1;
        k.set(x, y, z, B.planks(B.SPRUCE));
        k.set(x + 1, y, z, B.log(B.SPRUCE, 1));
        k.set(x, y + 1, z, B.log(B.OAK, 12));
        k.set(x - 1, y, z, B.of(B.HAY_BALE, 4));
        k.set(x, y, z - 1, B.planks(B.OAK));
    }

    private void fuelTank(int x, int z) {
        int y = SURF + 1;
        for (int dz = 0; dz < 5; dz++) {
            k.set(x, y, z + dz, B.slab(B.SLAB_STONE, false));
            k.set(x, y + 1, z + dz, B.IRON);
            k.set(x + 1, y + 1, z + dz, B.IRON);
            k.set(x, y + 2, z + dz, B.IRON);
            k.set(x + 1, y + 2, z + dz, B.IRON);
        }
        k.set(x + 1, y, z, B.slab(B.SLAB_STONE, false));
        k.set(x + 1, y, z + 4, B.slab(B.SLAB_STONE, false));
        k.set(x, y + 3, z + 2, B.of(B.CAULDRON));
        k.sign(x - 1, y + 1, z + 2, Dir.WEST, "§4FLAMMABLE", "JP-8 FUEL", "No smoking", "");
    }

    private void lampPost(int x, int z) {
        for (int y = SURF + 1; y <= SURF + 5; y++) {
            k.set(x, y, z, B.COBWALL);
        }
        k.set(x, SURF + 6, z, B.GLOW);
        k.set(x, SURF + 7, z, B.slab(B.SLAB_STONE, false));
    }

    private void sandbags(int x, int z) {
        int y = SURF + 1;
        for (int dz = -2; dz <= 2; dz++) {
            k.set(x + 2, y, z + dz, B.wool(B.BROWN));
            k.set(x + 2, y + 1, z + dz, B.of(B.SLAB, B.SLAB_SANDSTONE));
        }
        k.set(x + 1, y, z - 2, B.wool(B.BROWN));
        k.set(x + 1, y, z + 2, B.wool(B.BROWN));
        k.set(x + 1, y, z, B.dispenser(Dir.EAST));
        k.set(x + 1, y + 1, z, B.of(B.NETHER_FENCE));
    }

    private void generatorShed() {
        int x1 = -262, x2 = -252, z1 = 113, z2 = 119;
        k.fill(x1, SURF + 1, z1, x2, SURF + 4, z2, B.COB);
        k.fill(x1 + 1, SURF + 1, z1 + 1, x2 - 1, SURF + 3, z2 - 1, B.A);
        k.fill(x1, SURF + 5, z1, x2, SURF + 5, z2, B.slab(B.SLAB_COBBLE, false));
        k.fill(x1 + 1, SURF + 4, z1 + 1, x2 - 1, SURF + 4, z2 - 1, B.planks(B.SPRUCE));
        k.set(x1 + 5, SURF + 4, z1 + 3, B.GLOW);
        k.set(x1 + 8, SURF + 4, z1 + 2, B.GLOW);
        k.door(x1 + 5, SURF + 1, z2, B.SPRUCE_DOOR, Dir.NORTH, false);
        k.fill(x1 + 1, SURF + 2, z1, x1 + 3, SURF + 2, z1, B.BARS);
        k.fill(x2 - 3, SURF + 2, z1, x2 - 1, SURF + 2, z1, B.BARS);
        // generator: lit furnaces with iron casing
        for (int x = x1 + 1; x <= x1 + 3; x++) {
            k.set(x, SURF + 1, z1 + 1, B.furnace(Dir.SOUTH));
            k.set(x, SURF + 2, z1 + 1, x == x1 + 2 ? B.GLOW : B.IRON);
        }
        k.set(x2 - 1, SURF + 1, z1 + 1, B.of(B.NOTE_BLOCK));
        k.set(x2 - 2, SURF + 1, z1 + 1, B.of(B.CAULDRON, 3));
        k.set(x2 - 1, SURF + 1, z2 - 1, B.WORKBENCH_B);
        k.chest(x2 - 1, SURF + 1, z2 - 2, Dir.WEST, Loot.generatorShed());
        k.sign(x1 + 5, SURF + 3, z2 + 1, Dir.SOUTH, "GENERATOR 2", "§4Authorized", "§4only", "");
        k.c.room("Generator Shed", "Surface", x1, SURF, z1, x2, SURF + 5, z2, x1 + 5, SURF + 1, z1 + 3);
    }

    // ------------------------------------------------------------------------------------------
    private void perimeter() {
        // north & south walls from the facade to the east wall, east wall with gate
        for (int x = CY_X1; x <= CY_X2 + 1; x++) {
            wallColumn(x, CY_Z1 - 1, x);
            wallColumn(x, CY_Z2 + 1, x);
        }
        for (int z = CY_Z1 - 1; z <= CY_Z2 + 1; z++) {
            if (z >= 136 && z <= 146) {
                continue; // gate opening
            }
            wallColumn(CY_X2 + 1, z, z);
        }
        // gate posts (huge pillars) and the slid-open gate (iron bar panel behind the north wall section)
        for (int z : new int[]{134, 135, 147, 148}) {
            k.fill(CY_X2 + 1, SURF + 1, z, CY_X2 + 1, SURF + 8, z, B.SB);
            k.set(CY_X2 + 1, SURF + 9, z, B.SB_CHISELED);
        }
        k.fill(CY_X2, SURF + 1, 124, CY_X2, SURF + 5, 134, B.BARS);
        k.fill(CY_X2, SURF + 6, 124, CY_X2, SURF + 6, 134, B.IRON);
        // gate rail track in the pavement
        for (int z = 124; z <= 146; z++) {
            k.set(CY_X2 + 1, SURF, z, B.IRON);
        }
        // gate header beam with warning lamps
        k.fill(CY_X2 + 1, SURF + 8, 136, CY_X2 + 1, SURF + 8, 146, B.IRON);
        k.fill(CY_X2 + 1, SURF + 9, 134, CY_X2 + 1, SURF + 9, 148, B.SB);
        for (int z = 137; z <= 145; z += 2) {
            k.lamp(CY_X2 + 1, SURF + 7, z, Dir.UP);
        }
        k.fill(CY_X2 + 1, SURF + 9, 137, CY_X2 + 1, SURF + 9, 145, B.RS_BLOCK);
        k.fill(CY_X2 + 1, SURF + 10, 136, CY_X2 + 1, SURF + 10, 146, B.slab(B.SLAB_STONEBRICK, false));
        k.sign(CY_X2 + 2, SURF + 4, 133, Dir.EAST, "§4§lRESTRICTED", "§4§lAREA", "Site 7", "Authorized only");
        k.sign(CY_X2 + 2, SURF + 4, 149, Dir.EAST, "§4WARNING", "Use of deadly", "force", "authorized");
    }

    private void wallColumn(int x, int z, int seed) {
        for (int y = 58; y <= SURF; y++) {
            k.set(x, y, z, B.STN);
        }
        for (int y = SURF + 1; y <= SURF + 4; y++) {
            int b = y == SURF + 1 ? B.SB_CHISELED : B.SB;
            if (y == SURF + 3 && Math.floorMod(seed, 6) == 3) {
                b = B.BARS;
            } else if (Canvas.rand(x, y, z, 3) < 0.12) {
                b = B.SB_CRACKED;
            }
            k.set(x, y, z, b);
        }
        if (Math.floorMod(seed, 5) == 0) {
            k.set(x, SURF + 2, z, B.GLOW);
        }
        k.set(x, SURF + 5, z, B.COBWALL);
        k.set(x, SURF + 6, z, Math.floorMod(seed, 2) == 0 ? B.WEB_B : B.A);
        if (Math.floorMod(seed, 8) == 0) {
            k.set(x, SURF + 6, z, B.COBWALL);
            k.set(x, SURF + 7, z, B.GLOW);
        }
    }

    private void gateTowers() {
        tower(-254, 128, 134); // north tower x -254..-248, z 128..134
        tower(-254, 148, 154);
    }

    private void tower(int x1, int z1, int z2) {
        int x2 = x1 + 6;
        int top = SURF + 13;
        k.fill(x1, 58, z1, x2, SURF, z2, B.STN);
        // base shaft
        for (int y = SURF + 1; y <= top; y++) {
            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    boolean edge = x == x1 || x == x2 || z == z1 || z == z2;
                    boolean corner = (x == x1 || x == x2) && (z == z1 || z == z2);
                    if (edge) {
                        k.set(x, y, z, corner ? B.SB_CHISELED : (Canvas.rand(x, y, z, 7) < 0.1 ? B.SB_CRACKED : B.SB));
                    } else {
                        k.set(x, y, z, B.A);
                    }
                }
            }
        }
        k.fill(x1 + 1, SURF, z1 + 1, x2 - 1, SURF, z2 - 1, B.SMOOTH);
        // door facing the courtyard (west side)
        k.door(x1, SURF + 1, (z1 + z2) / 2, B.IRON_DOOR, Dir.EAST, false);
        k.set(x1 - 1, SURF + 2, (z1 + z2) / 2 + 1, B.button(Dir.WEST, false));
        k.set(x1 + 1, SURF + 2, (z1 + z2) / 2 + 1, B.button(Dir.EAST, false));
        k.c.marker("irondoor", x1, SURF + 1, (z1 + z2) / 2, "EAST");
        // interior ladder on the east wall up to the cabin
        k.ladderColumn(x2 - 1, SURF + 1, top, (z1 + z2) / 2, Dir.WEST);
        k.set(x2 - 1, SURF + 1, (z1 + z2) / 2, B.ladder(Dir.WEST));
        k.set(x1 + 2, top - 2, z1 + 1, B.GLOW);
        k.set(x1 + 2, SURF + 4, z1 + 1, B.GLOW);
        k.set(x1 + 2, SURF + 8, z2 - 1, B.GLOW);
        // cabin floor with ladder hatch
        k.fill(x1, top, z1, x2, top, z2, B.SMOOTH);
        k.set(x2 - 1, top, (z1 + z2) / 2, B.ladder(Dir.WEST));
        // cabin (overhanging by 1)
        int cy = top + 1;
        k.fill(x1 - 1, top, z1 - 1, x2 + 1, top, z2 + 1, B.SMOOTH);
        for (int y = cy; y <= cy + 3; y++) {
            for (int x = x1 - 1; x <= x2 + 1; x++) {
                for (int z = z1 - 1; z <= z2 + 1; z++) {
                    boolean edge = x == x1 - 1 || x == x2 + 1 || z == z1 - 1 || z == z2 + 1;
                    boolean corner = (x == x1 - 1 || x == x2 + 1) && (z == z1 - 1 || z == z2 + 1);
                    if (!edge) {
                        k.set(x, y, z, B.A);
                    } else if (corner) {
                        k.set(x, y, z, B.log(B.DARK_OAK, 0));
                    } else if (y == cy) {
                        k.set(x, y, z, B.SB);
                    } else if (y == cy + 3) {
                        k.set(x, y, z, B.SB);
                    } else {
                        k.set(x, y, z, B.PANE);
                    }
                }
            }
        }
        k.fill(x1 - 2, cy + 4, z1 - 2, x2 + 2, cy + 4, z2 + 2, B.slab(B.SLAB_STONEBRICK, false));
        k.fill(x1 - 1, cy + 4, z1 - 1, x2 + 1, cy + 4, z2 + 1, B.SMOOTH);
        k.set(x2 - 1, top, (z1 + z2) / 2, B.ladder(Dir.WEST));
        k.set(x1 + 3, cy + 5, (z1 + z2) / 2, B.of(B.DAYLIGHT_SENSOR));
        for (int y = cy + 5; y <= cy + 9; y++) {
            k.set(x1 + 1, y, z1 + 1, B.BARS);
        }
        k.lamp(x1 + 1, cy + 10, z1 + 1, Dir.DOWN);
        k.set(x1 + 1, cy + 11, z1 + 1, B.slab(B.SLAB_STONE, false));
        // interior of cabin: chair, console, searchlight
        k.set(x1 + 3, cy + 3, (z1 + z2) / 2, B.GLOW);
        k.set(x1, top, z1, B.GLOW);
        k.set(x2, top, z1, B.GLOW);
        k.set(x1, top, z2, B.GLOW);
        k.set(x2, top, z2, B.GLOW);
        k.set(x2, cy, (z1 + z2) / 2 - 2, B.dispenser(Dir.EAST));
        k.set(x2, cy + 1, (z1 + z2) / 2 - 2, B.of(B.NETHER_FENCE));
        k.stoneChair(x1 + 2, cy, (z1 + z2) / 2, B.SPRUCE_STAIRS, Dir.EAST);
        k.set(x1 + 1, cy, z1 + 1, B.chest(Dir.EAST));
        k.c.tile(new TileSpec.Inventory(x1 + 1, cy, z1 + 1, Painter.spread(Loot.guardTower(), 27, x1, cy, z1)));
        k.set(x1 + 1, cy, z2 - 1, B.of(B.JUKEBOX));
        k.c.room(z1 < 141 ? "North Gate Tower" : "South Gate Tower", "Surface", x1, SURF, z1, x2, cy + 4, z2, x1 + 2,
                cy, (z1 + z2) / 2 + 1);
    }

    private void road() {
        for (int x = RD_X1; x <= RD_X2; x++) {
            for (int z = 133; z <= 149; z++) {
                for (int y = 58; y < SURF; y++) {
                    k.set(x, y, z, B.STN);
                }
                k.fill(x, SURF + 1, z, x, SURF + 12, z, B.A);
                int b;
                if (z < 136 || z > 146) {
                    b = (z == 133 || z == 149) ? B.SB : B.GRASS_B;
                } else if (z == 136 || z == 146) {
                    b = B.clay(B.WHITE);
                } else if (z == 141 && Math.floorMod(x, 4) < 2) {
                    b = B.clay(B.YELLOW);
                } else {
                    b = B.clay(B.GRAY);
                }
                k.set(x, SURF, z, b);
            }
        }
        // road lights (flush) and tank traps on the verges
        for (int x = RD_X1 + 2; x <= RD_X2; x += 6) {
            k.set(x, SURF, 136, B.GLOW);
            k.set(x, SURF, 146, B.GLOW);
            k.set(x + 3, SURF, 141, B.GLOW);
        }
        for (int x : new int[]{-223, -233}) {
            k.set(x, SURF, 134, B.GLOW);
            k.set(x, SURF, 148, B.GLOW);
        }
        for (int x = RD_X1 + 4; x <= RD_X2 - 2; x += 7) {
            tankTrap(x, 134);
            tankTrap(x + 3, 148);
        }
        // warning signs on posts facing incoming traffic (east)
        k.signPost(-224, SURF + 1, 134, Dir.EAST, "§4§lSTOP", "Restricted area", "beyond this", "point");
        k.signPost(-224, SURF + 1, 148, Dir.EAST, "§4NO ENTRY", "Military", "installation", "§8Site 7");
        k.signPost(-236, SURF + 1, 148, Dir.EAST, "§lSITE 7", "Checkpoint", "ahead 100m", "Slow down");
        k.signPost(-236, SURF + 1, 134, Dir.EAST, "§4Photography", "§4prohibited", "", "");
    }

    private void tankTrap(int x, int z) {
        int y = SURF + 1;
        k.set(x, y, z, B.of(B.NETHER_FENCE));
        k.set(x, y + 1, z, B.BARS);
        k.set(x - 1, y, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.EAST, false));
        k.set(x + 1, y, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.WEST, false));
    }

    // ------------------------------------------------------------------------------------------
    /** Facade top height profile. */
    static int facadeTop(int z) {
        int dz = Math.abs(z - 141);
        if (dz <= 12) {
            return SURF + 21; // central block y 88
        }
        if (dz <= 24) {
            return SURF + 15; // flanks y 82
        }
        return SURF + 10; // wings y 77
    }

    private void facade() {
        for (int z = FA_Z1; z <= FA_Z2; z++) {
            int top = facadeTop(z);
            int dz = Math.abs(z - 141);
            for (int x = FA_X - 2; x <= FA_X; x++) {
                for (int y = 58; y <= top; y++) {
                    int b;
                    double r = Canvas.rand(x, y, z, 21);
                    if (y <= SURF) {
                        b = B.STN;
                    } else if (y == SURF + 1) {
                        b = B.SB_CHISELED;
                    } else if (y == top) {
                        b = B.SMOOTH;
                    } else if ((y - SURF) % 6 == 0) {
                        b = B.SMOOTH; // horizontal bands
                    } else {
                        b = r < 0.12 ? B.SB_CRACKED : (r < 0.16 ? B.SB_MOSSY : B.SB);
                    }
                    k.set(x, y, z, b);
                }
                // parapet
                k.set(x, top + 1, z, x == FA_X ? B.COBWALL : B.A);
            }
            // clear space in front of the facade above the courtyard (already) and above the facade
            for (int y = top + 2; y <= top + 12; y++) {
                for (int x = FA_X - 2; x <= FA_X; x++) {
                    k.set(x, y, z, B.A);
                }
            }
            // pilasters every 6 blocks: protruding iron-trimmed columns on the front face
            if (dz % 6 == 0 && dz != 0 && dz <= 30) {
                for (int y = SURF + 1; y <= top - 1; y++) {
                    k.set(FA_X + 1, y, z, (y - SURF) % 6 == 0 ? B.IRON : B.SB_CHISELED);
                }
                k.set(FA_X + 1, top, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.WEST, false));
            }
            // step-back buttress stairs where the height changes
            if (dz == 13 || dz == 25) {
                int prevTop = facadeTop(z < 141 ? z + 1 : z - 1);
                for (int y = top + 1; y <= prevTop; y++) {
                    for (int x = FA_X - 2; x <= FA_X; x++) {
                        k.set(x, y, z, B.stairs(B.STONE_BRICK_STAIRS, z < 141 ? Dir.SOUTH : Dir.NORTH, false));
                    }
                }
            }
        }
        // large lettering "SITE-7" on the crown, iron on a black band
        k.fill(FA_X + 1, SURF + 14, 129, FA_X + 1, SURF + 20, 153, B.clay(B.BLACK));
        k.text("SITE-7", FA_X + 1, SURF + 19, 152, Dir.NORTH, B.IRON);
        k.fill(FA_X + 1, SURF + 13, 129, FA_X + 1, SURF + 13, 153, B.IRON);
        k.fill(FA_X + 1, SURF + 21, 129, FA_X + 1, SURF + 21, 153, B.slab(B.SLAB_STONEBRICK, false));
        // warning beacons on the crown (lamps powered from behind)
        for (int z : new int[]{130, 152}) {
            k.lamp(FA_X + 1, SURF + 21, z, Dir.WEST);
        }
        for (int z = 118; z <= 164; z += 46) {
            k.lamp(FA_X + 1, SURF + 9, z, Dir.WEST);
        }
        // floodlights on iron brackets lighting the courtyard
        for (int z : new int[]{121, 133, 149, 161}) {
            k.set(FA_X + 1, SURF + 11, z, B.BARS);
            k.set(FA_X + 2, SURF + 11, z, B.GLOW);
            k.set(FA_X + 2, SURF + 12, z, B.slab(B.SLAB_STONE, false));
        }
        // guard door (north wing) and vehicle gate (south wing) are cut by the Entrance builder
    }

    /** The famous round door: frame ring in the facade and the rolled-aside cog door leaf. */
    private void roundDoor() {
        // frame ring around the opening, protruding one block
        for (int y = 64; y <= 82; y++) {
            for (int z = 130; z <= 152; z++) {
                double d = Math.sqrt((y - DOOR_Y) * (y - DOOR_Y) + (z - DOOR_Z) * (z - DOOR_Z));
                if (d > DOOR_R && d <= DOOR_R + 2.2 && y > SURF) {
                    double ang = Math.atan2(y - DOOR_Y, z - DOOR_Z);
                    boolean bolt = Math.floorMod((int) Math.round(ang * 16 / Math.PI), 2) == 0;
                    k.set(FA_X + 1, y, z, d > DOOR_R + 1.2 ? (bolt ? B.IRON : B.SMOOTH) : B.IRON);
                    if (d > DOOR_R + 1.2 && bolt) {
                        k.set(FA_X + 2, y, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.WEST, y > DOOR_Y));
                    }
                }
                if (d <= DOOR_R && y > SURF) {
                    for (int x = FA_X - 2; x <= FA_X + 2; x++) {
                        k.set(x, y, z, B.A);
                    }
                }
            }
        }
        // hazard ring painted on the facade just outside the frame
        for (int y = 64; y <= 84; y++) {
            for (int z = 128; z <= 154; z++) {
                double d = Math.sqrt((y - DOOR_Y) * (y - DOOR_Y) + (z - DOOR_Z) * (z - DOOR_Z));
                if (d > DOOR_R + 2.2 && d <= DOOR_R + 3.2 && y > SURF + 1) {
                    k.set(FA_X, y, z, B.clay(Math.floorMod(y + z, 4) < 2 ? B.YELLOW : B.BLACK));
                }
            }
        }
        // threshold
        for (int z = 138; z <= 144; z++) {
            for (int x = FA_X - 2; x <= FA_X + 2; x++) {
                k.set(x, SURF, z, B.IRON);
            }
        }
        // door leaf: rolled aside to the north, standing on its track in front of the facade
        double lz = 126.5, ly = DOOR_Y;
        double R = DOOR_R + 0.6;
        for (int y = SURF + 1; y <= 81; y++) {
            for (int z = 116; z <= 138; z++) {
                double d = Math.sqrt((y - ly) * (y - ly) + (z - lz) * (z - lz));
                if (d > R + 0.9) {
                    continue;
                }
                double ang = Math.atan2(y - ly, z - lz);
                boolean tooth = Math.floorMod((int) Math.floor(ang * 12 / Math.PI), 2) == 0;
                if (d > R && !tooth) {
                    continue;
                }
                int b;
                if (d > R - 1.0) {
                    b = B.IRON;
                } else if (d > R - 1.8) {
                    b = B.clay(Math.floorMod(y + z, 3) == 0 ? B.BLACK : B.YELLOW);
                } else if (d < 1.6) {
                    b = B.IRON;
                } else {
                    b = B.clay(B.SILVER);
                }
                k.set(FA_X + 1, y, z, b);
                k.set(FA_X + 2, y, z, d > R - 1.0 || d < 1.6 ? B.IRON : B.clay(B.SILVER));
            }
        }
        // "07" stencilled on the leaf
        k.text("07", FA_X + 2, (int) ly + 2, 129, Dir.NORTH, B.clay(B.BLACK));
        // hub bolt and hydraulic arm
        k.set(FA_X + 3, (int) ly, (int) Math.floor(lz), B.IRON);
        k.set(FA_X + 3, (int) ly, (int) Math.ceil(lz), B.IRON);
        k.fill(FA_X + 3, (int) ly, (int) Math.ceil(lz) + 1, FA_X + 3, (int) ly, 134, B.of(B.NETHER_FENCE));
        // rail track in the pavement
        for (int z = 116; z <= 146; z++) {
            k.set(FA_X + 1, SURF, z, B.IRON);
            k.set(FA_X + 2, SURF, z, B.IRON);
        }
        k.fill(FA_X + 1, SURF + 1, 116, FA_X + 2, SURF + 1, 116, B.stairs(B.STONE_BRICK_STAIRS, Dir.NORTH, false));
        // floor under the leaf so it looks bolted to its carriage
        k.fill(FA_X + 1, SURF + 1, 122, FA_X + 2, SURF + 1, 131, B.IRON);
    }

    // ------------------------------------------------------------------------------------------
    private void rooftop() {
        // exhaust stack over the conduit: 7x7 hollow stack, open top, beacon beam exits here
        int top = 97;
        int base = bermTop(CX, CZ);
        for (int y = base - 3; y <= top; y++) {
            for (int x = CX - 3; x <= CX + 3; x++) {
                for (int z = CZ - 3; z <= CZ + 3; z++) {
                    boolean edge = Math.abs(x - CX) == 3 || Math.abs(z - CZ) == 3;
                    boolean corner = Math.abs(x - CX) == 3 && Math.abs(z - CZ) == 3;
                    if (edge) {
                        int b = corner ? B.IRON : ((y - base) % 5 == 0 ? B.SMOOTH : B.SB);
                        if (!corner && y > top - 4 && Math.abs((x - CX) + (z - CZ)) % 2 == 0) {
                            b = B.BARS;
                        }
                        k.set(x, y, z, b);
                    } else if (y >= base) {
                        k.set(x, y, z, B.A);
                    }
                }
            }
        }
        k.fill(CX - 4, top + 1, CZ - 4, CX + 4, top + 1, CZ + 4, B.A);
        for (int x = CX - 4; x <= CX + 4; x++) {
            for (int z = CZ - 4; z <= CZ + 4; z++) {
                if (Math.abs(x - CX) == 4 || Math.abs(z - CZ) == 4) {
                    k.set(x, top, z, B.slab(B.SLAB_STONEBRICK, false));
                }
            }
        }
        // stack floor (grating) and a warning lamp ring
        k.fill(CX - 2, base - 1, CZ - 2, CX + 2, base - 1, CZ + 2, B.BARS);
        for (int x = CX - 2; x <= CX + 2; x++) {
            for (int z = CZ - 2; z <= CZ + 2; z++) {
                k.set(x, base - 2, z, B.IRON);
            }
        }
        k.lamp(CX - 3, top - 6, CZ, Dir.UP);
        k.lamp(CX + 3, top - 6, CZ, Dir.UP);
        k.set(CX - 3, top - 5, CZ, B.RS_BLOCK);
        k.set(CX + 3, top - 5, CZ, B.RS_BLOCK);
        // vent towers
        vent(-320, 162);
        vent(-352, 152);
        vent(-344, 116);
        // antenna mast with an aircraft warning light
        int mx = -360, mz = 140, mb = bermTop(mx, mz);
        for (int y = mb + 1; y <= mb + 22; y++) {
            k.set(mx, y, mz, y % 4 == 0 ? B.IRON : B.BARS);
            if (y % 6 == 0) {
                k.set(mx + 1, y, mz, B.BARS);
                k.set(mx - 1, y, mz, B.BARS);
                k.set(mx, y, mz + 1, B.BARS);
                k.set(mx, y, mz - 1, B.BARS);
            }
        }
        k.lamp(mx, mb + 23, mz, Dir.DOWN);
        k.set(mx, mb + 22, mz, B.RS_BLOCK);
        k.set(mx, mb + 24, mz, B.slab(B.SLAB_STONE, false));
        k.fill(mx - 1, mb, mz - 1, mx + 1, mb, mz + 1, B.SMOOTH);
        // radar dish on a lattice tower
        radar(-338, 150);
        k.c.marker("beacon-exit", CX, top, CZ, "exhaust stack");
    }

    private void vent(int x, int z) {
        int b = bermTop(x, z);
        for (int y = b - 2; y <= b + 7; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    boolean edge = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                    if (!edge) {
                        k.set(x + dx, y, z + dz, y <= b ? B.STN : B.A);
                        continue;
                    }
                    int blk = B.SB;
                    if (y > b + 2 && y < b + 7 && (Math.abs(dx) + Math.abs(dz)) % 2 == 1 && Math.abs(dx) != Math.abs(dz)) {
                        blk = B.BARS;
                    }
                    if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                        blk = B.IRON;
                    }
                    k.set(x + dx, y, z + dz, blk);
                }
            }
        }
        k.fill(x - 1, b + 1, z - 1, x + 1, b + 1, z + 1, B.IRON);
        k.fill(x - 2, b + 8, z - 2, x + 2, b + 8, z + 2, B.slab(B.SLAB_STONEBRICK, false));
        k.set(x, b + 8, z, B.SMOOTH);
        k.fill(x - 1, b + 2, z - 1, x + 1, b + 7, z + 1, B.A);
        k.set(x, b + 2, z, B.GLOW);
    }

    private void radar(int x, int z) {
        int b = bermTop(x, z);
        for (int y = b + 1; y <= b + 7; y++) {
            k.set(x, y, z, B.IRON);
            if (y < b + 6) {
                k.set(x + 1, y, z + 1, B.BARS);
                k.set(x - 1, y, z - 1, B.BARS);
            }
        }
        k.fill(x - 1, b, z - 1, x + 1, b, z + 1, B.SMOOTH);
        // dish facing east: a disc in the YZ plane, concave
        int cy = b + 11;
        for (int y = cy - 4; y <= cy + 4; y++) {
            for (int dz = -4; dz <= 4; dz++) {
                double d = Math.sqrt((y - cy) * (y - cy) + dz * dz);
                if (d <= 4.3) {
                    int off = d < 1.5 ? -1 : (d < 3 ? 0 : 1);
                    k.set(x + off, y, z + dz, d > 3.6 ? B.IRON : B.QUARTZ);
                }
            }
        }
        k.fill(x + 1, cy, z, x + 3, cy, z, B.BARS);
        k.set(x + 4, cy, z, B.IRON);
        k.fill(x, b + 8, z, x, cy - 5, z, B.IRON);
    }

    // ------------------------------------------------------------------------------------------
    /** Disguised pump house hiding the escape rail exit. */
    private void pumpHouse() {
        int x1 = PH_X1, x2 = PH_X2, z1 = PH_Z1, z2 = PH_Z2;
        // small pad with clearing and foundation
        k.fill(x1 - 3, 58, z1 - 3, x2 + 3, SURF - 1, z2 + 3, B.STN);
        k.fill(x1 - 3, SURF, z1 - 3, x2 + 3, SURF, z2 + 3, B.GRASS_B);
        k.fill(x1 - 3, SURF + 1, z1 - 3, x2 + 3, SURF + 10, z2 + 3, B.A);
        // path to the road
        for (int z = z2 + 1; z <= 132; z++) {
            k.set(-238, SURF, z, B.of(B.GRAVEL));
            k.set(-238, SURF - 1, z, B.STN);
            k.fill(-238, SURF + 1, z, -238, SURF + 3, z, B.A);
        }
        // weathered brick/cobble hut
        for (int y = SURF; y <= SURF + 4; y++) {
            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    boolean edge = x == x1 || x == x2 || z == z1 || z == z2;
                    if (y == SURF) {
                        k.set(x, y, z, B.COB);
                    } else if (edge) {
                        double r = Canvas.rand(x, y, z, 31);
                        k.set(x, y, z, r < 0.3 ? B.of(B.MOSSY_COBBLE) : (r < 0.5 ? B.COB : B.BRICKS));
                    } else {
                        k.set(x, y, z, B.A);
                    }
                }
            }
        }
        // pitched roof (spruce stairs along z)
        for (int x = x1 - 1; x <= x2 + 1; x++) {
            k.set(x, SURF + 5, z1 - 1, B.woodStairs(B.SPRUCE, Dir.SOUTH, false));
            k.set(x, SURF + 5, z2 + 1, B.woodStairs(B.SPRUCE, Dir.NORTH, false));
            for (int z = z1; z <= z2; z++) {
                int dzz = Math.min(z - z1, z2 - z);
                int y = SURF + 5 + dzz;
                if (z - z1 < z2 - z) {
                    k.set(x, y + 1, z, B.woodStairs(B.SPRUCE, Dir.SOUTH, false));
                } else if (z - z1 > z2 - z) {
                    k.set(x, y + 1, z, B.woodStairs(B.SPRUCE, Dir.NORTH, false));
                } else {
                    k.set(x, y + 1, z, B.woodSlab(B.SPRUCE, false));
                }
                if (x == x1 - 1 || x == x2 + 1) {
                    continue;
                }
                for (int yy = SURF + 5; yy <= y; yy++) {
                    k.set(x, yy, z, (x == x1 || x == x2) ? B.planks(B.SPRUCE) : B.A);
                }
            }
        }
        k.fill(x1 + 1, SURF + 4, z1 + 1, x2 - 1, SURF + 4, z2 - 1, B.A);
        // door (south) and window
        k.door((x1 + x2) / 2, SURF + 1, z2, B.SPRUCE_DOOR, Dir.NORTH, false);
        k.set(x1, SURF + 2, (z1 + z2) / 2, B.PANE);
        k.set(x2, SURF + 2, (z1 + z2) / 2, B.PANE);
        // rusty pump machinery as disguise
        k.set(x1 + 1, SURF + 1, z1 + 1, B.of(B.CAULDRON, 1));
        k.set(x1 + 2, SURF + 1, z1 + 1, B.IRON);
        k.set(x1 + 2, SURF + 2, z1 + 1, B.of(B.PISTON, 1));
        k.set(x1 + 3, SURF + 1, z1 + 1, B.of(B.NOTE_BLOCK));
        k.set(x1 + 1, SURF + 3, z1 + 3, B.GLOW);
        k.set(x2 - 1, SURF + 3, z2 - 2, B.GLOW);
        k.set(x2 - 1, SURF + 3, z1 + 1, B.GLOW);
        k.set(x1 + 4, SURF + 4, z1 + 1, B.GLOW);
        k.sign((x1 + x2) / 2, SURF + 3, z2 + 1, Dir.SOUTH, "PUMP STATION", "No. 3", "§8Water Board", "§8(disused)");
        k.c.room("Pump House (escape exit)", "Surface", x1, SURF, z1, x2, SURF + 3, z2, x1 + 2, SURF + 1, z2 - 2);
    }
}
