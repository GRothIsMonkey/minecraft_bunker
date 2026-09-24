package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/**
 * LEVEL 6 - Containment & Restricted (floor y=5): Checkpoint Delta, cell block, interrogation, evidence, the
 * Theta containment chamber, Project OMEGA lab, the Warden's office with the hidden vault, and - hidden behind
 * the Warden's bookcase - the Blacksite Core and the Director's private rail station.
 */
public final class Level6 {
    private final Kit k;
    private final Style s = Style.containment();
    private static final int F = L6;
    private static final String LV = "Level 6";

    // the Core
    static final int CO_X1 = -420, CO_X2 = -376, CO_Z1 = 97, CO_Z2 = 127;
    static final int CO_CX = -398, CO_CZ = 112;

    public Level6(Kit k) {
        this.k = k;
    }

    public void build() {
        Style core = coreStyle();
        k.shell(-424, 137, -330, 145, F, 5, s, 6);           // spine
        k.shell(-384, 127, -372, 137, F, 5, s, 0);           // checkpoint delta guard room
        k.shell(-410, 127, -384, 137, F, 5, wardenStyle(), 6); // warden's office
        k.shell(-424, 127, -410, 137, F, 5, vaultStyle(), 0);  // secret vault
        k.shell(CO_X1, CO_Z1, CO_X2, CO_Z2, F, 8, core, 6);  // the Core (secret)
        k.shell(-424, 79, -398, 95, F, 5, s, 6);             // director's rail station (secret)
        k.shell(-372, 105, -345, 137, F, 5, labStyle(), 6);  // project OMEGA restricted lab
        k.shell(-366, 145, -326, 181, F, 5, s, 6);           // cell block
        k.shell(-326, 145, -316, 163, F, 5, s, 0);           // interrogation
        k.shell(-316, 145, -306, 163, F, 5, s, 0);           // observation
        k.shell(-326, 163, -306, 181, F, 5, s, 6);           // evidence
        k.shell(-424, 145, -386, 149, F, 5, s, 6);           // observation corridor
        k.shell(-424, 149, -386, 185, F, 6, thetaStyle(), 6); // containment chamber theta
        openings();
        spine();
        delta();
        warden();
        vault();
        coreChamber();
        railStation();
        omegaLab();
        cellBlock();
        interrogation();
        evidence();
        theta();
    }

    static Style wardenStyle() {
        Style w = Style.containment();
        w.wall = B.planks(B.DARK_OAK);
        w.wallLow = B.planks(B.DARK_OAK);
        w.wallHigh = B.planks(B.DARK_OAK);
        w.pillar = B.log(B.DARK_OAK, 0);
        w.floorSlab = B.woodSlab(B.DARK_OAK, false);
        return w;
    }

    static Style vaultStyle() {
        Style v = Style.containment();
        v.wall = B.IRON;
        v.wallLow = B.IRON;
        v.wallHigh = B.IRON;
        v.pillar = B.IRON;
        v.floorSlab = B.slab(B.SLAB_QUARTZ, false);
        v.ceiling = B.IRON;
        return v;
    }

    static Style coreStyle() {
        Style c = Style.containment();
        c.wall = B.OBBY;
        c.wallLow = B.clay(B.BLACK);
        c.wallHigh = B.OBBY;
        c.pillar = B.of(B.END_STONE);
        c.floor = B.OBBY;
        c.floorSlab = B.slab(B.SLAB_NETHER, false);
        c.ceiling = B.OBBY;
        return c;
    }

    static Style labStyle() {
        Style c = Style.science();
        c.wallLow = B.clay(B.PURPLE);
        c.floorSlab = B.slab(B.SLAB_QUARTZ, false);
        return c;
    }

    static Style thetaStyle() {
        Style c = Style.containment();
        c.wall = B.OBBY;
        c.wallLow = B.OBBY;
        c.wallHigh = B.OBBY;
        c.pillar = B.IRON;
        c.floor = B.OBBY;
        return c;
    }

    private void openings() {
        // checkpoint delta west gate across the spine at x=-385
        for (int z = 138; z <= 144; z++) {
            k.fill(-385, F + 1, z, -385, F + 5, z, z == 141 ? B.A : (z == 140 || z == 142 ? B.IRON : B.BARS));
        }
        k.fill(-385, F + 3, 138, -385, F + 5, 144, B.IRON);
        k.door(-385, F + 1, 141, B.IRON_DOOR, Dir.WEST, false);
        k.set(-384, F + 2, 142, B.button(Dir.EAST, false));
        k.set(-386, F + 2, 142, B.button(Dir.WEST, false));
        k.c.marker("irondoor", -385, F + 1, 141, "WEST");
        k.opening(-378, 137, -377, 137, F, 3, s);           // spine -> delta guard room
        k.ironDoubleDoor(-397, F + 1, 137, Dir.NORTH);       // spine -> warden
        k.ironDoubleDoor(-359, F + 1, 137, Dir.NORTH);       // spine -> omega lab
        k.ironDoubleDoor(-347, F + 1, 145, Dir.NORTH);       // spine -> cell block
        k.opening(-405, 145, -402, 145, F, 3, s);           // spine -> observation corridor
        k.ironDoor(-326, F + 1, 150, Dir.EAST, true);       // cell block -> interrogation
        k.ironDoor(-326, F + 1, 172, Dir.EAST, true);       // cell block -> evidence
        k.door(-311, F + 1, 163, B.DARK_OAK_DOOR, Dir.NORTH, false); // evidence -> observation
        k.ironDoor(-405, F + 1, 149, Dir.SOUTH, true);      // corridor -> theta chamber
        // core -> director's rail station (north), plain iron door; the tunnel crosses z 95..97
        for (int z = 95; z <= 97; z++) {
            k.set(-410, F + 1, z, B.carpet(B.PURPLE));
            k.set(-410, F + 2, z, B.A);
            k.set(-411, F + 1, z, B.OBBY);
            k.set(-411, F + 2, z, B.OBBY);
            k.set(-409, F + 1, z, B.OBBY);
            k.set(-409, F + 2, z, B.OBBY);
            k.set(-410, F + 3, z, B.OBBY);
        }
        k.ironDoor(-410, F + 1, 97, Dir.NORTH, true);
    }

    private void spine() {
        for (int x = -423; x <= -331; x++) {
            if (x == -385) {
                continue;
            }
            k.set(x, F + 1, 141, B.carpet(B.PURPLE));
            if (Math.floorMod(x, 7) == 0) {
                k.lamp(x, F + 6, 141, Dir.UP);
                k.set(x, F + 5, 138, B.OBBY);
                k.set(x, F + 5, 144, B.OBBY);
            }
        }
        k.sign(-386, F + 4, 140, Dir.WEST, "§5RESTRICTED", "§5ZONE", "Level 6-W", "");
        k.sign(-423, F + 3, 141, Dir.EAST, "§lLEVEL 6", "§5Containment", "", "§8no exit");
        k.c.room("Level 6 Main Corridor", LV, -424, F, 137, -330, F + 6, 145, -370, F + 1, 141);
    }

    private void delta() {
        int x1 = -383, x2 = -373, z1 = 128, z2 = 136;
        // observation windows onto the spine
        for (int x = x1; x <= x2; x++) {
            if (x != -378 && x != -377) {
                k.set(x, F + 2, 137, B.BARS);
                k.set(x, F + 3, 137, B.BARS);
            }
        }
        for (int x = x1 + 1; x <= x2 - 1; x++) {
            if (x == -378 || x == -377) {
                continue;
            }
            k.set(x, F + 1, 135, B.stairs(B.STONE_BRICK_STAIRS, Dir.SOUTH, true));
        }
        k.set(-381, F + 2, 135, B.of(B.DAYLIGHT_SENSOR));
        k.set(-374, F + 2, 135, B.comparator(Dir.SOUTH));
        k.chair(-380, F + 1, 134, B.DARK_OAK, Dir.SOUTH);
        k.chair(-375, F + 1, 134, B.DARK_OAK, Dir.SOUTH);
        k.chest(x1, F + 1, z1, Dir.SOUTH, Loot.guardRoom());
        k.set(x1 + 1, F + 1, z1, B.dispenser(Dir.SOUTH));
        k.set(x2, F + 1, z1, B.WORKBENCH_B);
        k.lamp(-380, F + 6, 131, Dir.UP);
        k.lamp(-375, F + 6, 131, Dir.UP);
        k.sign(-376, F + 3, 138, Dir.SOUTH, "§lCHECKPOINT", "§lDELTA", "All visitors", "escorted");
        k.c.room("Checkpoint Delta", LV, -384, F, 127, -372, F + 6, 137, -377, F + 1, 131);
    }

    private void warden() {
        int x1 = -409, x2 = -385, z1 = 128, z2 = 136;
        // bookcase row along the north wall: three high, except the column that hides the Core passage
        int px = -397;
        for (int x = x1; x <= x2; x++) {
            for (int y = F + 1; y <= F + 3; y++) {
                if (x == px && y < F + 3) {
                    continue;
                }
                k.set(x, y, z1, B.BOOKS);
            }
        }
        // passage cells in the north wall (z=127) hold wall signs; the painting (GRAHAM) hangs in front of them
        for (int y = F + 1; y <= F + 2; y++) {
            k.set(px, y, 127, B.wallSign(Dir.EAST));
            k.c.tile(new TileSpec.SignText(px, y, 127, y == F + 1
                    ? new String[]{"§8...", "One shelf", "too short.", ""} : new String[]{"", "", "", ""}));
            k.set(px - 1, y, 127, B.OBBY);
            k.set(px + 1, y, 127, B.OBBY);
        }
        k.set(px, F + 3, 127, B.OBBY);
        k.set(px, F, 127, B.GLOW);
        k.set(px, F + 1, z1, B.A);
        k.set(px, F + 2, z1, B.A);
        k.set(px, F, z1, B.GLOW);
        k.painting(px, F + 1, z1, Dir.SOUTH, "GRAHAM");
        k.c.marker("secret-passage", px, F + 1, 127, "GRAHAM");
        // desk and chair
        for (int x = -400; x <= -394; x++) {
            k.set(x, F + 1, 133, B.woodStairs(B.DARK_OAK, Dir.SOUTH, true));
        }
        k.chair(-397, F + 1, 131, B.DARK_OAK, Dir.SOUTH);
        k.set(-399, F + 2, 133, B.of(B.FLOWER_POT));
        k.set(-395, F + 2, 133, B.GLOW);
        k.chest(x1, F + 1, z2, Dir.EAST, Loot.escapeKit());
        k.rug(-404, 130, -390, 135, F, B.carpet(B.RED));
        // the Warden's bust (player head on a quartz pedestal); the vault button is on the wall right behind it
        k.set(x1, F + 1, 134, B.QUARTZ_PILLAR);
        k.head(x1, F + 2, 134, 3, 12);
        k.set(x1, F + 2, 133, B.button(Dir.EAST, false));
        k.c.marker("vault-button", x1, F + 2, 133, "behind the bust");
        k.wallBanner(-392, F + 4, z2, Dir.NORTH, B.PURPLE, "bo", "black", "flo", "black");
        k.lamp(-403, F + 6, 132, Dir.UP);
        k.lamp(-391, F + 6, 132, Dir.UP);
        k.lamp(-397, F + 6, 135, Dir.UP);
        k.sign(-398, F + 4, 138, Dir.SOUTH, "§5WARDEN", "M. Voss", "", "");
        k.c.room("Warden's Office", LV, -410, F, 127, -384, F + 6, 137, -392, F + 1, 131);
    }

    private void vault() {
        int x1 = -423, x2 = -411, z1 = 128, z2 = 136;
        // vault door in the shared wall x=-410 (iron door, no handle on the office side)
        k.set(-410, F + 1, 131, B.planks(B.DARK_OAK));
        k.set(-410, F + 2, 131, B.planks(B.DARK_OAK));
        k.set(-410, F + 1, 133, B.planks(B.DARK_OAK));
        k.set(-410, F + 2, 133, B.planks(B.DARK_OAK));
        k.door(-410, F + 1, 132, B.IRON_DOOR, Dir.WEST, false);
        k.set(-411, F + 2, 133, B.button(Dir.WEST, false));
        k.c.marker("irondoor", -410, F + 1, 132, "WEST");
        // bullion, chests
        for (int x = x1 + 1; x <= x1 + 3; x++) {
            k.set(x, F + 1, z1, B.GOLD);
            k.set(x, F + 1, z2, B.GOLD);
        }
        k.set(x1 + 2, F + 2, z1, B.GOLD);
        // one double chest and a single: three chests in a line would merge into a broken 1.8 "triple chest"
        k.chest(x1, F + 1, 131, Dir.EAST, Loot.vault());
        k.chest(x1, F + 1, 132, Dir.EAST);
        k.set(x1, F + 1, 133, B.GOLD);
        k.chest(x1, F + 1, 134, Dir.EAST);
        k.set(x2, F + 1, z1, B.enderChest(Dir.WEST));
        k.itemFrame(x1, F + 3, 132, Dir.EAST, ItemSpec.of(264, 1));
        k.lamp(-417, F + 6, 132, Dir.UP);
        k.lamp(-413, F + 6, 132, Dir.UP);
        k.c.secretRoom("SECRET: Vault", LV, -424, F, 127, -410, F + 6, 137, -416, F + 1, 132);
    }

    private void coreChamber() {
        int x1 = CO_X1 + 1, x2 = CO_X2 - 1, z1 = CO_Z1 + 1, z2 = CO_Z2 - 1, top = F + 9;
        // floor pattern: purple glass ring over sea lanterns around the dais
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                double d = Math.sqrt((x - CO_CX) * (x - CO_CX) + (z - CO_CZ) * (z - CO_CZ));
                if (d > 7.5 && d < 8.6) {
                    k.set(x, F, z, B.SEA);
                    k.set(x, F + 1, z, B.carpet(B.PURPLE));
                } else if (Math.floorMod(x - CO_CX, 8) == 0 || Math.floorMod(z - CO_CZ, 8) == 0) {
                    k.set(x, F + 1, z, B.slab(B.SLAB_QUARTZ, false));
                }
            }
        }
        // the dais
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                int ad = Math.max(Math.abs(dx), Math.abs(dz));
                int x = CO_CX + dx, z = CO_CZ + dz;
                if (ad == 3) {
                    Dir out = Math.abs(dx) == 3 ? (dx > 0 ? Dir.EAST : Dir.WEST) : (dz > 0 ? Dir.SOUTH : Dir.NORTH);
                    k.set(x, F + 1, z, B.stairs(B.NETHER_BRICK_STAIRS, out.opposite(), false));
                } else {
                    k.set(x, F + 1, z, B.of(B.END_STONE));
                }
            }
        }
        // ring of end portal frames (eyes in) - deliberately not a valid portal shape
        int[][] ring = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}, {-2, -2}, {2, 2}, {-2, 2}, {2, -2}};
        for (int[] r : ring) {
            Dir facing = Math.abs(r[0]) >= Math.abs(r[1]) ? (r[0] > 0 ? Dir.WEST : Dir.EAST) : (r[1] > 0 ? Dir.NORTH : Dir.SOUTH);
            k.set(CO_CX + r[0], F + 2, CO_CZ + r[1], B.endFrame(facing, true));
        }
        // the artefact: dragon egg on an end stone plinth inside a glass case
        k.set(CO_CX, F + 2, CO_CZ, B.of(B.END_STONE));
        k.set(CO_CX, F + 3, CO_CZ, B.of(B.DRAGON_EGG));
        k.set(CO_CX + 1, F + 3, CO_CZ, B.GLS);
        k.set(CO_CX - 1, F + 3, CO_CZ, B.GLS);
        k.set(CO_CX, F + 3, CO_CZ + 1, B.GLS);
        k.set(CO_CX, F + 3, CO_CZ - 1, B.GLS);
        k.set(CO_CX, F + 4, CO_CZ, B.sglass(B.PURPLE));
        k.c.marker("artefact", CO_CX, F + 3, CO_CZ, "dragon egg");
        // light shaft over the dais
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                k.set(CO_CX + dx, top, CO_CZ + dz, (dx == 0 && dz == 0) ? B.SEA : B.sglass(B.PURPLE));
                k.set(CO_CX + dx, top + 1, CO_CZ + dz, B.SEA);
            }
        }
        // obsidian pillars with purple glass lights
        for (int a = 0; a < 8; a++) {
            double ang = a * Math.PI / 4;
            int x = (int) Math.round(CO_CX + 12 * Math.cos(ang)), z = (int) Math.round(CO_CZ + 11 * Math.sin(ang));
            for (int y = F + 1; y < top; y++) {
                k.set(x, y, z, y == F + 4 ? B.SEA : B.OBBY);
            }
        }
        // research stations and specimen cages along the walls
        for (int x = x1 + 3; x <= x2 - 3; x += 6) {
            k.fill(x - 1, F + 1, z1 + 1, x + 1, F + 3, z1 + 1, B.BARS);
            k.head(x, F + 1, z1, (x % 3 == 0) ? 1 : 0, 0);
            k.set(x, F + 1, z2, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
            k.set(x, F + 2, z2, B.of(B.DAYLIGHT_SENSOR));
        }
        k.chest(x1, F + 1, CO_CZ, Dir.EAST, Loot.core());
        k.set(x1, F + 1, CO_CZ + 1, B.enderChest(Dir.EAST));
        // lamps in the ceiling and wall
        k.ceilingGrid(x1 + 2, z1 + 2, x2 - 2, z2 - 2, top, 6, B.LAMP);
        for (int z = z1 + 2; z <= z2 - 2; z += 6) {
            k.set(CO_X1, F + 5, z, B.GLOW);
            k.set(CO_X2, F + 5, z, B.GLOW);
        }
        k.sign(-397, F + 3, z2, Dir.NORTH, "§5§lTHE CORE", "§5S-04 OMEGA", "§4Do not touch", "§4the artefact");
        k.c.secretRoom("SECRET: The Blacksite Core", LV, CO_X1, F, CO_Z1, CO_X2, top, CO_Z2, -397, F + 1, 124);
    }

    private void railStation() {
        int x1 = -423, x2 = -399, z1 = 80, z2 = 94;
        // platform along the track (rails at z=88 laid by Cores)
        for (int x = -404; x <= x2; x++) {
            k.set(x, F + 1, 87, B.slab(B.SLAB_STONEBRICK, false));
            k.set(x, F + 1, 89, B.clay(B.YELLOW));
            k.set(x, F + 1, 86, B.slab(B.SLAB_STONEBRICK, false));
        }
        k.minecart(-404, F + 1, 88);  // resting on the launcher (Cores.escapeRail)
        k.c.marker("rail-station", -404, F + 1, 88, "button:-404," + (F + 2) + ",88");
        // command desk, map wall, bunks
        k.fill(x1 - 1, F + 2, 83, x1 - 1, F + 4, 91, B.clay(B.BLACK));
        k.fill(x1 - 1, F + 3, 84, x1 - 1, F + 3, 90, B.wool(B.WHITE));
        for (int z = 84; z <= 90; z++) {
            k.set(x1 + 2, F + 1, z, B.woodStairs(B.DARK_OAK, Dir.WEST, true));
        }
        k.chair(x1 + 3, F + 1, 87, B.DARK_OAK, Dir.WEST);
        k.chest(x1, F + 1, z1, Dir.SOUTH, Loot.warRoom());
        k.chest(x1 + 1, F + 1, z1, Dir.SOUTH, Loot.escapeKit());
        k.bed(x1 + 5, F + 1, z2, Dir.SOUTH);
        k.bed(x1 + 7, F + 1, z2, Dir.SOUTH);
        k.set(x1 + 9, F + 1, z2, B.WORKBENCH_B);
        k.set(x1 + 10, F + 1, z2, B.furnace(Dir.NORTH));
        k.set(x1 + 11, F + 1, z2, B.enderChest(Dir.NORTH));
        k.set(-405, F + 2, 88, B.IRON);
        k.sign(-406, F + 2, 88, Dir.WEST, "§4EGRESS RAIL", "Sit in the cart", "press button", "east >>>");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 6, 4, B.LAMP);
        k.c.secretRoom("SECRET: Director's Rail Station", LV, -424, F, 79, -398, F + 6, 95, -410, F + 1, 93);
    }

    private void omegaLab() {
        int x1 = -371, x2 = -346, z1 = 106, z2 = 136;
        // containment tank (glass cylinder) with a "sample" inside
        for (int y = F + 1; y <= F + 4; y++) {
            k.ringXZ(-358, y, 118, 2.3, 3.2, y == F + 1 || y == F + 4 ? B.IRON : B.sglass(B.PURPLE));
        }
        k.diskXZ(-358, F + 1, 118, 2.2, B.SEA);
        k.set(-358, F + 2, 118, B.of(B.SOUL_SAND));
        k.head(-358, F + 3, 118, 1, 8);
        // lab benches and specimen notes
        for (int x = x1 + 2; x <= x2 - 2; x++) {
            if (x > -362 && x < -354) {
                continue;
            }
            k.set(x, F + 1, 110, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
            k.set(x, F + 1, 130, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
            if (Math.floorMod(x, 3) == 0) {
                k.set(x, F + 2, 110, B.of(B.BREWING_STAND));
                k.set(x, F + 2, 130, B.of(B.DAYLIGHT_SENSOR));
            }
        }
        k.chest(x1, F + 1, 120, Dir.EAST, Loot.specimens());
        k.chest(x1, F + 1, 121, Dir.EAST, Loot.labChem());
        // reactor sump access (door carved by Reactor through the east wall x=-345)
        k.sign(x2, F + 3, 117, Dir.WEST, "§eREACTOR SUMP", "§eACCESS", "§4Radiation", "");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 6, 4, B.SEA);
        k.sign(-358, F + 3, 138, Dir.SOUTH, "§5PROJECT OMEGA", "§4Restricted", "§4Lab", "");
        k.c.room("Project OMEGA Restricted Lab", LV, -372, F, 105, -345, F + 6, 137, -360, F + 1, 134);
    }

    private void cellBlock() {
        int x1 = -365, x2 = -327, z1 = 146, z2 = 180;
        // cells: six north (z 146..160, front at 160) and six south (z 166..180, front at 166)
        for (int i = 0; i < 6; i++) {
            int xs = -366 + i * 6, xe = xs + 6;
            cell(xs, xe, 146, 160, 160, Dir.SOUTH, i + 1);
            cell(xs, xe, 166, 180, 166, Dir.NORTH, i + 7);
        }
        // walkway light and guard observation booth at the east end
        for (int x = x1; x <= x2; x += 4) {
            k.lamp(x, F + 6, 163, Dir.UP);
        }
        k.fill(-329, F + 1, 161, -329, F + 4, 165, B.SB);
        k.fill(-329, F + 2, 161, -329, F + 3, 165, B.PANE);
        k.set(-329, F + 2, 162, B.SB);
        k.set(-329, F + 3, 162, B.SB);
        k.set(-329, F + 1, 163, B.doorLower(B.IRON_DOOR, Dir.EAST, false));
        k.set(-329, F + 2, 163, B.doorUpper(B.IRON_DOOR, false));
        k.set(-330, F + 2, 162, B.button(Dir.WEST, false));
        k.set(-328, F + 2, 162, B.button(Dir.EAST, false));
        k.c.marker("irondoor", -329, F + 1, 163, "EAST");
        k.set(-327, F + 1, 161, B.chest(Dir.WEST));
        k.set(-327, F + 1, 165, B.stairs(B.STONE_BRICK_STAIRS, Dir.EAST, false));
        k.sign(-349, F + 3, 146, Dir.SOUTH, "§lCELL BLOCK C", "12 cells", "", "");
        k.c.room("Cell Block C", LV, -366, F, 145, -326, F + 6, 181, -345, F + 1, 163);
    }

    private void cell(int xs, int xe, int za, int zb, int front, Dir facingOut, int n) {
        // partition walls
        for (int z = Math.min(za, zb); z <= Math.max(za, zb); z++) {
            k.fill(xs, F + 1, z, xs, F + 5, z, B.SB_CRACKED);
            k.fill(xe, F + 1, z, xe, F + 5, z, B.SB_CRACKED);
        }
        // front: bars with a door next to the west partition
        for (int x = xs + 1; x < xe; x++) {
            k.fill(x, F + 1, front, x, F + 5, front, B.BARS);
            k.set(x, F + 5, front, B.SB);
        }
        int dx = xs + 1;
        k.set(dx, F + 1, front, B.doorLower(B.IRON_DOOR, facingOut, false));
        k.set(dx, F + 2, front, B.doorUpper(B.IRON_DOOR, false));
        k.set(dx, F + 3, front, B.SB);
        // guard lever outside above the door, and a button inside (nobody gets soft-locked in a cell)
        k.set(dx, F + 3, front + facingOut.dz, B.lever(facingOut, false));
        k.set(dx, F + 3, front - facingOut.dz, B.button(facingOut.opposite(), false));
        k.c.marker("irondoor", dx, F + 1, front, facingOut.name());
        // interior
        int back = front == Math.min(za, zb) ? Math.max(za, zb) : Math.min(za, zb);
        int in = -facingOut.dz;
        k.bed(xe - 1, F + 1, front + in * 2, in > 0 ? Dir.SOUTH : Dir.NORTH);
        k.set(xs + 2, F + 1, back, B.of(B.CAULDRON, 1));
        k.set(xs + 3, F + 5, (front + back) / 2, B.LAMP);
        k.set(xs + 3, F + 6, (front + back) / 2, B.RS_BLOCK);
        if (n == 3 || n == 10) {
            EntitySpec e = k.armorStand(xs + 3, F + 1, back + (in > 0 ? -2 : 2), facingOut);
            e.chest = Loot.leather(299, 0xD87F33, "Prisoner Jumpsuit");
            e.legs = Loot.leather(300, 0xD87F33, "Prisoner Trousers");
            e.boots = Loot.leather(301, 0x3A3A3A, "Slippers");
        }
        k.sign(xs + 3, F + 4, front + facingOut.dz, facingOut, "CELL " + n, n == 3 || n == 10 ? "§4OCCUPIED" : "vacant", "", "");
    }

    private void interrogation() {
        // interrogation room x -325..-317
        k.set(-321, F + 1, 154, B.of(B.NETHER_FENCE));
        k.set(-321, F + 2, 154, B.slab(B.SLAB_NETHER, true));
        k.set(-321, F + 3, 154, B.carpet(B.BLACK));
        k.stoneChair(-321, F + 1, 152, B.NETHER_BRICK_STAIRS, Dir.SOUTH);
        k.stoneChair(-321, F + 1, 156, B.NETHER_BRICK_STAIRS, Dir.NORTH);
        // interrogation light: lever-controlled lamp over the table
        k.set(-321, F + 5, 154, B.of(B.LAMP_ON));
        k.set(-321, F + 5, 155, B.lever(Dir.SOUTH, true));
        k.c.marker("lighttoggle", -321, F + 5, 155, "interrogation lamp");
        // one-way mirror into the observation room
        k.fill(-316, F + 2, 149, -316, F + 4, 159, B.sglass(B.GRAY));
        // observation room
        for (int z = 148; z <= 160; z += 3) {
            k.chair(-314, F + 1, z, B.DARK_OAK, Dir.WEST);
        }
        k.set(-308, F + 1, 148, B.chest(Dir.WEST));
        k.set(-308, F + 1, 150, B.of(B.JUKEBOX));
        k.lamp(-311, F + 6, 150, Dir.UP);
        k.lamp(-311, F + 6, 157, Dir.UP);
        k.lamp(-321, F + 6, 148, Dir.UP);
        k.lamp(-321, F + 6, 160, Dir.UP);
        k.c.room("Interrogation Room", LV, -326, F, 145, -316, F + 6, 163, -323, F + 1, 150);
        k.c.room("Observation Room", LV, -316, F, 145, -306, F + 6, 163, -311, F + 1, 160);
    }

    private void evidence() {
        int x1 = -325, x2 = -307, z1 = 164, z2 = 180;
        String[] labels = {"Case 0419", "Case 0420", "Case 0427", "Case 0501", "Case 0512", "Case 0513"};
        k.storageRow(x1 + 1, x2 - 1, z2, F + 1, Dir.NORTH, B.IRON, labels, 2);
        for (int i = 0; i < 6; i++) {
            k.c.tile(new TileSpec.Inventory(x1 + 1 + i * 3, F + 1, z2,
                    Painter.spread(Loot.evidence(i), 27, i, 3, 3)));
        }
        // confiscation cage
        k.fill(x1 + 2, F + 1, 169, x1 + 8, F + 3, 169, B.BARS);
        k.fill(x1 + 8, F + 1, 165, x1 + 8, F + 3, 168, B.BARS);
        k.set(x1 + 8, F + 1, 167, B.fenceGate(B.DARK_OAK_FENCE_GATE, Dir.EAST, false));
        k.set(x1 + 8, F + 2, 167, B.BARS);
        EntitySpec e = k.armorStand(x1 + 4, F + 1, 166, Dir.SOUTH);
        e.helmet = ItemSpec.of(314, 1);
        e.chest = Loot.leather(299, 0x2E6B2E, "Explorer's Coat");
        e.hand = ItemSpec.of(346, 1);
        k.chest(x1 + 2, F + 1, 165, Dir.SOUTH, Loot.evidence(4));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 6, 4, B.LAMP);
        k.sign(-316, F + 3, 164, Dir.SOUTH, "EVIDENCE", "& Confiscation", "§4Sign out", "§4required");
        k.c.room("Evidence & Confiscation Store", LV, -326, F, 163, -306, F + 6, 181, -316, F + 1, 172);
    }

    private void theta() {
        int x1 = -423, x2 = -387, z1 = 150, z2 = 184;
        // observation corridor glass wall (z=149)
        for (int x = -423; x <= -387; x++) {
            if (x >= -406 && x <= -404) {
                continue;
            }
            k.set(x, F + 2, 149, B.GLS);
            k.set(x, F + 3, 149, B.GLS);
        }
        for (int x = -422; x <= -388; x += 6) {
            k.lamp(x, F + 6, 147, Dir.UP);
        }
        k.c.room("Theta Observation Corridor", LV, -424, F, 145, -386, F + 6, 149, -410, F + 1, 147);
        // central cage 9x9 of iron bars with the specimen statue
        int cx = -405, cz = 168;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                boolean edge = Math.abs(dx) == 4 || Math.abs(dz) == 4;
                if (edge) {
                    k.fill(cx + dx, F + 1, cz + dz, cx + dx, F + 5, cz + dz, (Math.abs(dx) == 4 && Math.abs(dz) == 4) ? B.IRON : B.BARS);
                }
                k.set(cx + dx, F, cz + dz, B.of(B.END_STONE));
            }
        }
        k.set(cx, F + 1, cz, B.of(B.SOUL_SAND));
        k.set(cx, F + 2, cz, B.of(B.SOUL_SAND));
        k.head(cx, F + 3, cz, 1, 0);
        k.head(cx + 2, F + 1, cz - 1, 1, 4);
        k.head(cx - 2, F + 1, cz + 2, 1, 10);
        for (int dx = -3; dx <= 3; dx += 3) {
            for (int dz = -3; dz <= 3; dz += 3) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                k.fill(cx + dx, F + 3, cz + dz, cx + dx, F + 6, cz + dz, B.BARS);
            }
        }
        k.set(cx, F + 6, cz, B.sglass(B.PURPLE));
        k.set(cx, F + 7, cz, B.SEA);
        // dim warning lights
        for (int x = x1 + 3; x <= x2 - 3; x += 8) {
            for (int z = z1 + 3; z <= z2 - 3; z += 8) {
                if (Math.abs(x - cx) <= 5 && Math.abs(z - cz) <= 5) {
                    continue;
                }
                k.lamp(x, F + 7, z, Dir.UP);
            }
        }
        k.sign(-405, F + 4, 150, Dir.SOUTH, "§4§lTHETA", "§4Containment", "Do not feed", "the specimen");
        k.c.room("Containment Chamber Theta", LV, -424, F, 149, -386, F + 7, 185, -405, F + 1, 152);
    }
}
