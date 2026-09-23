package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/** LEVEL 1 - Security & Command (floor y=55). */
public final class Level1 {
    private final Kit k;
    private final Style s = Style.security();
    private static final int F = L1;
    private static final String LV = "Level 1";

    public Level1(Kit k) {
        this.k = k;
    }

    public void build() {
        shells();
        openings();
        intakeHall();
        commandGate();
        commandCenter();
        northWing();
        comms();
        sigint();
        mapRoom();
        directorOffice();
        warRoom();
        securityControl();
        briefing();
        securityStorage();
        lockers();
        guardQuarters();
        spine();
        readyRoom();
        quartermaster();
        armory();
        annex();
        range();
        barracks();
    }

    private void shells() {
        k.shell(-358, 127, -340, 155, F, 8, s, 6);          // intake hall
        k.shell(-348, 123, -340, 127, F, 5, s, 0);          // command gate vestibule
        k.shell(-348, 93, -312, 123, F, 9, s, 6);           // command center
        k.shell(-418, 123, -348, 127, F, 5, s, 6);          // north wing corridor
        k.shell(-364, 105, -348, 123, F, 6, s, 6);          // communications
        k.shell(-364, 93, -348, 105, F, 6, s, 6);           // SIGINT
        k.shell(-382, 105, -364, 123, F, 6, s, 6);          // map room
        k.shell(-382, 91, -364, 105, F, 6, s, 6);           // director's office
        k.shell(-400, 91, -382, 105, F, 6, s, 6);           // war room (secret)
        k.shell(-400, 105, -382, 123, F, 6, s, 6);          // security control
        k.shell(-418, 105, -400, 123, F, 6, s, 6);          // briefing room
        k.shell(-418, 91, -400, 105, F, 6, s, 6);           // security storage
        k.shell(-384, 127, -358, 137, F, 6, s, 6);          // equipment lockers
        k.shell(-418, 127, -384, 137, F, 6, s, 6);          // guard quarters
        k.shell(-420, 137, -358, 145, F, 5, s, 6);          // spine
        k.shell(-366, 145, -358, 155, F, 6, s, 0);          // ready room
        k.shell(-381, 155, -358, 169, F, 6, s, 6);          // quartermaster
        k.shell(-406, 145, -381, 169, F, 6, s, 6);          // armory
        k.shell(-400, 172, -386, 180, F, 5, s, 0);          // armory annex (secret)
        k.shell(-432, 145, -406, 169, F, 6, s, 6);          // firing range
        k.shell(-358, 155, -330, 177, F, 6, s, 6);          // barracks
    }

    private void openings() {
        // ramp arrives through the intake hall east wall
        k.fill(-340, F + 1, 137, -340, F + 7, 145, B.A);
        // intake hall -> command gate (double iron door) and -> north wing corridor
        k.ironDoubleDoor(-345, F + 1, 127, Dir.NORTH);
        k.ironDoubleDoor(-345, F + 1, 123, Dir.NORTH);
        k.ironDoubleDoor(-354, F + 1, 127, Dir.NORTH);
        // checkpoint Bravo: intake -> spine, three lanes through the IH west wall x=-358
        for (int z = 138; z <= 144; z++) {
            k.fill(-358, F + 1, z, -358, F + 5, z, z % 2 == 0 ? B.IRON : B.SB);
        }
        for (int z = 139; z <= 143; z += 2) {
            k.door(-358, F + 1, z, B.IRON_DOOR, Dir.WEST, false);
            k.set(-357, F + 1, z, B.of(B.STONE_PLATE));
            k.set(-359, F + 1, z, B.of(B.STONE_PLATE));
            k.c.marker("irondoor", -358, F + 1, z, "WEST");
        }
        // north wing doors
        k.ironDoor(-356, F + 1, 123, Dir.NORTH, true);          // comms
        k.ironDoor(-373, F + 1, 123, Dir.NORTH, true);          // map room
        k.ironDoor(-391, F + 1, 123, Dir.NORTH, true);          // security control
        k.opening(-410, 123, -408, 123, F, 3, s);                // briefing (open arch)
        k.ironDoor(-356, F + 1, 105, Dir.NORTH, true);          // comms -> SIGINT
        k.door(-373, F + 1, 105, B.DARK_OAK_DOOR, Dir.NORTH, false); // map -> director
        k.ironDoor(-409, F + 1, 105, Dir.NORTH, true);          // briefing -> storage
        // lockers and guard quarters
        k.opening(-372, 137, -370, 137, F, 3, s);
        k.opening(-378, 127, -377, 127, F, 3, s);
        k.opening(-403, 137, -401, 137, F, 3, s);
        k.opening(-395, 127, -394, 127, F, 3, s);
        // south
        k.ironDoor(-362, F + 1, 145, Dir.SOUTH, false);         // ready room
        k.ironDoubleDoor(-395, F + 1, 145, Dir.NORTH);          // armory (from inside it's facing north)
        k.opening(-362, 155, -361, 155, F, 3, s);                // ready -> quartermaster
        k.opening(-381, 160, -381, 162, F, 3, s);                // armory <-> quartermaster
        k.ironDoor(-406, F + 1, 151, Dir.WEST, true);           // armory -> range
        k.opening(-349, 155, -347, 155, F, 3, s);                // intake -> barracks
    }

    // ------------------------------------------------------------------------------------------
    private void intakeHall() {
        int x1 = -357, x2 = -341, z1 = 128, z2 = 154;
        // floor pattern: red "lanes" leading from the ramp to the checkpoint
        for (int x = x1; x <= x2; x++) {
            k.set(x, F + 1, 137, B.carpet(B.RED));
            k.set(x, F + 1, 145, B.carpet(B.RED));
        }
        // reception desk (U-shape) on the south side
        for (int x = -352; x <= -346; x++) {
            k.set(x, F + 1, 149, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
        }
        k.set(-353, F + 1, 150, B.stairs(B.QUARTZ_STAIRS, Dir.WEST, true));
        k.set(-353, F + 1, 151, B.stairs(B.QUARTZ_STAIRS, Dir.WEST, true));
        k.set(-345, F + 1, 150, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, true));
        k.set(-345, F + 1, 151, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, true));
        k.set(-350, F + 2, 149, B.of(B.DAYLIGHT_SENSOR));
        k.set(-348, F + 2, 149, B.button(Dir.UP, false));
        k.chair(-349, F + 1, 151, B.DARK_OAK, Dir.NORTH);
        k.chair(-351, F + 1, 151, B.DARK_OAK, Dir.NORTH);
        k.chest(-347, F + 1, 151, Dir.NORTH, Loot.checkpointDesk());
        k.set(-352, F + 1, 152, B.of(B.FLOWER_POT));
        // emblem wall behind reception (south wall z=155): black panel with the site crest
        k.fill(-354, F + 4, 155, -344, F + 7, 155, B.clay(B.BLACK));
        k.text("S7", -346, F + 7, 154, Dir.WEST, B.IRON);
        k.wallBanner(-355, F + 5, 154, Dir.NORTH, B.BLACK, "cs", "red", "flo", "white", "bo", "red");
        k.wallBanner(-343, F + 5, 154, Dir.NORTH, B.BLACK, "cs", "red", "flo", "white", "bo", "red");
        // waiting benches along the north wall
        for (int x = -356; x <= -350; x++) {
            if (x == -354 || x == -353) {
                continue;
            }
            k.stoneChair(x, F + 1, 129, B.STONE_BRICK_STAIRS, Dir.SOUTH);
        }
        for (int x = -349; x <= -342; x++) {
            if (x >= -346 && x <= -343) {
                continue;
            }
            k.stoneChair(x, F + 1, 129, B.STONE_BRICK_STAIRS, Dir.SOUTH);
        }
        // scanner arch in front of the checkpoint lanes
        for (int z = 138; z <= 144; z += 2) {
            k.fill(-355, F + 1, z, -355, F + 3, z, B.BARS);
        }
        k.fill(-355, F + 4, 137, -355, F + 4, 145, B.IRON);
        k.set(-355, F + 5, 141, B.GLOW);
        // light over the checkpoint lanes (both sides of the wall)
        for (int z = 138; z <= 144; z++) {
            k.set(-358, F + 4, z, B.GLOW);
        }
        k.sign(-357, F + 3, 137, Dir.EAST, "§lCHECKPOINT", "§lBRAVO", "Badge scan", "required");
        k.sign(-357, F + 3, 145, Dir.EAST, "§lLEVEL 1", "Security &", "Command", "§8stairs >");
        k.sign(-345, F + 4, 128, Dir.SOUTH, "§4COMMAND", "§4CENTER", "Clearance 5", "");
        k.sign(-354, F + 4, 128, Dir.SOUTH, "North Wing", "Comms  Maps", "Security Ctl", "Briefing");
        k.sign(-349, F + 4, 154, Dir.NORTH, "BARRACKS", "§8>>>", "", "");
        // pillars with hanging lights
        for (int x : new int[]{-353, -345}) {
            for (int z : new int[]{133, 141, 149}) {
                if (z == 141) {
                    k.hangingLight(x, F + 5, z, F + 9, B.BARS, B.GLOW);
                } else {
                    k.fill(x, F + 1, z, x, F + 8, z, B.SB_CHISELED);
                    k.set(x, F + 1, z, B.IRON);
                    k.set(x, F + 4, z, B.GLOW);
                }
            }
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 9, 4, B.LAMP);
        k.c.room("Intake Hall & Checkpoint Bravo", LV, -358, F, 127, -340, F + 9, 155, -350, F + 1, 141);
    }

    private void commandGate() {
        k.set(-347, F + 1, 124, B.IRON);
        k.set(-347, F + 2, 124, B.of(B.DAYLIGHT_SENSOR));
        k.set(-341, F + 1, 124, B.dispenser(Dir.WEST));
        k.set(-341, F + 1, 126, B.dispenser(Dir.WEST));
        k.set(-347, F + 1, 126, B.chest(Dir.EAST));
        k.lamp(-344, F + 6, 125, Dir.UP);
        k.lamp(-346, F + 6, 125, Dir.UP);
        k.lamp(-342, F + 6, 125, Dir.UP);
        k.sign(-347, F + 3, 125, Dir.EAST, "§4SECURITY", "§4AIRLOCK", "One door at", "a time");
        k.c.room("Command Airlock", LV, -348, F, 123, -340, F + 6, 127, -343, F + 1, 125);
    }

    private void commandCenter() {
        int x1 = -347, x2 = -313, z1 = 94, z2 = 122, top = F + 10;
        // floor: slab tiles with dark accent grid lines
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                boolean grid = Math.floorMod(x + 330, 6) == 0 || Math.floorMod(z - 108, 6) == 0;
                k.set(x, F + 1, z, grid ? B.slab(B.SLAB_NETHER, false) : B.slab(B.SLAB_STONE, false));
            }
        }
        // the Big Board on the north wall: world map mosaic with lit markers
        k.fill(-345, F + 2, 93, -315, F + 9, 93, B.clay(B.BLACK));
        for (int x = -344; x <= -316; x++) {
            for (int y = F + 3; y <= F + 8; y++) {
                double n = Math.sin(x * 0.35) + Math.cos(y * 0.9 + x * 0.12) + Math.sin((x + y) * 0.21);
                k.set(x, y, 93, n > 0.9 ? B.clay(B.GREEN) : (n > 0.5 ? B.clay(B.LIME) : B.clay(B.BLUE)));
            }
        }
        int[][] marks = {{-340, F + 6}, {-331, F + 4}, {-324, F + 7}, {-319, F + 5}, {-336, F + 8}};
        for (int[] m : marks) {
            k.lamp(m[0], m[1], 93, Dir.NORTH);
        }
        k.fill(-345, F + 2, 94, -315, F + 2, 94, B.slab(B.SLAB_QUARTZ, true));
        // holo-table around the beam
        for (int x = -334; x <= -326; x++) {
            for (int z = 105; z <= 111; z++) {
                boolean edge = x == -334 || x == -326 || z == 105 || z == 111;
                k.set(x, F, z, edge ? B.IRON : B.SEA);
                if (edge) {
                    Dir out = x == -334 ? Dir.WEST : x == -326 ? Dir.EAST : z == 105 ? Dir.NORTH : Dir.SOUTH;
                    k.set(x, F + 1, z, B.stairs(B.QUARTZ_STAIRS, out.opposite(), true));
                } else {
                    k.set(x, F + 1, z, B.sglass(B.CYAN));
                }
            }
        }
        // holo "projection" ring hanging over the table
        k.ringXZ(-330, F + 7, 108, 2.2, 3.2, B.BARS);
        for (int x = -331; x <= -329; x++) {
            k.set(x, top - 1, 107, B.IRON);
            k.set(x, top - 1, 109, B.IRON);
        }
        // console rows facing north (toward the board)
        int v = 0;
        for (int row = 0; row < 3; row++) {
            int z = 99 + row * 4;
            for (int x = x1 + 5; x <= x2 - 5; x++) {
                if (x >= -336 && x <= -324 && row == 2) {
                    continue; // keep the holo-table clear
                }
                if (x >= -336 && x <= -324 && row == 1) {
                    continue;
                }
                if (Math.floorMod(x + 330, 7) == 0) {
                    continue; // aisles
                }
                k.console(x, F + 1, z, Dir.SOUTH, v++);
                if (Math.floorMod(x, 2) == 0) {
                    k.chair(x, F + 1, z + 1, B.DARK_OAK, Dir.NORTH);
                }
            }
        }
        // raised command deck (south) with the commander's desk
        for (int x = -337; x <= -323; x++) {
            for (int z = 116; z <= 122; z++) {
                k.set(x, F + 1, z, B.SB);
                k.set(x, F + 2, z, z == 116 ? B.slab(B.SLAB_STONEBRICK, false) : B.carpet(B.RED));
            }
        }
        for (int x = -337; x <= -323; x++) {
            k.set(x, F + 1, 115, B.stairs(B.STONE_BRICK_STAIRS, Dir.SOUTH, false));
            if (x == -337 || x == -323 || (x > -335 && x < -325 && Math.floorMod(x, 3) != 0)) {
                k.set(x, F + 2, 116, B.PANE);
            }
        }
        k.set(-330, F + 2, 116, B.slab(B.SLAB_STONEBRICK, false));
        for (int x = -333; x <= -327; x++) {
            k.set(x, F + 2, 118, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
        }
        k.set(-332, F + 3, 118, B.of(B.DAYLIGHT_SENSOR));
        k.set(-328, F + 3, 118, B.lever(Dir.UP, false));
        k.set(-330, F + 2, 120, B.woodStairs(B.DARK_OAK, Dir.SOUTH, false));
        k.set(-331, F + 2, 120, B.woodStairs(B.DARK_OAK, Dir.EAST, true));
        k.set(-329, F + 2, 120, B.woodStairs(B.DARK_OAK, Dir.WEST, true));
        k.chest(-335, F + 2, 121, Dir.NORTH, Loot.commandDesk());
        k.chest(-325, F + 2, 121, Dir.NORTH);
        k.standingBanner(-337, F + 3, 122, 0, B.BLACK, "cs", "red", "flo", "white");
        k.standingBanner(-323, F + 3, 122, 0, B.BLACK, "cs", "red", "flo", "white");
        // hanging work lights over the console rows and the command deck
        for (int x = -343; x <= -317; x += 5) {
            for (int z : new int[]{100, 104, 108}) {
                if (z == 108 && x > -337 && x < -323) {
                    continue;
                }
                k.hangingLight(x, F + 6, z, top, B.BARS, B.GLOW);
            }
        }
        for (int x = -335; x <= -325; x += 5) {
            k.hangingLight(x, F + 6, 119, top, B.BARS, B.GLOW);
        }
        // side balconies (west x -347..-345, east x -315..-313) at y F+5 with glass railing
        balcony(-347, -345, -344, Dir.EAST);
        balcony(-315, -313, -316, Dir.WEST);
        // lighting: ceiling lamp grid and wall uplights
        k.ceilingGrid(x1 + 2, z1 + 2, x2 - 2, z2 - 2, top, 5, B.LAMP);
        for (int z = 97; z <= 121; z += 6) {
            k.set(x1 - 1, F + 3, z, B.GLOW);
            k.set(x2 + 1, F + 3, z, B.GLOW);
        }
        for (int x = -343; x <= -317; x += 6) {
            k.set(x, F + 3, 123, B.GLOW);
        }
        // floor lights near the aisles
        for (int x = -344; x <= -316; x += 7) {
            for (int z = 97; z <= 113; z += 4) {
                if (k.get(x, F + 1, z) == s.floorSlab) {
                    k.set(x, F + 1, z, B.carpet(B.RED));
                }
            }
        }
        k.sign(-330, F + 9, 94, Dir.SOUTH, "§lSITE-7", "§lCOMMAND", "§8DEEPWATCH", "");
        k.c.room("Command Center", LV, -348, F, 93, -312, top, 123, -330, F + 1, 113);
        k.c.marker("holotable", -330, F + 1, 108, "");
    }

    private void balcony(int xa, int xb, int rail, Dir face) {
        int by = F + 5;
        for (int x = xa; x <= xb; x++) {
            for (int z = 95; z <= 116; z++) {
                k.set(x, by, z, B.SB);
                k.set(x, by + 1, z, B.carpet(B.RED));
            }
        }
        for (int z = 95; z <= 116; z++) {
            k.set(rail, by, z, B.SMOOTH);
            k.set(rail, by + 1, z, z == 116 ? B.PANE : B.PANE);
        }
        // stairs up along the wall from z=121 (y F+1) to z=117 (y F+5)
        for (int i = 0; i < 5; i++) {
            int z = 121 - i, y = F + 1 + i;
            for (int x = xa; x <= xb; x++) {
                k.set(x, y, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.NORTH, false));
                for (int yy = F + 1; yy < y; yy++) {
                    k.set(x, yy, z, B.SB);
                }
            }
            k.set(rail, y + 1, z, B.PANE);
        }
        k.set(rail, by + 1, 116, B.A);
        k.set(rail, by, 116, B.SB);
        // wall consoles on the balcony
        int wall = face == Dir.EAST ? xa - 1 : xb + 1;
        for (int z = 96; z <= 114; z += 3) {
            k.set(wall, by + 2, z, B.clay(B.BLACK));
            k.lamp(wall, by + 3, z, face.opposite());
            int cx = face == Dir.EAST ? xa : xb;
            k.set(cx, by + 1, z, B.stairs(B.QUARTZ_STAIRS, face, true));
            k.set(cx, by + 2, z, z % 2 == 0 ? B.lever(Dir.UP, true) : B.button(Dir.UP, false));
        }
    }

    private void northWing() {
        for (int x = -417; x <= -349; x++) {
            k.set(x, F + 1, 125, B.carpet(B.RED));
            if (Math.floorMod(x, 6) == 1) {
                k.lamp(x, F + 6, 125, Dir.UP);
            }
        }
        k.sign(-417, F + 3, 125, Dir.EAST, "NORTH WING", "§8Level 1", "", "");
        k.c.room("North Wing Corridor", LV, -418, F, 123, -348, F + 6, 127, -380, F + 1, 125);
    }

    private void comms() {
        int x1 = -363, x2 = -349, z1 = 106, z2 = 122;
        // radio racks along the west wall: note blocks, jukebox, dispensers as speakers, lamps as indicators
        for (int z = z1 + 1; z <= z2 - 1; z++) {
            k.set(x1, F + 1, z, z % 3 == 0 ? B.of(B.JUKEBOX) : B.of(B.NOTE_BLOCK));
            k.set(x1, F + 2, z, z % 2 == 0 ? B.dispenser(Dir.EAST) : B.IRON);
            k.set(x1, F + 3, z, z % 4 == 0 ? B.LAMP : B.clay(B.BLACK));
            if (z % 4 == 0) {
                k.set(x1 - 1, F + 3, z, B.RS_BLOCK);
            }
        }
        // operator desks facing the racks
        for (int z = z1 + 2; z <= z2 - 2; z += 3) {
            k.set(x1 + 3, F + 1, z, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, true));
            k.set(x1 + 3, F + 2, z, z % 2 == 0 ? B.of(B.DAYLIGHT_SENSOR) : B.lever(Dir.UP, false));
            k.chair(x1 + 4, F + 1, z, B.DARK_OAK, Dir.WEST);
        }
        // antenna feed: ladder up to a small cable vault in the ceiling
        k.fill(x2 - 1, F + 1, z1, x2 - 1, F + 6, z1, B.of(B.NETHER_FENCE));
        k.set(x2 - 2, F + 1, z1 + 1, B.of(B.CAULDRON));
        // glass window onto the Command Center (upper wall x=-348)
        k.fill(-348, F + 2, 108, -348, F + 4, 115, B.PANE);
        k.fill(-348, F + 2, 114, -348, F + 4, 114, B.IRON);
        k.chest(x2, F + 1, z2, Dir.WEST, Loot.all(Loot.officer(1)).toArray(new ItemSpec[0]));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-356, F + 3, 124, Dir.SOUTH, "§lCOMMS", "Signals &", "Radio", "");
        k.c.room("Communications Room", LV, -364, F, 105, -348, F + 7, 123, -356, F + 1, 114);
    }

    private void sigint() {
        int x1 = -363, x2 = -349, z1 = 94, z2 = 104;
        // server-like racks: iron + lamps + note blocks in rows
        for (int x = x1 + 1; x <= x2 - 1; x += 3) {
            for (int z = z1 + 1; z <= z2 - 2; z++) {
                k.set(x, F + 1, z, B.IRON);
                k.set(x, F + 2, z, z % 2 == 0 ? B.of(B.NOTE_BLOCK) : B.clay(B.BLACK));
                k.set(x, F + 3, z, z % 3 == 0 ? B.GLOW : B.IRON);
            }
        }
        k.set(x2, F + 1, z2 - 1, B.WORKBENCH_B);
        k.chest(x1, F + 1, z2 - 1, Dir.EAST, Loot.all(Loot.archive(3)).toArray(new ItemSpec[0]));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-356, F + 3, 106, Dir.SOUTH, "SIGINT", "§4Eyes only", "", "");
        k.c.room("Signals Intelligence", LV, -364, F, 93, -348, F + 7, 105, -356, F + 1, 103);
    }

    private void mapRoom() {
        int x1 = -381, x2 = -365, z1 = 106, z2 = 122;
        // big terrain map table: coloured clay relief with markers
        for (int x = -378; x <= -368; x++) {
            for (int z = 109; z <= 119; z++) {
                boolean edge = x == -378 || x == -368 || z == 109 || z == 119;
                if (edge) {
                    k.set(x, F + 1, z, B.log(B.DARK_OAK, 12));
                    continue;
                }
                double n = Math.sin(x * 0.7) * Math.cos(z * 0.6) + Math.sin((x - z) * 0.3);
                int col = n > 0.8 ? B.clay(B.WHITE) : n > 0.3 ? B.clay(B.GREEN) : n > -0.4 ? B.clay(B.LIME) : B.clay(B.BLUE);
                k.set(x, F + 1, z, col);
            }
        }
        k.set(-374, F + 2, 112, B.of(B.REDSTONE_TORCH, 5));
        k.set(-371, F + 2, 116, B.of(B.REDSTONE_TORCH, 5));
        k.set(-375, F + 2, 117, B.of(B.TORCH, 5));
        k.set(-370, F + 2, 111, B.of(B.FLOWER_POT));
        // benches around
        for (int x = -377; x <= -369; x += 2) {
            k.chair(x, F + 1, 108, B.DARK_OAK, Dir.SOUTH);
            k.chair(x, F + 1, 120, B.DARK_OAK, Dir.NORTH);
        }
        // wall charts (wool mosaics) and bookshelves
        k.fill(x1, F + 2, 110, x1, F + 4, 118, B.BOOKS);
        k.fill(x2, F + 2, 108, x2, F + 5, 112, B.wool(B.WHITE));
        k.fill(x2, F + 3, 109, x2, F + 3, 111, B.wool(B.RED));
        k.fill(x2, F + 2, 116, x2, F + 5, 120, B.wool(B.LIGHT_BLUE));
        k.fill(x2, F + 4, 117, x2, F + 4, 119, B.wool(B.BLACK));
        k.hangingLight(-373, F + 5, 114, F + 7, B.of(B.DARK_OAK_FENCE), B.GLOW);
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 5, B.LAMP);
        k.chest(x1 + 1, F + 1, z1, Dir.SOUTH, Loot.all(Loot.archive(0)).toArray(new ItemSpec[0]));
        k.sign(-373, F + 3, 124, Dir.SOUTH, "MAP ROOM", "Planning &", "Operations", "");
        k.c.room("Map & Planning Room", LV, -382, F, 105, -364, F + 7, 123, -373, F + 1, 121);
    }

    private void directorOffice() {
        int x1 = -381, x2 = -365, z1 = 92, z2 = 104;
        Style w = s.copy();
        k.rug(-378, 94, -368, 102, F, B.carpet(B.RED));
        k.fill(x1 - 1, F + 1, z1, x1 - 1, F + 5, z2, B.planks(B.DARK_OAK));
        // desk
        for (int x = -376; x <= -370; x++) {
            k.set(x, F + 1, 97, B.woodStairs(B.DARK_OAK, Dir.NORTH, true));
        }
        k.set(-376, F + 2, 97, B.of(B.FLOWER_POT));
        k.set(-370, F + 2, 97, B.GLOW);
        k.set(-373, F + 1, 95, B.woodStairs(B.DARK_OAK, Dir.NORTH, false));
        k.set(-373, F + 1, 99, B.woodStairs(B.DARK_OAK, Dir.NORTH, false));
        k.set(-375, F + 1, 99, B.woodStairs(B.DARK_OAK, Dir.NORTH, false));
        k.chest(-378, F + 1, 93, Dir.SOUTH, Loot.commandDesk());
        // bookshelves and the painting that hides the war room (west wall x=-382)
        k.fill(-381, F + 1, 93, -381, F + 3, 95, B.BOOKS);
        k.fill(-381, F + 1, 101, -381, F + 3, 103, B.BOOKS);
        // secret passage through the west wall at z=98 (2 high), filled with wall signs, covered by a painting
        secretPaintingPassage(-382, 98, Dir.EAST, "WANDERER", B.planks(B.DARK_OAK), "§8...", "Keep the",
                "Wanderer on", "the wall.");
        k.set(-374, F + 1, 103, B.of(B.JUKEBOX));
        k.ceilingGrid(x1 + 2, z1 + 2, x2 - 2, z2 - 2, F + 7, 4, B.LAMP);
        k.set(-366, F + 3, 98, B.GLOW);
        k.wallBanner(-373, F + 4, 104, Dir.NORTH, B.BLACK, "cs", "red", "flo", "white", "bo", "red");
        k.sign(-373, F + 3, 106, Dir.SOUTH, "§lDIRECTOR", "Office", "§8Knock first", "");
        k.c.room("Director's Office", LV, -382, F, 91, -364, F + 7, 105, -373, F + 1, 101);
    }

    /**
     * 1x2 secret passage through a wall along z (wall plane x = wx). Cells (wx, F+1..F+2, z) hold wall signs so the
     * painting in front of them survives while players walk straight through.
     */
    void secretPaintingPassage(int wx, int z, Dir roomSide, String art, int wallBlock, String... signText) {
        // signs face into the hidden side, attached to the block on the room side? No: attach to the wall block
        // beside the gap (z+1) so both faces stay clear.
        k.set(wx, F + 1, z, B.wallSign(Dir.NORTH));
        k.c.tile(new TileSpec.SignText(wx, F + 1, z, new String[]{signText[0], signText[1], signText[2], signText[3]}));
        k.set(wx, F + 2, z, B.wallSign(Dir.NORTH));
        k.c.tile(new TileSpec.SignText(wx, F + 2, z, new String[]{"", "", "", ""}));
        k.set(wx, F + 1, z + 1, wallBlock);
        k.set(wx, F + 2, z + 1, wallBlock);
        k.set(wx, F + 3, z, wallBlock);
        k.set(wx, F, z, B.GLOW); // not a spawnable surface, and lights the gap
        k.set(wx, F + 1, z - 1, wallBlock);
        k.set(wx, F + 2, z - 1, wallBlock);
        k.painting(wx + roomSide.dx, F + 1, z, roomSide, art);
        k.c.marker("secret-passage", wx, F + 1, z, art);
    }

    private void warRoom() {
        int x1 = -399, x2 = -383, z1 = 92, z2 = 104;
        k.fill(x1, F + 1, z1, x2, F + 1, z2, B.carpet(B.BLACK));
        // round table
        k.diskXZ(-391, F + 1, 98, 2.6, B.of(B.DARK_OAK_FENCE));
        k.diskXZ(-391, F + 2, 98, 2.6, B.of(B.WOOD_PLATE));
        k.set(-391, F + 1, 98, B.log(B.DARK_OAK, 0));
        k.set(-391, F + 2, 98, B.GLOW);
        for (int a = 0; a < 8; a++) {
            double ang = a * Math.PI / 4;
            int x = (int) Math.round(-391 + 4 * Math.cos(ang)), z = (int) Math.round(98 + 4 * Math.sin(ang));
            Dir face = Math.abs(Math.cos(ang)) > Math.abs(Math.sin(ang)) ? (Math.cos(ang) > 0 ? Dir.WEST : Dir.EAST)
                    : (Math.sin(ang) > 0 ? Dir.NORTH : Dir.SOUTH);
            k.chair(x, F + 1, z, B.DARK_OAK, face);
        }
        // maps on the wall, weapons, emergency supplies
        k.fill(x1 - 1, F + 2, 94, x1 - 1, F + 5, 102, B.clay(B.BLACK));
        k.fill(x1 - 1, F + 3, 96, x1 - 1, F + 4, 100, B.wool(B.WHITE));
        k.set(x1 - 1, F + 4, 97, B.wool(B.RED));
        k.set(x1 - 1, F + 3, 99, B.wool(B.RED));
        k.chest(x2 - 1, F + 1, z1, Dir.SOUTH, Loot.warRoom());
        k.set(x2 - 2, F + 1, z1, B.enderChest(Dir.SOUTH));
        k.itemFrame(x1, F + 3, z2, Dir.NORTH, ItemSpec.of(345, 1));
        k.itemFrame(x1 + 2, F + 3, z2, Dir.NORTH, ItemSpec.of(347, 1));
        k.ceilingGrid(x1 + 2, z1 + 2, x2 - 2, z2 - 2, F + 7, 4, B.LAMP);
        k.c.secretRoom("SECRET: Director's War Room", LV, -400, F, 91, -382, F + 7, 105, -390, F + 1, 103);
    }

    private void securityControl() {
        int x1 = -399, x2 = -383, z1 = 106, z2 = 122;
        // CCTV wall on the north side: grid of "monitors"
        for (int x = x1 + 1; x <= x2 - 1; x++) {
            for (int y = F + 2; y <= F + 5; y++) {
                boolean frame = Math.floorMod(x - x1, 3) == 0 || y == F + 2 || y == F + 5;
                k.set(x, y, 105, frame ? B.clay(B.BLACK) : (Canvas.rand(x, y, 105, 3) < 0.5 ? B.sglass(B.GREEN)
                        : B.sglass(B.GRAY)));
            }
        }
        for (int x = x1 + 2; x <= x2 - 1; x += 3) {
            k.set(x, F + 3, 104, B.GLOW);
            k.set(x, F + 4, 104, B.GLOW);
        }
        // control desk (curved)
        for (int x = x1 + 2; x <= x2 - 2; x++) {
            k.set(x, F + 1, 109, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
            k.set(x, F + 2, 109, Math.floorMod(x, 3) == 0 ? B.lever(Dir.UP, Math.floorMod(x, 2) == 0)
                    : (Math.floorMod(x, 3) == 1 ? B.button(Dir.UP, false) : B.of(B.DAYLIGHT_SENSOR)));
            if (Math.floorMod(x, 3) == 0) {
                k.chair(x, F + 1, 110, B.DARK_OAK, Dir.NORTH);
            }
        }
        // alarm light switch: lever toggling lamps over the desk
        k.set(x1 - 1, F + 2, 112, B.SB);
        k.set(x1 - 1, F + 2, 111, B.of(B.LAMP_ON));
        k.set(x1 - 1, F + 2, 113, B.of(B.LAMP_ON));
        k.set(x1, F + 2, 112, B.lever(Dir.EAST, true));
        k.c.marker("lighttoggle", x1, F + 2, 112, "security alarm lamps");
        // weapon lockers / evidence
        k.chest(x2 - 1, F + 1, z2, Dir.NORTH, Loot.guardRoom());
        k.chest(x2 - 2, F + 1, z2, Dir.NORTH);
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-391, F + 3, 124, Dir.SOUTH, "§lSECURITY", "§lCONTROL", "CCTV / Alarms", "");
        k.c.room("Security Control Room", LV, -400, F, 105, -382, F + 7, 123, -391, F + 1, 116);
    }

    private void briefing() {
        int x1 = -417, x2 = -401, z1 = 106, z2 = 122;
        // lectern + screen on the west wall, rows of seats
        k.fill(x1 - 1, F + 2, 109, x1 - 1, F + 5, 119, B.clay(B.BLACK));
        k.fill(x1 - 1, F + 3, 110, x1 - 1, F + 4, 118, B.wool(B.WHITE));
        k.set(x1 + 1, F + 1, 114, B.stairs(B.QUARTZ_STAIRS, Dir.WEST, true));
        k.set(x1 + 1, F + 2, 114, B.of(B.FLOWER_POT));
        for (int x = x1 + 4; x <= x2 - 1; x += 2) {
            for (int z = z1 + 2; z <= z2 - 2; z++) {
                if (z == 114) {
                    continue;
                }
                k.chair(x, F + 1, z, B.SPRUCE, Dir.WEST);
            }
        }
        k.wallBanner(x1, F + 4, z1, Dir.SOUTH, B.RED, "bo", "black", "cr", "black");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-409, F + 3, 124, Dir.SOUTH, "BRIEFING", "Room", "", "");
        k.c.room("Briefing Room", LV, -418, F, 105, -400, F + 7, 123, -405, F + 1, 114);
    }

    private void securityStorage() {
        int x1 = -417, x2 = -401, z1 = 92, z2 = 104;
        String[] labels = {"Radios", "Batteries", "Uniforms", "Riot gear", "Ammunition", "Rations"};
        k.storageRow(x1 + 1, x2 - 1, z1, F + 1, Dir.SOUTH, B.IRON, labels, 2);
        k.chest(x1, F + 1, 100, Dir.EAST, Loot.quartermaster(0));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.c.room("Security Storage", LV, -418, F, 91, -400, F + 7, 105, -409, F + 1, 100);
    }

    private void lockers() {
        int x1 = -383, x2 = -359, z1 = 128, z2 = 136;
        // lockers: iron trapdoor faced cabinets two high along the north wall
        for (int x = x1 + 1; x <= x2 - 1; x++) {
            if (x == -378 || x == -377) {
                continue;
            }
            k.set(x, F + 1, z1, B.chest(Dir.SOUTH));
            k.set(x, F + 2, z1, B.of(B.IRON_TRAPDOOR, 3 | 4));
            if (Math.floorMod(x, 3) == 0) {
                k.set(x, F + 1, z1, B.IRON);
                k.set(x, F + 2, z1, B.IRON);
            }
        }
        // centre benches
        for (int x = x1 + 3; x <= x2 - 3; x++) {
            if (x == -371 || x == -370) {
                continue;
            }
            k.set(x, F + 1, 132, B.slab(B.SLAB_STONEBRICK, true));
        }
        // gear racks: armour stands in field uniforms
        for (int x = x1 + 2; x <= x2 - 2; x += 5) {
            EntitySpec e = k.armorStand(x, F + 1, z2, Dir.NORTH);
            e.helmet = Loot.leather(298, Loot.OLIVE, "Field Helmet");
            e.chest = Loot.leather(299, Loot.OLIVE, "Field Jacket");
            e.legs = Loot.leather(300, Loot.OLIVE, "Field Trousers");
            e.boots = Loot.leather(301, Loot.BLACK, "Boots");
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-371, F + 3, 136, Dir.NORTH, "EQUIPMENT", "LOCKERS", "", "");
        k.c.room("Equipment Lockers", LV, -384, F, 127, -358, F + 7, 137, -371, F + 1, 134);
    }

    private void guardQuarters() {
        int x1 = -417, x2 = -385, z1 = 128, z2 = 136;
        // bunks along north wall
        for (int x = x1 + 1; x <= x2 - 1; x += 3) {
            if (x >= -396 && x <= -393) {
                continue;
            }
            k.bed(x, F + 1, z1, Dir.SOUTH);
            k.chest(x + 1, F + 1, z1, Dir.SOUTH, Loot.crewLocker(x));
        }
        // table and seating in the middle, lockers south wall
        for (int x = -412; x <= -406; x += 3) {
            k.postTable(x, F + 1, 133, B.SPRUCE_FENCE);
            k.chair(x - 1, F + 1, 133, B.SPRUCE, Dir.EAST);
            k.chair(x + 1, F + 1, 133, B.SPRUCE, Dir.WEST);
        }
        k.set(x2 - 1, F + 1, z2, B.WORKBENCH_B);
        k.set(x2 - 2, F + 1, z2, B.furnace(Dir.NORTH));
        k.set(x2 - 3, F + 1, z2, B.of(B.JUKEBOX));
        k.rug(-396, 131, -386, 134, F, B.carpet(B.GRAY));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-402, F + 3, 136, Dir.NORTH, "GUARD", "QUARTERS", "", "");
        k.c.room("Guard Quarters", LV, -418, F, 127, -384, F + 7, 137, -402, F + 1, 133);
    }

    private void spine() {
        int x1 = -419, x2 = -359;
        // red centre stripe and wall rhythm
        for (int x = x1; x <= x2; x++) {
            k.set(x, F + 1, 141, B.carpet(B.RED));
            if (Math.floorMod(x, 6) == 0) {
                k.set(x, F + 6, 138, B.IRON);
                k.set(x, F + 6, 144, B.IRON);
                k.fill(x, F + 6, 139, x, F + 6, 143, B.slab(B.SLAB_STONEBRICK, true));
                k.lamp(x, F + 6, 141, Dir.UP);
            }
        }
        // decorative blast door at the west end
        k.fill(-420, F + 1, 138, -420, F + 5, 144, B.IRON);
        k.fill(-419, F + 1, 138, -419, F + 1, 144, B.clay(B.YELLOW));
        for (int z = 138; z <= 144; z++) {
            k.set(-419, F + 1, z, B.clay(z % 2 == 0 ? B.YELLOW : B.BLACK));
        }
        k.sign(-419, F + 3, 141, Dir.EAST, "§4SEALED", "Blast door B", "§8maintenance", "");
        k.c.room("Level 1 Main Corridor", LV, -420, F, 137, -358, F + 6, 145, -390, F + 1, 141);
    }

    private void readyRoom() {
        int x1 = -365, x2 = -359, z1 = 146, z2 = 154;
        k.set(x1, F + 1, z1, B.chest(Dir.EAST));
        k.set(x1, F + 1, z1 + 1, B.chest(Dir.EAST));
        k.c.tile(new TileSpec.Inventory(x1, F + 1, z1, Painter.spread(Loot.rations(), 27, x1, F, z1)));
        k.set(x2, F + 1, z1 + 2, B.of(B.BREWING_STAND));
        k.set(x2, F + 1, z1 + 3, B.of(B.CAULDRON, 3));
        for (int z = z1 + 3; z <= z2 - 2; z++) {
            k.stoneChair(x1, F + 1, z, B.STONE_BRICK_STAIRS, Dir.EAST);
        }
        k.fill(-362, F + 1, 150, -362, F + 1, 151, B.slab(B.SLAB_QUARTZ, true));
        k.ceilingGrid(x1, z1 + 1, x2, z2 - 1, F + 7, 3, B.LAMP);
        k.sign(-362, F + 3, 144, Dir.NORTH, "READY ROOM", "§8combat prep", "", "");
        k.c.room("Ready Room", LV, -366, F, 145, -358, F + 7, 155, -362, F + 1, 152);
    }

    private void quartermaster() {
        int x1 = -380, x2 = -359, z1 = 156, z2 = 168;
        // issue counter
        for (int x = x1 + 2; x <= x2 - 2; x++) {
            if (x == -370) {
                continue;
            }
            k.set(x, F + 1, 162, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
        }
        k.set(-370, F + 1, 162, B.fenceGate(B.SPRUCE_FENCE_GATE, Dir.NORTH, false));
        // shelving behind the counter
        String[] labels = {"Rations", "Tools", "Uniforms", "Misc", "Torches", "Rope", "Spare"};
        k.storageRow(x1 + 1, x2 - 1, z2, F + 1, Dir.NORTH, B.planks(B.SPRUCE), labels, 2);
        k.c.tile(new TileSpec.Inventory(x1 + 1, F + 1, z2, Painter.spread(Loot.quartermaster(0), 27, 1, 2, 3)));
        k.c.tile(new TileSpec.Inventory(x1 + 4, F + 1, z2, Painter.spread(Loot.quartermaster(1), 27, 4, 2, 3)));
        k.c.tile(new TileSpec.Inventory(x1 + 7, F + 1, z2, Painter.spread(Loot.quartermaster(2), 27, 7, 2, 3)));
        k.c.tile(new TileSpec.Inventory(x1 + 10, F + 1, z2, Painter.spread(Loot.quartermaster(3), 27, 9, 2, 3)));
        k.set(x2, F + 1, 164, B.anvil(false));
        k.set(x2, F + 1, 166, B.WORKBENCH_B);
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.c.room("Quartermaster Stores", LV, -381, F, 155, -358, F + 7, 169, -370, F + 1, 158);
    }

    private void armory() {
        int x1 = -405, x2 = -382, z1 = 146, z2 = 168;
        // floor: diamond plate look (iron blocks under slabs are not visible) -> hazard border
        for (int x = x1; x <= x2; x++) {
            k.set(x, F + 1, z1, B.carpet(B.GRAY));
        }
        // weapon racks on the west wall: item frames with weapons above chests
        int n = 0;
        for (int z = z1 + 2; z <= z2 - 2; z += 2) {
            k.chest(x1, F + 1, z, Dir.EAST, n % 2 == 0 ? Loot.armoryWeapons() : new ItemSpec[0]);
            k.itemFrame(x1, F + 3, z, Dir.EAST, n % 3 == 0 ? ItemSpec.of(261, 1) : ItemSpec.of(n % 3 == 1 ? 267 : 272, 1));
            n++;
        }
        // armour racks: stands on the east side
        for (int z = z1 + 2; z <= z2 - 6; z += 3) {
            EntitySpec e = k.armorStand(x2 - 1, F + 1, z, Dir.WEST);
            e.helmet = Loot.leather(298, Loot.BLACK, "Tactical Helmet");
            e.chest = Loot.leather(299, Loot.BLACK, "Tactical Vest");
            e.legs = Loot.leather(300, Loot.NAVY, "Tactical Trousers");
            e.boots = Loot.leather(301, Loot.BLACK, "Tactical Boots");
            e.hand = ItemSpec.of(z % 2 == 0 ? 261 : 267, 1);
        }
        // issue counter in the middle
        for (int x = -399; x <= -388; x++) {
            k.set(x, F + 1, 156, B.stairs(B.STONE_BRICK_STAIRS, Dir.SOUTH, true));
            k.set(x, F + 1, 157, B.stairs(B.STONE_BRICK_STAIRS, Dir.NORTH, true));
        }
        k.chest(-394, F + 2, 156, Dir.NORTH, Loot.armoryArmor());
        k.set(-390, F + 2, 157, B.of(B.BREWING_STAND));
        k.set(-397, F + 2, 157, B.anvil(true));
        // explosives cage (TNT behind iron bars, no redstone anywhere nearby)
        k.fill(-405, F + 1, 164, -401, F + 3, 164, B.BARS);
        k.fill(-401, F + 1, 165, -401, F + 3, 168, B.BARS);
        k.set(-401, F + 1, 166, B.fenceGate(B.SPRUCE_FENCE_GATE, Dir.EAST, false));
        k.set(-401, F + 2, 166, B.BARS);
        k.fill(-405, F + 1, 166, -403, F + 1, 168, B.of(B.TNT));
        k.set(-404, F + 2, 167, B.of(B.TNT));
        k.chest(-405, F + 1, 165, Dir.EAST, Loot.armoryExplosives());
        k.sign(-401, F + 4, 164, Dir.NORTH, "§4EXPLOSIVES", "§4No flames", "§4No redstone", "");
        k.c.marker("explosives", -404, F + 1, 167, "TNT cage: no power sources nearby");
        // lockers along the south wall: 8 iron-door lockers; locker 5 is the secret annex entrance
        k.fill(-401, F + 1, z2 + 2, -385, F + 3, z2 + 3, s.wall);
        for (int i = 0; i < 8; i++) {
            int x = -400 + i * 2;
            k.set(x, F + 1, z2 + 1, B.doorLower(B.IRON_DOOR, Dir.SOUTH, false));
            k.set(x, F + 2, z2 + 1, B.doorUpper(B.IRON_DOOR, false));
            k.set(x, F + 3, z2 + 1, B.IRON);
            k.set(x, F + 3, z2, B.lever(Dir.NORTH, false));
            k.set(x + 1, F + 1, z2 + 1, B.IRON);
            k.set(x + 1, F + 2, z2 + 1, B.IRON);
            k.set(x + 1, F + 3, z2 + 1, B.IRON);
            k.c.marker("irondoor-lever", x, F + 1, z2 + 1, "locker " + (i + 1));
            if (i != 4) {
                // locker niche with a chest
                k.set(x, F + 1, z2 + 2, B.chest(Dir.NORTH));
                k.set(x, F + 2, z2 + 2, B.A);
            }
        }
        k.set(-401, F + 1, 169, B.IRON);
        k.set(-401, F + 2, 169, B.IRON);
        k.set(-401, F + 3, 169, B.IRON);
        k.sign(-394, F + 4, 168, Dir.NORTH, "PERSONAL", "LOCKERS", "§8lever above", "§8each door");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.wallBanner(-394, F + 5, 146, Dir.SOUTH, B.BLACK, "cs", "red", "flo", "white", "bo", "red");
        k.sign(-395, F + 3, 144, Dir.NORTH, "§lARMORY", "§4Authorized", "§4personnel", "");
        k.c.room("Armory", LV, -406, F, 145, -381, F + 7, 169, -394, F + 1, 150);
    }

    private void annex() {
        // locker 5 (x=-392) opens into a 1-wide passage that leads south into the annex room
        int x = -392;
        for (int z = 170; z <= 172; z++) {
            k.set(x, F + 1, z, B.slab(B.SLAB_STONE, false));
            k.set(x, F + 2, z, B.A);
        }
        k.set(x, F + 3, 170, B.lever(Dir.SOUTH, false));
        k.set(x, F + 3, 171, B.A);
        k.set(x, F + 3, 172, B.A);
        int x1 = -399, x2 = -387, z1 = 173, z2 = 179;
        // the annex: weapon racks, special armour
        k.fill(x1, F + 1, z2, x2, F + 1, z2, B.of(B.DARK_OAK_FENCE));
        k.chest(x1, F + 1, z1 + 1, Dir.EAST, Loot.armoryAnnex());
        k.set(x1, F + 1, z1 + 2, B.anvil(false));
        EntitySpec e = k.armorStand(x2, F + 1, 176, Dir.WEST);
        e.helmet = ItemSpec.of(302, 1);
        e.chest = ItemSpec.of(303, 1);
        e.legs = ItemSpec.of(304, 1);
        e.boots = ItemSpec.of(305, 1);
        e.hand = ItemSpec.of(267, 1);
        k.itemFrame(x1 + 3, F + 3, z2 + 1, Dir.NORTH, ItemSpec.of(261, 1));
        k.itemFrame(x1 + 6, F + 3, z2 + 1, Dir.NORTH, ItemSpec.of(276, 1));
        k.lamp(-393, F + 6, 176, Dir.UP);
        k.lamp(-397, F + 6, 176, Dir.UP);
        k.lamp(-389, F + 6, 176, Dir.UP);
        k.wallBanner(-393, F + 4, z2, Dir.NORTH, B.BLACK, "sku", "white", "bo", "red");
        k.c.secretRoom("SECRET: Armory Annex", LV, -400, F, 172, -386, F + 6, 180, -393, F + 1, 175);
    }

    private void range() {
        int x1 = -431, x2 = -407, z1 = 146, z2 = 168;
        // shooting line with booth partitions at x=-411
        for (int z = z1; z <= z2; z++) {
            k.set(-411, F + 1, z, B.slab(B.SLAB_STONEBRICK, true));
            if (Math.floorMod(z - z1, 4) == 0) {
                k.set(-411, F + 2, z, B.PANE);
                k.set(-411, F + 3, z, B.PANE);
                k.fill(-410, F + 2, z, -409, F + 3, z, B.PANE);
            }
        }
        k.set(-411, F + 1, 151, B.slab(B.SLAB_STONE, false));
        k.set(-411, F + 1, 152, B.slab(B.SLAB_STONE, false));
        k.set(-411, F + 2, 152, B.A);
        k.set(-411, F + 3, 152, B.A);
        // targets on the west wall: bullseye made of wool on hay
        for (int z = z1 + 2; z <= z2 - 2; z += 4) {
            k.fill(x1, F + 2, z - 1, x1, F + 4, z + 1, B.of(B.HAY_BALE));
            k.set(x1, F + 3, z, B.wool(B.RED));
            k.set(x1 + 1, F + 3, z, B.A);
        }
        // sand-bag berm in front of the targets
        k.fill(x1 + 1, F + 1, z1, x1 + 1, F + 1, z2, B.wool(B.BROWN));
        k.chest(x2, F + 1, z2, Dir.WEST, Loot.armoryWeapons());
        k.set(x2, F + 1, z2 - 1, B.dispenser(Dir.WEST));
        // bright range lighting
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-407, F + 3, 153, Dir.EAST, "FIRING RANGE", "§4Eye & ear", "§4protection", "");
        k.c.room("Firing Range", LV, -432, F, 145, -406, F + 7, 169, -409, F + 1, 157);
    }

    private void barracks() {
        int x1 = -357, x2 = -331, z1 = 156, z2 = 176;
        // bunk rows: bed on the floor, second bed on a platform above (bunk)
        int n = 0;
        for (int x = x1 + 1; x <= x2 - 1; x += 3) {
            for (int zi : new int[]{z1, z2 - 1}) {
                Dir head = zi == z1 ? Dir.NORTH : Dir.SOUTH;
                int zf = zi == z1 ? z1 + 1 : z2 - 1;
                k.bed(x, F + 1, zf, head);
                k.set(x, F + 3, zf, B.woodSlab(B.SPRUCE, true));
                k.set(x, F + 3, zf + head.dz, B.woodSlab(B.SPRUCE, true));
                k.set(x, F + 4, zf, B.bed(head, false));
                k.set(x, F + 4, zf + head.dz, B.bed(head, true));
                k.set(x + 1, F + 1, zf + head.dz, B.chest(head.opposite()));
                k.c.tile(new TileSpec.Inventory(x + 1, F + 1, zf + head.dz,
                        Painter.spread(Loot.crewLocker(n++), 27, x, F, zf)));
            }
        }
        // central aisle rug, tables
        k.rug(x1 + 1, 164, x2 - 1, 168, F, B.carpet(B.GREEN));
        for (int x = x1 + 3; x <= x2 - 3; x += 5) {
            k.postTable(x, F + 1, 166, B.SPRUCE_FENCE);
            k.chair(x, F + 1, 165, B.SPRUCE, Dir.SOUTH);
            k.chair(x, F + 1, 167, B.SPRUCE, Dir.NORTH);
        }
        // wash corner (east end)
        k.fill(x2, F + 1, 162, x2, F + 1, 170, B.QUARTZ);
        for (int z = 163; z <= 169; z += 2) {
            k.set(x2 - 1, F + 1, z, B.of(B.CAULDRON, 3));
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-348, F + 3, 156, Dir.SOUTH, "BARRACKS", "Lights out 2200", "", "");
        k.c.room("Barracks", LV, -358, F, 155, -330, F + 7, 177, -344, F + 1, 166);
    }
}
