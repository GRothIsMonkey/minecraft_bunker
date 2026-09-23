package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/** LEVEL 3 - Science & Medical (floor y=35). Avoids the reactor, hangar and storage hall volumes. */
public final class Level3 {
    private final Kit k;
    private final Style s = Style.science();
    private static final int F = L3;
    private static final String LV = "Level 3";

    public Level3(Kit k) {
        this.k = k;
    }

    public void build() {
        Style med = medStyle();
        k.shell(-424, 137, -340, 145, F, 5, s, 6);         // spine
        k.shell(-350, 123, -310, 137, F, 6, s, 6);         // reactor observation gallery
        k.shell(-424, 109, -350, 113, F, 5, s, 6);         // lab corridor
        k.shell(-372, 113, -350, 137, F, 6, s, 6);         // chemistry lab
        k.shell(-398, 113, -372, 137, F, 6, s, 6);         // analysis lab
        k.shell(-424, 113, -398, 137, F, 6, archiveStyle(), 4); // archive
        k.shell(-436, 119, -424, 131, F, 5, archiveStyle(), 0); // hidden records
        k.shell(-372, 89, -350, 109, F, 6, s, 6);          // specimen research
        k.shell(-398, 89, -372, 109, F, 6, s, 6);          // clean room
        k.shell(-424, 89, -398, 109, F, 6, s, 6);          // sample storage / offices
        k.shell(-366, 145, -341, 163, F, 6, med, 6);       // infirmary ward
        k.shell(-366, 163, -354, 181, F, 6, med, 6);       // surgery
        k.shell(-354, 163, -341, 172, F, 6, med, 0);       // pharmacy
        k.shell(-354, 172, -341, 181, F, 6, med, 0);       // medical storage
        k.shell(-382, 153, -366, 181, F, 6, quarantineStyle(), 4); // quarantine & decon
        openings(med);
        spine();
        gallery();
        labCorridor();
        chemistry();
        analysis();
        archive();
        hiddenRecords();
        specimens();
        cleanRoom();
        sampleStorage();
        infirmary();
        surgery();
        pharmacy();
        medStorage();
        quarantine();
    }

    static Style medStyle() {
        Style m = Style.science();
        m.wallLow = B.clay(B.WHITE);
        m.accent = B.clay(B.RED);
        m.carpet = B.carpet(B.WHITE);
        return m;
    }

    static Style archiveStyle() {
        Style a = Style.science();
        a.wall = B.planks(B.DARK_OAK);
        a.wallLow = B.planks(B.DARK_OAK);
        a.wallHigh = B.planks(B.SPRUCE);
        a.pillar = B.log(B.DARK_OAK, 0);
        a.floorSlab = B.woodSlab(B.DARK_OAK, false);
        a.ceiling = B.planks(B.SPRUCE);
        return a;
    }

    static Style quarantineStyle() {
        Style q = Style.science();
        q.wall = B.clay(B.WHITE);
        q.wallLow = B.clay(B.YELLOW);
        q.wallHigh = B.clay(B.WHITE);
        q.pillar = B.IRON;
        return q;
    }

    private void openings(Style med) {
        k.opening(-348, 137, -342, 137, F, 4, s);     // spine -> gallery
        k.ironDoubleDoor(-362, F + 1, 137, Dir.NORTH); // spine -> chemistry
        k.ironDoubleDoor(-386, F + 1, 137, Dir.NORTH); // spine -> analysis
        k.opening(-412, 137, -410, 137, F, 3, s);     // spine -> archive
        k.opening(-362, 113, -361, 113, F, 3, s);     // chem -> lab corridor
        k.opening(-386, 113, -385, 113, F, 3, s);     // analysis -> lab corridor
        k.opening(-411, 113, -410, 113, F, 3, s);     // archive -> lab corridor
        k.ironDoor(-361, F + 1, 109, Dir.NORTH, true); // specimen
        k.ironDoor(-386, F + 1, 109, Dir.NORTH, true); // clean room (airlock inside)
        k.opening(-412, 109, -411, 109, F, 3, s);     // sample storage
        k.ironDoubleDoor(-356, F + 1, 145, Dir.NORTH); // spine -> infirmary
        k.opening(-360, 163, -358, 163, F, 3, med);   // infirmary -> surgery
        k.door(-348, F + 1, 163, B.BIRCH_DOOR, Dir.SOUTH, false); // infirmary -> pharmacy
        k.door(-347, F + 1, 172, B.BIRCH_DOOR, Dir.SOUTH, false); // pharmacy -> storage
        k.ironDoor(-366, F + 1, 158, Dir.WEST, true);  // infirmary -> quarantine (decon)
    }

    private void spine() {
        for (int x = -423; x <= -341; x++) {
            k.set(x, F + 1, 141, B.carpet(B.LIGHT_BLUE));
            if (Math.floorMod(x, 5) == 0) {
                k.set(x, F + 6, 141, B.SEA);
            }
        }
        k.sign(-423, F + 3, 141, Dir.EAST, "§lLEVEL 3", "Science &", "Medical", "");
        k.c.room("Level 3 Main Corridor", LV, -424, F, 137, -340, F + 6, 145, -380, F + 1, 141);
    }

    private void gallery() {
        int x1 = -349, x2 = -311, z1 = 124, z2 = 136;
        // tiered viewing benches facing the reactor windows (north)
        for (int x = x1 + 2; x <= x2 - 2; x++) {
            if (Math.floorMod(x, 8) == 0) {
                continue;
            }
            k.set(x, F + 1, 130, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, false));
            k.set(x, F + 1, 133, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, false));
        }
        // railing in front of the glass
        for (int x = x1 + 1; x <= x2 - 1; x++) {
            k.set(x, F + 1, 125, B.of(B.NETHER_FENCE));
        }
        // info plaques
        k.sign(-330, F + 3, 136, Dir.NORTH, "§lREACTOR", "§lOBSERVATION", "Output: 4.1 GW", "§8Conduit: OK");
        k.sign(-340, F + 3, 136, Dir.NORTH, "The Conduit", "beam carries", "OMEGA field", "§8to surface");
        k.sign(-320, F + 3, 136, Dir.NORTH, "§4DO NOT", "§4tap on glass", "", "");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 5, B.SEA);
        k.c.room("Reactor Observation Gallery", LV, -350, F, 123, -310, F + 7, 137, -330, F + 1, 134);
    }

    private void labCorridor() {
        for (int x = -423; x <= -351; x++) {
            k.set(x, F + 1, 111, B.carpet(B.WHITE));
            if (Math.floorMod(x, 5) == 0) {
                k.set(x, F + 6, 111, B.SEA);
            }
        }
        // decontamination arch mid-corridor
        k.fill(-385, F + 1, 110, -385, F + 4, 110, B.IRON);
        k.fill(-385, F + 1, 112, -385, F + 4, 112, B.IRON);
        k.fill(-385, F + 5, 110, -385, F + 5, 112, B.IRON);
        k.set(-385, F + 4, 111, B.dispenser(Dir.DOWN));
        k.sign(-351, F + 3, 111, Dir.WEST, "CLEAN CORRIDOR", "§8Labs", "", "");
        k.c.room("Lab Corridor", LV, -424, F, 109, -350, F + 6, 113, -400, F + 1, 111);
    }

    private void chemistry() {
        int x1 = -371, x2 = -351, z1 = 114, z2 = 136;
        // lab benches (quartz counters) in rows, with brewing stands and cauldrons
        for (int row = 0; row < 3; row++) {
            int z = 118 + row * 6;
            for (int x = x1 + 3; x <= x2 - 3; x++) {
                k.set(x, F + 1, z, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
                k.set(x, F + 1, z + 1, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
                int m = Math.floorMod(x, 4);
                if (m == 0) {
                    k.set(x, F + 2, z, B.of(B.BREWING_STAND));
                } else if (m == 2) {
                    k.set(x, F + 2, z + 1, B.of(B.FLOWER_POT));
                }
            }
            k.set(x1 + 3, F + 1, z, B.of(B.CAULDRON, 3));
            k.set(x2 - 3, F + 1, z + 1, B.of(B.CAULDRON, 2));
        }
        // fume hoods along the west wall
        for (int z = z1 + 2; z <= z2 - 2; z += 4) {
            k.set(x1, F + 1, z, B.stairs(B.QUARTZ_STAIRS, Dir.WEST, true));
            k.set(x1, F + 2, z, B.of(B.BREWING_STAND));
            k.set(x1, F + 3, z, B.spane(B.LIGHT_BLUE));
            k.set(x1, F + 4, z, B.of(B.HOPPER, 0));
        }
        k.chest(x2, F + 1, z1, Dir.WEST, Loot.labChem());
        k.chest(x2, F + 1, z1 + 1, Dir.WEST, Loot.brewing());
        k.chest(x2, F + 1, z2, Dir.WEST);
        k.set(x2, F + 1, z2 - 1, B.WORKBENCH_B);
        // eyewash / safety
        k.sign(x2, F + 3, 128, Dir.WEST, "§4GOGGLES", "§4REQUIRED", "", "");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.SEA);
        k.c.room("Chemistry Lab", LV, -372, F, 113, -350, F + 7, 137, -361, F + 1, 116);
    }

    private void analysis() {
        int x1 = -397, x2 = -373, z1 = 114, z2 = 136;
        // glass-partitioned analysis bays
        for (int x = x1 + 6; x <= x2 - 1; x += 6) {
            k.partition(x, z1, x, 125, F, 4, B.QUARTZ, B.spane(B.LIGHT_BLUE));
            k.set(x, F + 1, 122, B.A);
            k.set(x, F + 2, 122, B.A);
            k.set(x, F + 1, 122, s.floorSlab);
        }
        for (int bay = 0; bay < 4; bay++) {
            int bx = x1 + 1 + bay * 6;
            // "microscope": dispenser pointing down on a counter with a lever
            k.set(bx + 1, F + 1, z1, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
            k.set(bx + 2, F + 1, z1, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
            k.set(bx + 1, F + 2, z1, B.dispenser(Dir.DOWN));
            k.set(bx + 2, F + 2, z1, B.of(B.DAYLIGHT_SENSOR));
            k.chair(bx + 1, F + 1, z1 + 1, B.BIRCH, Dir.NORTH);
            k.set(bx + 3, F + 1, z1, B.chest(Dir.SOUTH));
            if (bay == 1) {
                k.c.tile(new TileSpec.Inventory(bx + 3, F + 1, z1, Painter.spread(Loot.labAnalysis(), 27, bx, F, z1)));
            }
        }
        // centrifuges & sample fridge in the open south half
        for (int x = x1 + 2; x <= x2 - 2; x += 4) {
            k.set(x, F + 1, 131, B.of(B.CAULDRON));
            k.set(x + 1, F + 1, 131, B.IRON);
            k.set(x + 1, F + 2, 131, B.of(B.DAYLIGHT_SENSOR));
        }
        k.fill(x1, F + 1, 133, x1, F + 3, 135, B.BOOKS);
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.SEA);
        k.c.room("Analysis Lab", LV, -398, F, 113, -372, F + 7, 137, -386, F + 1, 134);
    }

    private void archive() {
        int x1 = -423, x2 = -399, z1 = 114, z2 = 136;
        // full-height west wall of books (the secret door hides in it)
        k.fill(-424, F + 1, 114, -424, F + 5, 136, B.BOOKS);
        // bookshelf aisles
        for (int x = x1 + 3; x <= x2 - 6; x += 3) {
            for (int z = z1 + 2; z <= z2 - 6; z++) {
                if (z == 124 || z == 125) {
                    continue;
                }
                k.fill(x, F + 1, z, x, F + 4, z, B.BOOKS);
            }
            k.set(x, F + 5, z1 + 5, B.SEA);
        }
        // records chests and reading tables near the entrance (south east)
        int n = 0;
        for (int x = x2 - 4; x <= x2; x++) {
            k.chest(x, F + 1, z1, Dir.SOUTH, Loot.archive(n++));
        }
        for (int x = x2 - 4; x <= x2 - 1; x += 3) {
            k.postTable(x, F + 1, 131, B.DARK_OAK_FENCE);
            k.chair(x, F + 1, 132, B.DARK_OAK, Dir.NORTH);
            k.chair(x, F + 1, 130, B.DARK_OAK, Dir.SOUTH);
        }
        k.set(x2, F + 1, 128, B.enderChest(Dir.WEST));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 6, 3, B.GLOW);
        k.sign(-411, F + 4, 136, Dir.NORTH, "§lARCHIVE", "Records &", "Documents", "§8silence");
        k.c.room("Archive & Records", LV, -424, F, 113, -398, F + 7, 137, -405, F + 1, 128);
    }

    /** Sticky-piston bookshelf door (wall x=-424, doorway z=124), lever hidden under a bookcase at z=127. */
    private void hiddenRecords() {
        int wx = -424, zd = 124;
        // doorway cells hold bookshelves (closed state); pistons at zd+2 pull them to zd+1 when unpowered
        k.set(wx, F + 1, zd, B.BOOKS);
        k.set(wx, F + 2, zd, B.BOOKS);
        k.set(wx, F + 3, zd, B.planks(B.DARK_OAK));
        for (int y = F + 1; y <= F + 2; y++) {
            k.set(wx, y, zd + 2, B.of(B.STICKY_PISTON, 2 | 8));  // facing north, extended
            k.set(wx, y, zd + 1, B.of(B.PISTON_HEAD, 2 | 8));    // sticky head facing north
        }
        k.set(wx, F + 1, zd + 3, B.planks(B.DARK_OAK));          // control block X
        k.set(wx, F + 2, zd + 3, B.of(B.REDSTONE_WIRE));          // dust on X powers the upper piston
        k.set(wx, F + 3, zd + 3, B.planks(B.DARK_OAK));
        k.set(wx, F + 3, zd + 1, B.planks(B.DARK_OAK));
        k.set(wx, F + 3, zd + 2, B.planks(B.DARK_OAK));
        // archive side: a protruding bookcase hides the pistons; the lever sits under it (ON = closed)
        for (int z = zd + 1; z <= zd + 3; z++) {
            for (int y = F + 1; y <= F + 4; y++) {
                k.set(wx + 1, y, z, B.BOOKS);
            }
        }
        k.set(wx + 1, F + 1, zd + 3, B.lever(Dir.EAST, true));
        // hidden side: bookcase + lever (OFF)
        for (int z = zd + 1; z <= zd + 3; z++) {
            for (int y = F + 1; y <= F + 3; y++) {
                k.set(wx - 1, y, z, B.BOOKS);
            }
        }
        k.set(wx - 1, F + 1, zd + 3, B.lever(Dir.WEST, false));
        k.set(wx + 1, F + 1, zd, s.floorSlab);
        k.set(wx + 1, F + 2, zd, B.A);
        k.set(wx - 1, F + 1, zd, B.woodSlab(B.DARK_OAK, false));
        k.set(wx - 1, F + 2, zd, B.A);
        k.c.marker("pistondoor", wx, F + 1, zd, "lever:" + (wx + 1) + "," + (F + 1) + "," + (zd + 3));
        // the room
        int x1 = -435, x2 = -425, z1 = 120, z2 = 130;
        k.chest(x1, F + 1, 125, Dir.EAST, Loot.hiddenRecords());
        k.fill(x1, F + 1, z1, x1, F + 4, z1 + 3, B.BOOKS);
        k.fill(x1, F + 1, z2 - 3, x1, F + 4, z2, B.BOOKS);
        for (int x = -432; x <= -429; x++) {
            k.set(x, F + 1, 125, B.woodStairs(B.DARK_OAK, Dir.EAST, true));
        }
        k.chair(-430, F + 1, 126, B.DARK_OAK, Dir.NORTH);
        k.set(-431, F + 2, 125, B.GLOW);
        k.set(-430, F + 2, 125, B.of(B.FLOWER_POT));
        k.rug(-433, 121, -427, 129, F, B.carpet(B.RED));
        k.lamp(-430, F + 6, 122, Dir.UP);
        k.lamp(-430, F + 6, 128, Dir.UP);
        k.c.secretRoom("SECRET: Hidden Records Room", LV, -436, F, 119, -424, F + 6, 131, -428, F + 1, 124);
    }

    private void specimens() {
        int x1 = -371, x2 = -351, z1 = 90, z2 = 108;
        // observation room (south part) looking through glass into the sealed specimen chamber (north part)
        k.fill(x1, F + 1, 98, x2, F + 5, 98, B.OBBY);
        k.fill(x1 + 2, F + 2, 98, x2 - 2, F + 4, 98, B.GLS);
        // sealed chamber: obsidian lined, specimen jars on pedestals
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= 97; z++) {
                if (x == x1 || x == x2 || z == z1) {
                    k.fill(x, F + 1, z, x, F + 6, z, B.OBBY);
                }
            }
        }
        int[] heads = {1, 2, 4, 0, 1};
        for (int i = 0; i < 5; i++) {
            int x = x1 + 3 + i * 4;
            k.set(x, F + 1, 93, B.QUARTZ_PILLAR);
            k.head(x, F + 2, 93, heads[i], 0);
            k.set(x - 1, F + 2, 93, B.PANE);
            k.set(x + 1, F + 2, 93, B.PANE);
            k.set(x, F + 2, 92, B.PANE);
            k.set(x, F + 2, 94, B.PANE);
            k.set(x, F + 3, 93, B.GLS);
        }
        k.set(-361, F + 1, 95, B.of(B.SOUL_SAND));
        k.set(-361, F + 2, 95, B.WEB_B);
        k.set(-361, F + 5, 95, B.SEA);
        k.set(-355, F + 5, 93, B.SEA);
        k.set(-367, F + 5, 93, B.SEA);
        // observation desks
        for (int x = x1 + 2; x <= x2 - 2; x++) {
            k.set(x, F + 1, 100, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
            if (Math.floorMod(x, 3) == 0) {
                k.chair(x, F + 1, 101, B.BIRCH, Dir.NORTH);
                k.set(x, F + 2, 100, B.of(B.DAYLIGHT_SENSOR));
            }
        }
        k.chest(x1, F + 1, z2, Dir.EAST, Loot.specimens());
        k.set(x1, F + 1, z2 - 1, B.of(B.BREWING_STAND));
        k.ceilingGrid(x1 + 1, 99, x2 - 1, z2 - 1, F + 7, 4, B.SEA);
        k.sign(-361, F + 3, 108, Dir.NORTH, "§4SPECIMEN", "§4RESEARCH", "Observation", "only");
        k.c.room("Specimen Research & Observation", LV, -372, F, 89, -350, F + 7, 109, -361, F + 1, 104);
    }

    private void cleanRoom() {
        int x1 = -397, x2 = -373, z1 = 90, z2 = 108;
        // airlock: inner glass wall with a second iron door
        k.partition(x1, 105, x2, 105, F, 5, B.QUARTZ, B.GLS);
        k.fill(-385, F + 1, 105, -385, F + 5, 105, B.QUARTZ);
        k.fill(-387, F + 1, 105, -387, F + 5, 105, B.QUARTZ);
        k.ironDoor(-386, F + 1, 105, Dir.NORTH, true);
        // clean benches with sea lantern task lights
        for (int row = 0; row < 3; row++) {
            int z = 93 + row * 4;
            for (int x = x1 + 3; x <= x2 - 3; x++) {
                k.set(x, F + 1, z, B.QUARTZ);
                k.set(x, F + 2, z, Math.floorMod(x, 5) == 0 ? B.of(B.BREWING_STAND)
                        : (Math.floorMod(x, 5) == 2 ? B.of(B.HOPPER, 0) : B.slab(B.SLAB_QUARTZ, false)));
            }
        }
        // hazmat suits on stands in the airlock
        for (int x = x1 + 2; x <= x2 - 2; x += 5) {
            EntitySpec e = k.armorStand(x, F + 1, 107, Dir.NORTH);
            e.helmet = Loot.leather(298, Loot.HAZMAT, "Hazmat Hood");
            e.chest = Loot.leather(299, Loot.HAZMAT, "Hazmat Suit");
            e.legs = Loot.leather(300, Loot.HAZMAT, "Hazmat Trousers");
            e.boots = Loot.leather(301, Loot.BLACK, "Hazmat Boots");
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 3, B.SEA);
        k.sign(-385, F + 3, 108, Dir.NORTH, "§bCLEAN ROOM", "Suit up in", "the airlock", "");
        k.c.room("Clean Room", LV, -398, F, 89, -372, F + 7, 109, -385, F + 1, 107);
    }

    private void sampleStorage() {
        int x1 = -423, x2 = -399, z1 = 90, z2 = 108;
        String[] labels = {"Samples A", "Samples B", "Reagents", "Glassware", "Specimens", "Cultures", "Waste",
                "Misc"};
        k.storageRow(x1 + 1, x2 - 1, z1, F + 1, Dir.SOUTH, B.QUARTZ, labels, 2);
        // lab offices: two glass cubicles
        k.partition(x1 + 1, 100, x1 + 8, 100, F, 4, B.QUARTZ, B.spane(B.WHITE));
        k.partition(x2 - 8, 100, x2 - 1, 100, F, 4, B.QUARTZ, B.spane(B.WHITE));
        k.set(x1 + 4, F + 1, 100, s.floorSlab);
        k.set(x1 + 4, F + 2, 100, B.A);
        k.set(x2 - 4, F + 1, 100, s.floorSlab);
        k.set(x2 - 4, F + 2, 100, B.A);
        for (int x : new int[]{x1 + 3, x2 - 3}) {
            k.set(x, F + 1, 104, B.woodStairs(B.BIRCH, Dir.NORTH, true));
            k.set(x + 1, F + 1, 104, B.woodStairs(B.BIRCH, Dir.NORTH, true));
            k.chair(x, F + 1, 105, B.BIRCH, Dir.NORTH);
            k.set(x + 1, F + 2, 104, B.of(B.FLOWER_POT));
        }
        k.chest(x1 + 2, F + 1, z2, Dir.NORTH, Loot.officer(2));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.SEA);
        k.c.room("Sample Storage & Lab Offices", LV, -424, F, 89, -398, F + 7, 109, -411, F + 1, 97);
    }

    private void infirmary() {
        int x1 = -365, x2 = -342, z1 = 146, z2 = 162;
        int n = 0;
        for (int x = x1 + 1; x <= x2 - 1; x += 3) {
            k.bed(x, F + 1, z1 + 1, Dir.NORTH);
            k.set(x + 1, F + 1, z1, B.chest(Dir.SOUTH));
            if (n++ % 2 == 0) {
                k.c.tile(new TileSpec.Inventory(x + 1, F + 1, z1, Painter.spread(Loot.medical(), 27, x, F, z1)));
            }
            k.set(x + 1, F + 2, z1, B.of(B.BREWING_STAND));
            k.set(x, F + 4, z1, B.SEA);
            // privacy curtains (white wool on fences) between beds
            k.set(x - 1, F + 3, z1 + 1, B.wool(B.WHITE));
            k.set(x - 1, F + 3, z1 + 2, B.wool(B.WHITE));
        }
        // nurse station
        for (int x = -358; x <= -350; x++) {
            k.set(x, F + 1, 157, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
        }
        k.set(-354, F + 2, 157, B.of(B.DAYLIGHT_SENSOR));
        k.chair(-354, F + 1, 158, B.BIRCH, Dir.NORTH);
        k.set(-350, F + 2, 157, B.of(B.FLOWER_POT));
        k.rug(x1 + 1, 152, x2 - 1, 154, F, B.carpet(B.RED));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.SEA);
        k.sign(-356, F + 3, 144, Dir.NORTH, "§c§lMEDICAL", "Infirmary", "Surgery", "Pharmacy");
        k.c.room("Infirmary Ward", LV, -366, F, 145, -341, F + 7, 163, -354, F + 1, 160);
    }

    private void surgery() {
        int x1 = -365, x2 = -355, z1 = 164, z2 = 180;
        // operating table with surgical lights
        for (int z = 170; z <= 172; z++) {
            k.set(-360, F + 1, z, B.QUARTZ_PILLAR);
            k.set(-360, F + 2, z, B.slab(B.SLAB_QUARTZ, false));
        }
        k.set(-361, F + 6, 171, B.SEA);
        k.set(-359, F + 6, 171, B.SEA);
        k.fill(-361, F + 5, 171, -359, F + 5, 171, B.BARS);
        k.set(-362, F + 1, 169, B.of(B.CAULDRON, 3));
        k.set(-358, F + 1, 173, B.of(B.BREWING_STAND));
        k.set(x1, F + 1, z2, B.chest(Dir.NORTH));
        k.c.tile(new TileSpec.Inventory(x1, F + 1, z2, Painter.spread(Loot.medical(), 27, 9, 9, 9)));
        k.set(x2, F + 1, z2, B.of(B.DISPENSER, 1));
        k.set(x2, F + 2, z2, B.of(B.DAYLIGHT_SENSOR));
        // scrub sinks
        k.set(x1, F + 1, z1 + 1, B.of(B.CAULDRON, 3));
        k.set(x1, F + 1, z1 + 2, B.of(B.CAULDRON, 3));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.SEA);
        k.c.room("Surgery & Treatment", LV, -366, F, 163, -354, F + 7, 181, -360, F + 1, 166);
    }

    private void pharmacy() {
        int x1 = -353, x2 = -342, z1 = 164, z2 = 171;
        String[] labels = {"Analgesics", "Antidotes"};
        k.storageRow(x1, x1 + 4, z2, F + 1, Dir.NORTH, B.QUARTZ, labels, 2);
        k.c.tile(new TileSpec.Inventory(x1, F + 1, z2, Painter.spread(Loot.pharmacy(), 27, 8, 8, 8)));
        k.set(x1, F + 1, z1, B.of(B.BREWING_STAND));
        k.set(x1 + 1, F + 1, z1, B.of(B.CAULDRON, 3));
        k.ceilingGrid(x1, z1, x2, z2 - 1, F + 7, 3, B.SEA);
        k.c.room("Pharmacy", LV, -354, F, 163, -341, F + 7, 172, -348, F + 1, 166);
    }

    private void medStorage() {
        int x1 = -353, x2 = -342, z1 = 173, z2 = 180;
        String[] labels = {"Linen", "Stretchers", "Oxygen", "Splints", "Masks"};
        k.storageRow(x1, x2, z2, F + 1, Dir.NORTH, B.QUARTZ, labels, 2);
        k.ceilingGrid(x1, z1, x2, z2 - 1, F + 7, 3, B.SEA);
        k.c.room("Medical Supplies", LV, -354, F, 172, -341, F + 7, 181, -347, F + 1, 175);
    }

    private void quarantine() {
        int x1 = -381, x2 = -367, z1 = 154, z2 = 180;
        // decon airlock at the east (entered from the infirmary at z=158): shower heads (dispensers) and hazard floor
        k.fill(x2 - 3, F + 1, z1, x2 - 3, F + 5, 162, B.clay(B.WHITE));
        k.fill(x2 - 3, F + 2, z1 + 1, x2 - 3, F + 3, 161, B.GLS);
        k.fill(x2 - 3, F + 1, 157, x2 - 3, F + 5, 157, B.clay(B.WHITE));
        k.fill(x2 - 3, F + 1, 159, x2 - 3, F + 5, 159, B.clay(B.WHITE));
        k.ironDoor(x2 - 3, F + 1, 158, Dir.WEST, true);
        for (int z = z1 + 1; z <= 162; z += 2) {
            k.set(x2, F + 4, z, B.dispenser(Dir.WEST));
            k.set(x2 - 1, F + 1, z, B.carpet(B.YELLOW));
        }
        k.sign(x2, F + 3, 158, Dir.WEST, "§eDECON", "§eAIRLOCK", "Wait for", "the green light");
        // isolation cells along the west side with glass fronts
        for (int i = 0; i < 4; i++) {
            int cz1 = z1 + i * 6 + 1, cz2 = cz1 + 4;
            k.fill(x1, F + 1, cz2 + 1, x1 + 6, F + 5, cz2 + 1, B.clay(B.WHITE));
            k.fill(x1 + 6, F + 1, cz1, x1 + 6, F + 5, cz2, B.clay(B.WHITE));
            k.fill(x1 + 6, F + 2, cz1, x1 + 6, F + 3, cz2 - 1, B.GLS);
            k.ironDoor(x1 + 6, F + 1, cz2, Dir.WEST, false);
            k.bed(x1 + 1, F + 1, cz1 + 1, Dir.WEST);
            k.set(x1 + 1, F + 1, cz2 - 1, B.of(B.CAULDRON, 3));
            k.set(x1 + 3, F + 5, cz1 + 2, B.SEA);
            k.sign(x1 + 7, F + 4, cz1 + 1, Dir.EAST, "ISOLATION " + (i + 1), i == 2 ? "§4OCCUPIED" : "vacant", "", "");
        }
        k.ceilingGrid(x1 + 8, z1 + 1, x2 - 4, z2 - 1, F + 7, 4, B.SEA);
        k.ceilingGrid(x2 - 2, z1 + 1, x2, 162, F + 7, 3, B.SEA);
        k.c.room("Quarantine & Decontamination", LV, -382, F, 153, -366, F + 7, 181, -372, F + 1, 170);
    }
}
