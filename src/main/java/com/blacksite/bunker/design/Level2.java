package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/** LEVEL 2 - Operations & Living (floor y=45). */
public final class Level2 {
    private final Kit k;
    private final Style s = Style.living();
    private static final int F = L2;
    private static final String LV = "Level 2";

    public Level2(Kit k) {
        this.k = k;
    }

    public void build() {
        Style ops = Style.security();
        ops.floorSlab = B.slab(B.SLAB_STONE, false);
        k.shell(-424, 137, -318, 145, F, 5, s, 6);          // spine
        k.shell(-352, 93, -308, 127, F, 6, ops, 6);          // operations hall
        k.shell(-336, 127, -324, 137, F, 5, ops, 0);         // ops gallery
        k.shell(-362, 145, -330, 173, F, 6, s, 6);           // mess hall
        k.shell(-330, 145, -311, 165, F, 6, kitchenStyle(), 5); // kitchen
        k.shell(-330, 165, -311, 181, F, 5, pantryStyle(), 4);  // pantry / cold room
        k.shell(-381, 155, -362, 187, F, 4, farmStyle(), 5);    // hydroponics
        k.shell(-405, 145, -401, 187, F, 5, s, 6);           // residential corridor
        k.shell(-401, 145, -382, 159, F, 6, s, 6);           // dorm A
        k.shell(-401, 159, -382, 173, F, 6, s, 6);           // dorm B
        k.shell(-401, 173, -382, 187, F, 6, s, 6);           // laundry
        k.shell(-424, 145, -405, 161, F, 6, washStyle(), 4); // showers
        k.shell(-424, 161, -405, 175, F, 6, s, 6);           // dorm C
        k.shell(-424, 175, -405, 187, F, 6, s, 6);           // workshop
        k.shell(-380, 113, -352, 137, F, 6, s, 6);           // lounge
        k.shell(-380, 95, -352, 113, F, 6, s, 6);            // commissary
        k.shell(-405, 97, -399, 137, F, 5, s, 6);            // officer corridor
        for (int i = 0; i < 3; i++) {
            int z1 = 97 + i * 13, z2 = z1 + 13;
            k.shell(-424, z1, -405, Math.min(z2, 137), F, 6, s, 6);   // officer suites west
            k.shell(-399, z1, -380, Math.min(z2, 137), F, 6, s, 6);   // officer suites east
        }
        openings(ops);
        spine();
        opsHall();
        mess();
        kitchen();
        pantry();
        farm();
        residential();
        lounge();
        commissary();
        officers();
    }

    static Style kitchenStyle() {
        Style s = Style.living();
        s.wall = B.QUARTZ;
        s.wallLow = B.clay(B.WHITE);
        s.wallHigh = B.QUARTZ;
        s.pillar = B.QUARTZ_PILLAR;
        s.floorSlab = B.slab(B.SLAB_QUARTZ, false);
        return s;
    }

    static Style pantryStyle() {
        Style s = Style.living();
        s.wall = B.of(B.PACKED_ICE);
        s.wallLow = B.SB;
        s.wallHigh = B.of(B.PACKED_ICE);
        s.pillar = B.SB;
        s.floorSlab = B.slab(B.SLAB_STONEBRICK, false);
        return s;
    }

    static Style farmStyle() {
        Style s = Style.living();
        s.wall = B.SB;
        s.wallLow = B.SB_MOSSY;
        s.wallHigh = B.SB;
        s.pillar = B.log(B.OAK, 0);
        s.floor = B.DIRT_B;
        s.floorSlab = -1;
        s.ceiling = B.GLS;
        return s;
    }

    static Style washStyle() {
        Style s = Style.living();
        s.wall = B.QUARTZ;
        s.wallLow = B.clay(B.LIGHT_BLUE);
        s.wallHigh = B.QUARTZ;
        s.pillar = B.QUARTZ_PILLAR;
        s.floorSlab = B.slab(B.SLAB_QUARTZ, false);
        return s;
    }

    private void openings(Style ops) {
        k.opening(-333, 137, -327, 137, F, 4, ops);   // spine -> gallery
        k.opening(-333, 127, -327, 127, F, 4, ops);   // gallery -> ops
        k.opening(-353, 145, -350, 145, F, 3, s);     // spine -> mess (west door)
        k.opening(-342, 145, -339, 145, F, 3, s);     // spine -> mess (east door)
        k.opening(-330, 150, -330, 152, F, 2, s);     // mess <-> kitchen pass
        k.opening(-322, 145, -321, 145, F, 3, s);     // spine -> kitchen
        k.door(-320, F + 1, 165, B.IRON_DOOR, Dir.SOUTH, false); // kitchen -> cold room (heavy door)
        k.set(-319, F + 2, 164, B.button(Dir.NORTH, false));
        k.set(-319, F + 2, 166, B.button(Dir.SOUTH, false));
        k.c.marker("irondoor", -320, F + 1, 165, "SOUTH");
        k.opening(-362, 160, -362, 162, F, 3, s);     // mess -> hydroponics
        k.opening(-404, 145, -402, 145, F, 3, s);     // spine -> residential corridor
        k.door(-401, F + 1, 152, B.SPRUCE_DOOR, Dir.EAST, false); // dorm A
        k.door(-401, F + 1, 166, B.SPRUCE_DOOR, Dir.EAST, false); // dorm B
        k.opening(-401, 179, -401, 181, F, 3, s);     // laundry
        k.door(-405, F + 1, 153, B.BIRCH_DOOR, Dir.WEST, false); // showers
        k.door(-405, F + 1, 168, B.SPRUCE_DOOR, Dir.WEST, false); // dorm C
        k.opening(-405, 180, -405, 182, F, 3, s);     // workshop
        k.opening(-370, 137, -367, 137, F, 3, s);     // spine -> lounge
        k.opening(-367, 113, -365, 113, F, 3, s);     // lounge -> commissary
        k.opening(-404, 137, -400, 137, F, 3, s);     // spine -> officer corridor
        for (int i = 0; i < 3; i++) {
            int zc = 97 + i * 13 + 6;
            k.door(-405, F + 1, zc, B.DARK_OAK_DOOR, Dir.WEST, false);
            k.door(-399, F + 1, zc, B.DARK_OAK_DOOR, Dir.EAST, false);
        }
    }

    private void spine() {
        for (int x = -423; x <= -319; x++) {
            k.set(x, F + 1, 141, B.carpet(B.GREEN));
            if (Math.floorMod(x, 6) == 0) {
                k.set(x, F + 6, 141, B.GLOW);
                k.fill(x, F + 5, 138, x, F + 5, 138, B.log(B.SPRUCE, 0));
                k.fill(x, F + 5, 144, x, F + 5, 144, B.log(B.SPRUCE, 0));
            }
        }
        k.sign(-423, F + 3, 141, Dir.EAST, "§lLEVEL 2", "Operations &", "Living", "");
        k.sign(-369, F + 3, 138, Dir.SOUTH, "< Officers", "  Lounge ^", "Mess Hall >", "");
        k.c.room("Level 2 Main Corridor", LV, -424, F, 137, -318, F + 6, 145, -372, F + 1, 141);
    }

    private void opsHall() {
        int x1 = -351, x2 = -309, z1 = 94, z2 = 126;
        // conduit display around the beam column (-330,108)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int ad = Math.max(Math.abs(dx), Math.abs(dz));
                if (ad == 2) {
                    k.set(CX + dx, F + 1, CZ + dz, (Math.abs(dx) == 2 && Math.abs(dz) == 2) ? B.IRON : B.sglass(B.LIGHT_BLUE));
                } else if (ad == 1) {
                    k.set(CX + dx, F + 1, CZ + dz, B.SEA);
                    for (int y = F + 2; y <= F + 5; y++) {
                        k.set(CX + dx, y, CZ + dz, (dx != 0 && dz != 0) ? B.IRON : B.spane(B.LIGHT_BLUE));
                    }
                    k.set(CX + dx, F + 6, CZ + dz, B.IRON);
                }
            }
        }
        // situation desk ring
        k.ringXZ(CX, F + 1, CZ, 5.5, 6.4, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
        for (int a = 0; a < 16; a++) {
            double ang = a * Math.PI / 8;
            int x = (int) Math.round(CX + 7.3 * Math.cos(ang)), z = (int) Math.round(CZ + 7.3 * Math.sin(ang));
            if (k.get(x, F + 1, z) == s.floorSlab || k.get(x, F + 1, z) == B.slab(B.SLAB_STONE, false)) {
                Dir face = Math.abs(Math.cos(ang)) > Math.abs(Math.sin(ang)) ? (Math.cos(ang) > 0 ? Dir.WEST : Dir.EAST)
                        : (Math.sin(ang) > 0 ? Dir.NORTH : Dir.SOUTH);
                k.chair(x, F + 1, z, B.DARK_OAK, face);
            }
        }
        for (int a = 0; a < 8; a++) {
            double ang = a * Math.PI / 4 + 0.2;
            int x = (int) Math.round(CX + 6 * Math.cos(ang)), z = (int) Math.round(CZ + 6 * Math.sin(ang));
            k.set(x, F + 2, z, a % 2 == 0 ? B.of(B.DAYLIGHT_SENSOR) : B.button(Dir.UP, false));
        }
        // workstation blocks east and west of the ring
        for (int side = -1; side <= 1; side += 2) {
            for (int row = 0; row < 3; row++) {
                int z = 106 + row * 7;
                for (int i = 0; i < 6; i++) {
                    int x = CX + side * (11 + i);
                    k.set(x, F + 1, z, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
                    k.set(x, F + 1, z + 1, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
                    if (i % 2 == 0) {
                        k.chair(x, F + 1, z - 1, B.DARK_OAK, Dir.SOUTH);
                        k.chair(x, F + 1, z + 2, B.DARK_OAK, Dir.NORTH);
                        k.set(x, F + 2, z, B.of(B.DAYLIGHT_SENSOR));
                    } else {
                        k.set(x, F + 2, z + 1, B.of(B.FLOWER_POT));
                    }
                }
            }
        }
        // status boards on the north wall
        for (int x = x1 + 2; x <= x2 - 2; x++) {
            k.set(x, F + 3, 93, Math.floorMod(x, 4) == 0 ? B.LAMP : B.clay(B.BLACK));
            if (Math.floorMod(x, 4) == 0) {
                k.set(x, F + 3, 92, B.RS_BLOCK);
            }
            k.set(x, F + 4, 93, B.clay(B.BLACK));
            k.set(x, F + 2, 93, B.IRON);
        }
        // glass-walled chief of operations office in the NW corner
        k.partition(-351, 104, -343, 104, F, 5, B.IRON, B.PANE);
        k.partition(-343, 94, -343, 103, F, 5, B.IRON, B.PANE);
        k.set(-343, F + 1, 99, B.doorLower(B.SPRUCE_DOOR, Dir.EAST, false));
        k.set(-343, F + 2, 99, B.doorUpper(B.SPRUCE_DOOR, false));
        for (int x = -349; x <= -346; x++) {
            k.set(x, F + 1, 97, B.woodStairs(B.DARK_OAK, Dir.NORTH, true));
        }
        k.chair(-347, F + 1, 95, B.DARK_OAK, Dir.SOUTH);
        k.chest(-350, F + 1, 103, Dir.EAST, Loot.officer(0));
        k.set(-349, F + 1, 103, B.BOOKS);
        k.set(-348, F + 1, 103, B.BOOKS);
        k.lamp(-347, F + 7, 99, Dir.UP);
        // lighting
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 5, B.LAMP);
        k.sign(-330, F + 4, 126, Dir.NORTH, "§lOPERATIONS", "§lHALL", "The Conduit", "§8do not touch");
        k.c.room("Central Operations Hall", LV, -352, F, 93, -308, F + 7, 127, -330, F + 1, 120);
        // gallery between the spine and ops
        for (int x = -335; x <= -325; x++) {
            k.set(x, F + 1, 132, B.carpet(B.GREEN));
        }
        for (int z = 128; z <= 136; z += 3) {
            k.set(-335, F + 3, z, B.GLOW);
            k.set(-325, F + 3, z, B.GLOW);
        }
        k.c.room("Operations Gallery", LV, -336, F, 127, -324, F + 6, 137, -330, F + 1, 132);
    }

    private void mess() {
        int x1 = -361, x2 = -331, z1 = 146, z2 = 172;
        // four long tables along x with benches
        for (int t = 0; t < 4; t++) {
            int z = 151 + t * 6;
            for (int x = x1 + 3; x <= x2 - 5; x++) {
                k.set(x, F + 1, z, B.woodStairs(B.SPRUCE, Dir.SOUTH, true));
                k.set(x, F + 1, z + 1, B.woodStairs(B.SPRUCE, Dir.NORTH, true));
                k.chair(x, F + 1, z - 1, B.SPRUCE, Dir.SOUTH);
                k.chair(x, F + 1, z + 2, B.SPRUCE, Dir.NORTH);
                if (Math.floorMod(x, 7) == 0) {
                    k.set(x, F + 2, z, B.of(B.CAKE));
                } else if (Math.floorMod(x, 7) == 3) {
                    k.set(x, F + 2, z + 1, B.of(B.FLOWER_POT));
                }
            }
        }
        // serving counter along the east wall (kitchen pass-through at z 150..152)
        for (int z = z1 + 1; z <= z2 - 1; z++) {
            k.set(x2, F + 1, z, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, true));
        }
        k.set(x2, F + 1, 150, s.floorSlab);
        k.set(x2, F + 1, 151, s.floorSlab);
        k.set(x2, F + 1, 152, s.floorSlab);
        k.set(x2 - 1, F + 1, 147, B.of(B.CAULDRON, 3));
        k.set(x2, F + 2, 155, B.of(B.CAKE));
        k.set(x2, F + 2, 158, B.of(B.FLOWER_POT));
        // chandeliers
        for (int x = x1 + 5; x <= x2 - 5; x += 8) {
            for (int z = 154; z <= 166; z += 6) {
                k.hangingLight(x, F + 5, z, F + 7, B.of(B.SPRUCE_FENCE), B.GLOW);
            }
        }
        k.ceilingGrid(x1 + 2, z1 + 1, x2 - 2, z2 - 1, F + 7, 4, B.GLOW);
        // banners and a notice board
        for (int x = x1 + 4; x <= x2 - 4; x += 6) {
            k.wallBanner(x, F + 5, z2, Dir.NORTH, B.GREEN, "bo", "black", "flo", "yellow");
        }
        k.sign(x1, F + 3, 155, Dir.EAST, "§lMESS HALL", "Breakfast 0600", "Lunch 1200", "Dinner 1800");
        k.sign(x1, F + 3, 157, Dir.EAST, "Today:", "Mystery stew", "§8(again)", "");
        k.c.room("Mess Hall", LV, -362, F, 145, -330, F + 7, 173, -346, F + 1, 148);
    }

    private void kitchen() {
        int x1 = -329, x2 = -312, z1 = 146, z2 = 164;
        // cooking line: furnaces along the east wall
        for (int z = z1 + 1; z <= z2 - 1; z++) {
            if (z % 2 == 0) {
                k.set(x2, F + 1, z, B.furnace(Dir.WEST));
            } else {
                k.set(x2, F + 1, z, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, true));
            }
            k.set(x2, F + 3, z, B.BARS);
        }
        k.set(x2, F + 1, 155, B.litFurnace(Dir.WEST));
        // prep island
        for (int x = -325; x <= -316; x++) {
            k.set(x, F + 1, 155, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
            k.set(x, F + 1, 156, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
        }
        k.set(-322, F + 1, 155, B.WORKBENCH_B);
        k.set(-319, F + 1, 156, B.WORKBENCH_B);
        k.set(-324, F + 2, 155, B.of(B.CAULDRON, 3));
        // sinks & storage along the north wall
        for (int x = x1 + 1; x <= x2 - 2; x++) {
            k.set(x, F + 1, z1, x % 3 == 0 ? B.of(B.CAULDRON, 3) : B.chest(Dir.SOUTH));
        }
        k.c.tile(new TileSpec.Inventory(-327, F + 1, z1, Painter.spread(Loot.kitchen(), 27, 1, 1, 1)));
        k.c.tile(new TileSpec.Inventory(-326, F + 1, z1, Painter.spread(Loot.rations(), 27, 2, 1, 1)));
        k.set(x1, F + 1, 162, B.of(B.BREWING_STAND));
        k.set(x1, F + 1, 160, B.of(B.HAY_BALE));
        k.sign(x1, F + 3, 157, Dir.EAST, "KITCHEN", "§8wash hands", "", "");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.c.room("Kitchen", LV, -330, F, 145, -311, F + 7, 165, -320, F + 1, 160);
    }

    private void pantry() {
        int x1 = -329, x2 = -312, z1 = 166, z2 = 180;
        String[] labels = {"Grain", "Veg", "Meat", "Fish", "Frozen", "Dairy", "Baking", "Sweets"};
        k.storageRow(x1 + 1, x2 - 1, z2, F + 1, Dir.NORTH, B.of(B.PACKED_ICE), labels, 2);
        k.c.tile(new TileSpec.Inventory(x1 + 1, F + 1, z2, Painter.spread(Loot.pantry(), 27, 3, 3, 3)));
        k.c.tile(new TileSpec.Inventory(x1 + 4, F + 1, z2, Painter.spread(Loot.freezer(), 27, 4, 3, 3)));
        for (int x = x1 + 2; x <= x2 - 2; x += 3) {
            k.set(x, F + 1, 170, B.of(B.HAY_BALE));
            k.set(x, F + 1, 173, x % 2 == 0 ? B.of(B.MELON) : B.pumpkinFace(B.PUMPKIN, Dir.NORTH));
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 6, 4, B.GLOW);
        k.sign(-320, F + 3, 166, Dir.SOUTH, "§bCOLD STORAGE", "Keep door", "closed", "");
        k.c.room("Pantry & Cold Room", LV, -330, F, 165, -311, F + 6, 181, -320, F + 1, 168);
    }

    private void farm() {
        int x1 = -380, x2 = -363, z1 = 156, z2 = 186;
        for (int x = x1; x <= x2; x++) {
            int m = Math.floorMod(x - x1, 10);
            for (int z = z1; z <= z2; z++) {
                if (m == 0) {
                    k.set(x, F, z, B.SB);
                    k.set(x, F + 1, z, B.slab(B.SLAB_STONEBRICK, false));
                } else if (m == 5) {
                    k.set(x, F, z, B.of(B.WATER));
                    k.set(x, F - 1, z, B.SB);
                } else {
                    k.set(x, F, z, B.of(B.FARMLAND, 7));
                    int band = Math.floorMod((z - z1) / 5 + (x - x1) / 10, 3);
                    int crop = band == 0 ? B.of(B.WHEAT, 7) : band == 1 ? B.of(B.CARROTS, 7) : B.of(B.POTATOES, 7);
                    k.set(x, F + 1, z, crop);
                }
            }
        }
        // cross walkway in the middle
        for (int x = x1; x <= x2; x++) {
            int m = Math.floorMod(x - x1, 10);
            k.set(x, F, 171, m == 5 ? B.of(B.WATER) : B.SB);
            k.set(x, F + 1, 171, m == 5 ? B.A : B.slab(B.SLAB_STONEBRICK, false));
        }
        // sugar cane on the west edge beside water (dirt + water)
        // grow lights: glowstone grid in the glass ceiling
        k.ceilingGrid(x1, z1, x2, z2, F + 5, 3, B.GLOW);
        k.set(x1, F + 1, z1 - 0, B.chest(Dir.SOUTH));
        k.c.tile(new TileSpec.Inventory(x1, F + 1, z1, Painter.spread(Loot.farm(), 27, 5, 5, 5)));
        k.sign(-362, F + 3, 159, Dir.WEST, "§2HYDROPONICS", "Wheat Carrots", "Potatoes", "§8don't jump!");
        k.c.room("Hydroponics Farm", LV, -381, F, 155, -362, F + 5, 187, -370, F + 2, 171);
        k.c.marker("farm", -372, F + 1, 170, "crops need light >= 9");
    }

    private void residential() {
        // corridor
        for (int z = 146; z <= 186; z++) {
            k.set(-403, F + 1, z, B.carpet(B.GREEN));
            if (z % 5 == 0) {
                k.set(-403, F + 6, z, B.GLOW);
            }
        }
        k.c.room("Residential Corridor", LV, -405, F, 145, -401, F + 6, 187, -403, F + 1, 170);
        dorm(-400, -383, 146, 158, "Crew Dorm A", 0);
        dorm(-400, -383, 160, 172, "Crew Dorm B", 10);
        dorm(-423, -406, 162, 174, "Crew Dorm C", 20);
        // laundry
        int x1 = -400, x2 = -383, z1 = 174, z2 = 186;
        for (int x = x1 + 1; x <= x2 - 1; x += 2) {
            k.set(x, F + 1, z2, B.of(B.CAULDRON, 3));
            k.set(x + 1, F + 1, z2, B.IRON);
            k.set(x + 1, F + 2, z2, B.of(B.DAYLIGHT_SENSOR));
        }
        k.chest(x1 + 1, F + 1, z1, Dir.SOUTH, Loot.laundry());
        k.chest(x1 + 2, F + 1, z1, Dir.SOUTH);
        for (int x = x1 + 3; x <= x2 - 3; x++) {
            k.set(x, F + 4, 180, B.of(B.SPRUCE_FENCE));
            if (x % 2 == 0) {
                k.set(x, F + 3, 180, B.wool(x % 4 == 0 ? B.WHITE : B.GREEN));
            }
        }
        k.fill(x1 + 2, F + 1, 180, x1 + 2, F + 3, 180, B.of(B.SPRUCE_FENCE));
        k.fill(x2 - 2, F + 1, 180, x2 - 2, F + 3, 180, B.of(B.SPRUCE_FENCE));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.c.room("Laundry & Maintenance", LV, -401, F, 173, -382, F + 7, 187, -392, F + 1, 177);
        // showers & washroom
        x1 = -423;
        x2 = -406;
        z1 = 146;
        z2 = 160;
        for (int x = x1 + 1; x <= x2 - 1; x += 3) {
            // shower stall: bars sides, tripwire hook shower head
            k.fill(x - 1, F + 1, z1, x - 1, F + 3, z1 + 1, B.BARS);
            k.set(x, F + 3, z1, B.of(B.TRIPWIRE_HOOK, 0));
            k.set(x, F + 1, z1, B.carpet(B.LIGHT_BLUE));
        }
        for (int x = x1 + 1; x <= x2 - 1; x += 2) {
            k.set(x, F + 1, z2, B.of(B.CAULDRON, 3));
            k.set(x, F + 3, z2, B.spane(B.LIGHT_BLUE));
        }
        // toilet cubicles on the west wall
        for (int z = z1 + 4; z <= z2 - 3; z += 3) {
            k.set(x1, F + 1, z, B.stairs(B.QUARTZ_STAIRS, Dir.WEST, false));
            k.set(x1 + 1, F + 1, z - 1, B.QUARTZ);
            k.set(x1 + 1, F + 2, z - 1, B.QUARTZ);
            k.set(x1 + 1, F + 1, z + 1, B.QUARTZ);
            k.set(x1 + 1, F + 2, z + 1, B.QUARTZ);
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.SEA);
        k.sign(-406, F + 3, 151, Dir.EAST, "WASHROOMS", "Showers", "", "");
        k.c.room("Showers & Washrooms", LV, -424, F, 145, -405, F + 7, 161, -412, F + 1, 153);
        // workshop
        x1 = -423;
        x2 = -406;
        z1 = 176;
        z2 = 186;
        k.set(x1 + 1, F + 1, z2, B.WORKBENCH_B);
        k.set(x1 + 2, F + 1, z2, B.WORKBENCH_B);
        k.set(x1 + 3, F + 1, z2, B.anvil(true));
        k.set(x1 + 4, F + 1, z2, B.furnace(Dir.NORTH));
        k.set(x1 + 5, F + 1, z2, B.furnace(Dir.NORTH));
        k.chest(x1 + 6, F + 1, z2, Dir.NORTH, Loot.workshop());
        k.chest(x1 + 7, F + 1, z2, Dir.NORTH);
        k.fill(x1 + 9, F + 1, z2, x2 - 1, F + 1, z2, B.woodSlab(B.SPRUCE, true));
        for (int x = x1 + 9; x <= x2 - 1; x += 2) {
            k.itemFrame(x, F + 3, z2, Dir.NORTH, ItemSpec.of(x % 4 == 0 ? 257 : 258, 1));
        }
        k.set(x1, F + 1, 180, B.of(B.CAULDRON, 3));
        k.set(x1, F + 1, 181, B.of(B.BREWING_STAND));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.c.room("General Workshop", LV, -424, F, 175, -405, F + 7, 187, -414, F + 1, 180);
    }

    private void dorm(int x1, int x2, int z1, int z2, String name, int seed) {
        boolean eastSide = x1 > -402;
        // beds along both long walls
        int n = seed;
        for (int x = x1 + 1; x <= x2 - 1; x += 3) {
            k.bed(x, F + 1, z1 + 1, Dir.NORTH);
            k.chest(x + 1, F + 1, z1, Dir.SOUTH, Loot.crewLocker(n++));
            k.bed(x, F + 1, z2 - 1, Dir.SOUTH);
            k.chest(x + 1, F + 1, z2, Dir.NORTH, Loot.crewLocker(n++));
        }
        k.rug(x1 + 1, z1 + 3, x2 - 1, z2 - 3, F, B.carpet(B.BROWN));
        int mid = (z1 + z2) / 2;
        for (int x = x1 + 4; x <= x2 - 4; x += 5) {
            k.postTable(x, F + 1, mid, B.SPRUCE_FENCE);
            k.chair(x - 1, F + 1, mid, B.SPRUCE, Dir.EAST);
            k.chair(x + 1, F + 1, mid, B.SPRUCE, Dir.WEST);
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.c.room(name, LV, x1 - 1, F, z1 - 1, x2 + 1, F + 7, z2 + 1, eastSide ? x1 + 1 : x2 - 1, F + 1, mid + 1);
    }

    private void lounge() {
        int x1 = -379, x2 = -353, z1 = 114, z2 = 136;
        // pool table (green felt on planks)
        for (int x = -371; x <= -366; x++) {
            for (int z = 123; z <= 125; z++) {
                k.set(x, F + 1, z, B.planks(B.DARK_OAK));
                k.set(x, F + 2, z, B.carpet(B.GREEN));
            }
        }
        // couches around a TV
        for (int x = -377; x <= -373; x++) {
            k.set(x, F + 1, 133, B.woodStairs(B.SPRUCE, Dir.SOUTH, false));
        }
        k.set(-378, F + 1, 133, B.woodStairs(B.SPRUCE, Dir.EAST, true));
        k.set(-372, F + 1, 133, B.woodStairs(B.SPRUCE, Dir.WEST, true));
        k.fill(-377, F + 2, 136, -373, F + 4, 136, B.clay(B.BLACK));
        k.fill(-376, F + 3, 136, -374, F + 3, 136, B.sglass(B.BLUE));
        k.rug(-377, 129, -373, 132, F, B.carpet(B.RED));
        // bar along the east wall
        for (int z = z1 + 3; z <= z2 - 3; z++) {
            k.set(-356, F + 1, z, B.woodStairs(B.DARK_OAK, Dir.WEST, true));
            if (z % 3 == 0) {
                k.chair(-357, F + 1, z, B.DARK_OAK, Dir.EAST);
            }
        }
        k.set(-354, F + 1, 120, B.of(B.BREWING_STAND));
        k.set(-354, F + 1, 122, B.of(B.CAULDRON, 3));
        k.chest(-354, F + 1, 124, Dir.WEST, Loot.lounge());
        k.set(-354, F + 1, 126, B.of(B.JUKEBOX));
        k.fill(x2, F + 2, 118, x2, F + 3, 130, B.BOOKS);
        // bookshelf nook and armchairs
        k.fill(x1, F + 1, 116, x1, F + 3, 121, B.BOOKS);
        k.chair(-377, F + 1, 118, B.DARK_OAK, Dir.EAST);
        k.chair(-377, F + 1, 120, B.DARK_OAK, Dir.EAST);
        // sealed lava fireplace in the north wall (encased: glass front, stone brick all round)
        int fx = -374, fz = 113;
        k.fill(fx - 1, F + 1, fz - 1, fx + 1, F + 3, fz + 1, B.SB);
        k.set(fx, F + 1, fz, B.of(B.LAVA));
        k.set(fx, F + 1, fz + 1, B.GLS);
        k.fill(fx - 1, F + 2, fz + 1, fx + 1, F + 2, fz + 1, B.stairs(B.STONE_BRICK_STAIRS, Dir.NORTH, true));
        k.set(fx - 1, F + 1, fz + 1, B.SB_CHISELED);
        k.set(fx + 1, F + 1, fz + 1, B.SB_CHISELED);
        k.c.marker("lava", fx, F + 1, fz, "sealed fireplace");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 5, B.GLOW);
        k.sign(-368, F + 3, 136, Dir.NORTH, "§6CREW LOUNGE", "Rec room & bar", "", "");
        k.c.room("Crew Lounge & Rec Room", LV, -380, F, 113, -352, F + 7, 137, -368, F + 1, 128);
    }

    private void commissary() {
        int x1 = -379, x2 = -353, z1 = 96, z2 = 112;
        for (int x = x1 + 4; x <= x2 - 4; x++) {
            k.set(x, F + 1, 107, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
        }
        String[] labels = {"Snacks", "Toiletries", "Batteries", "Stationery", "Cards", "Misc", "Spares", "Returns"};
        k.storageRow(x1 + 1, x2 - 1, z1, F + 1, Dir.SOUTH, B.planks(B.SPRUCE), labels, 2);
        for (int x = x1 + 2; x <= x2 - 2; x += 4) {
            k.itemFrame(x, F + 4, z1, Dir.SOUTH, ItemSpec.of(x % 8 == 0 ? 357 : 297, 1));
        }
        k.set(-366, F + 1, 107, B.fenceGate(B.SPRUCE_FENCE_GATE, Dir.SOUTH, false));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 5, B.GLOW);
        k.sign(-363, F + 3, 114, Dir.SOUTH, "COMMISSARY", "§8Open 0800-2000", "", "");
        k.c.room("Commissary", LV, -380, F, 95, -352, F + 7, 113, -366, F + 1, 110);
    }

    private void officers() {
        for (int z = 98; z <= 136; z++) {
            k.set(-402, F + 1, z, B.carpet(B.RED));
            if (z % 5 == 0) {
                k.set(-402, F + 6, z, B.GLOW);
            }
        }
        k.c.room("Officer Corridor", LV, -405, F, 97, -399, F + 6, 137, -402, F + 1, 117);
        String[] names = {"Cmdr. Hale", "Maj. Osei", "Dr. Iyer", "Lt. Moreau", "Capt. Brandt", "Warden Voss"};
        int n = 0;
        for (int i = 0; i < 3; i++) {
            int z1 = 97 + i * 13, z2 = Math.min(z1 + 13, 137);
            suite(-424, -405, z1, z2, true, names[n], n);
            n++;
            suite(-399, -380, z1, z2, false, names[n], n);
            n++;
        }
    }

    private void suite(int x1, int x2, int z1, int z2, boolean west, String owner, int n) {
        int ix1 = x1 + 1, ix2 = x2 - 1, iz1 = z1 + 1, iz2 = z2 - 1;
        int doorZ = z1 + 6;
        int far = west ? ix1 : ix2;       // far wall side (away from the corridor)
        Dir toCorr = west ? Dir.EAST : Dir.WEST;
        k.rug(ix1 + 2, iz1 + 2, ix2 - 2, iz2 - 2, F, B.carpet(n % 2 == 0 ? B.RED : B.BLUE));
        k.bed(far, F + 1, iz1 + 2, Dir.NORTH);
        k.set(far, F + 1, iz1, B.chest(Dir.SOUTH));
        k.c.tile(new TileSpec.Inventory(far, F + 1, iz1, Painter.spread(Loot.officer(n), 27, far, F, iz1)));
        // desk
        int dx = west ? ix1 + 3 : ix2 - 3;
        k.set(dx, F + 1, iz2, B.woodStairs(B.DARK_OAK, Dir.SOUTH, true));
        k.set(dx + (west ? 1 : -1), F + 1, iz2, B.woodStairs(B.DARK_OAK, Dir.SOUTH, true));
        k.set(dx, F + 2, iz2, B.of(B.FLOWER_POT));
        k.chair(dx, F + 1, iz2 - 1, B.DARK_OAK, Dir.SOUTH);
        k.fill(far, F + 1, iz2 - 3, far, F + 3, iz2, B.BOOKS);
        k.set(far, F + 1, iz2 - 4, B.WORKBENCH_B);
        EntitySpec e = k.armorStand(dx + (west ? 4 : -4), F + 1, iz1 + 1, toCorr);
        e.chest = Loot.leather(299, Loot.NAVY, "Dress Uniform");
        e.legs = Loot.leather(300, Loot.NAVY, "Dress Trousers");
        e.boots = Loot.leather(301, Loot.BLACK, "Dress Shoes");
        k.set((ix1 + ix2) / 2, F + 5, (iz1 + iz2) / 2, B.GLOW);
        k.hangingLight((ix1 + ix2) / 2, F + 5, (iz1 + iz2) / 2, F + 7, B.of(B.DARK_OAK_FENCE), B.GLOW);
        k.set(dx, F + 4, iz2 + 1, B.GLOW);
        k.set(far, F + 4, (iz1 + iz2) / 2, B.GLOW);
        int signX = west ? x2 + 1 : x1 - 1;
        k.sign(signX, F + 3, doorZ + 1, toCorr, owner, "§8Officer suite", "", "");
        k.c.room("Officer Suite (" + owner + ")", LV, x1, F, z1, x2, F + 7, z2, west ? ix2 - 1 : ix1 + 1, F + 1, doorZ);
    }
}
