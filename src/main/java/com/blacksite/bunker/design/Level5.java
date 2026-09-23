package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/** LEVEL 5 - Reactor & Power (floor y=15). The reactor chamber itself is built by {@link Reactor}. */
public final class Level5 {
    private final Kit k;
    private final Style s = Style.reactor();
    private static final int F = L5;
    private static final String LV = "Level 5";

    public Level5(Kit k) {
        this.k = k;
    }

    public void build() {
        k.shell(-424, 137, -298, 145, F, 5, s, 6);           // spine
        k.shell(-372, 105, -345, 137, F, 6, s, 6);           // reactor control room
        k.shell(-424, 95, -372, 137, F, 6, turbineStyle(), 6); // turbine hall
        k.shell(-366, 145, -336, 165, F, 6, s, 6);           // generator A
        k.shell(-336, 145, -298, 165, F, 6, s, 6);           // generator B
        k.shell(-366, 165, -298, 185, F, 6, s, 6);           // power routing hall
        k.shell(-424, 145, -382, 165, F, 6, scramStyle(), 6); // emergency shutdown
        k.shell(-424, 165, -382, 185, F, 6, s, 6);           // battery bank
        k.shell(-382, 153, -366, 185, F, 6, s, 5);           // transformer room
        k.shell(-314, 97, -298, 137, F, 6, s, 5);            // switchyard
        openings();
        spine();
        control();
        turbines();
        generator(-365, -337, "Backup Generator A");
        generator(-335, -299, "Backup Generator B");
        routing();
        scram();
        batteries();
        transformers();
        switchyard();
    }

    static Style turbineStyle() {
        Style t = Style.reactor();
        t.wallLow = B.IRON;
        t.pillar = B.IRON;
        return t;
    }

    static Style scramStyle() {
        Style t = Style.reactor();
        t.wallLow = B.clay(B.RED);
        t.wallHigh = B.clay(B.RED);
        return t;
    }

    private void openings() {
        k.ironDoubleDoor(-359, F + 1, 137, Dir.NORTH);   // spine -> control room
        k.opening(-400, 137, -396, 137, F, 4, s);        // spine -> turbine hall
        k.opening(-372, 128, -372, 130, F, 3, s);        // control <-> turbine hall
        k.opening(-352, 145, -350, 145, F, 3, s);        // spine -> generator A
        k.opening(-318, 145, -316, 145, F, 3, s);        // spine -> generator B
        k.opening(-351, 165, -349, 165, F, 3, s);        // gen A -> routing
        k.opening(-318, 165, -316, 165, F, 3, s);        // gen B -> routing
        k.opening(-336, 154, -336, 156, F, 3, s);        // gen A <-> gen B
        k.ironDoubleDoor(-393, F + 1, 145, Dir.NORTH);   // spine -> SCRAM room
        k.opening(-404, 165, -402, 165, F, 3, s);        // scram -> batteries
        k.opening(-382, 170, -382, 172, F, 3, s);        // batteries -> transformers
        k.opening(-366, 176, -366, 178, F, 3, s);        // transformers -> routing
        k.opening(-308, 137, -304, 137, F, 3, s);        // spine -> switchyard
    }

    private void spine() {
        for (int x = -423; x <= -299; x++) {
            k.set(x, F + 1, 138, B.slab(B.SLAB_SANDSTONE, false));
            k.set(x, F + 1, 144, B.slab(B.SLAB_SANDSTONE, false));
            k.set(x, F + 1, 141, Math.floorMod(x, 2) == 0 ? B.slab(B.SLAB_NETHER, false) : B.slab(B.SLAB_SANDSTONE, false));
            if (Math.floorMod(x, 6) == 0) {
                k.lamp(x, F + 6, 141, Dir.UP);
                k.set(x, F + 5, 138, B.IRON);
                k.set(x, F + 5, 144, B.IRON);
            }
            if (Math.floorMod(x, 3) == 0) {
                k.set(x, F + 5, 139, B.wool(B.RED));
                k.set(x, F + 5, 143, B.wool(B.YELLOW));
            } else {
                k.set(x, F + 5, 139, B.wool(B.RED));
                k.set(x, F + 5, 143, B.wool(B.YELLOW));
            }
        }
        k.sign(-423, F + 3, 141, Dir.EAST, "§lLEVEL 5", "Reactor &", "Power", "§4Dosimeter!");
        k.c.room("Level 5 Main Corridor", LV, -424, F, 137, -298, F + 6, 145, -360, F + 1, 141);
    }

    private void control() {
        int x1 = -371, x2 = -346, z1 = 106, z2 = 136;
        // tiered control desks facing the reactor windows (east)
        for (int tier = 0; tier < 3; tier++) {
            int x = x2 - 3 - tier * 5;
            for (int z = 108; z <= 124; z++) {
                if (z == 116) {
                    continue;
                }
                k.set(x, F + 1, z, B.stairs(B.QUARTZ_STAIRS, Dir.EAST, true));
                int m = Math.floorMod(z + tier, 4);
                k.set(x, F + 2, z, m == 0 ? B.repeater(Dir.EAST, 1 + (z % 4)) : m == 1 ? B.comparator(Dir.WEST)
                        : m == 2 ? B.of(B.DAYLIGHT_SENSOR) : B.comparator(Dir.EAST));
                if (Math.floorMod(z, 2) == 0) {
                    k.chair(x - 1, F + 1, z, B.DARK_OAK, Dir.EAST);
                }
            }
        }
        // indicator wall (west): lamps and black panels
        for (int z = z1 + 1; z <= z2 - 1; z++) {
            for (int y = F + 2; y <= F + 5; y++) {
                boolean lamp = Math.floorMod(z + y, 3) == 0;
                if (lamp) {
                    k.set(x1 - 1, y, z, B.GLOW);
                } else {
                    k.set(x1 - 1, y, z, B.clay(B.BLACK));
                }
            }
        }
        // chief engineer's station
        k.chest(x1, F + 1, z2, Dir.EAST, Loot.reactorControl());
        k.set(x1, F + 1, z2 - 1, B.WORKBENCH_B);
        k.sign(-358, F + 4, 136, Dir.NORTH, "§e§lREACTOR", "§e§lCONTROL", "Core output", "§a  NOMINAL");
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.c.room("Reactor Control Room", LV, -372, F, 105, -345, F + 7, 137, -360, F + 1, 130);
    }

    private void turbines() {
        int x1 = -423, x2 = -373, z1 = 96, z2 = 136;
        for (int t = 0; t < 2; t++) {
            int cz = 105 + t * 20;
            double cy = F + 3;
            for (int x = x1 + 3; x <= x2 - 6; x++) {
                for (int y = F + 1; y <= F + 5; y++) {
                    for (int z = cz - 3; z <= cz + 3; z++) {
                        double d = Math.sqrt((y - cy) * (y - cy) + (z - cz) * (z - cz));
                        if (d <= 2.6) {
                            int b = Math.floorMod(x, 6) == 0 ? B.SB : (d < 1.5 ? B.IRON : B.IRON);
                            if (Math.floorMod(x, 6) == 3 && d > 1.8 && y > cy) {
                                b = B.sglass(B.ORANGE);
                            }
                            k.set(x, y, z, b);
                        }
                    }
                }
            }
            // generator housing and exhaust pipe
            k.fill(x2 - 5, F + 1, cz - 3, x2 - 1, F + 5, cz + 3, B.QUARTZ);
            k.fill(x2 - 4, F + 2, cz - 3, x2 - 2, F + 4, cz - 3, B.clay(B.YELLOW));
            k.lamp(x2 - 3, F + 3, cz - 3, Dir.SOUTH);
            k.fill(x1 + 1, F + 1, cz - 1, x1 + 2, F + 6, cz + 1, B.SB);
            k.set(x1 + 2, F + 3, cz, B.SB_CHISELED);
            // catwalk over the turbine
            for (int x = x1 + 3; x <= x2 - 6; x++) {
                k.set(x, F + 1, cz + 5, B.slab(B.SLAB_STONEBRICK, false));
                k.set(x, F + 1, cz - 5, B.slab(B.SLAB_STONEBRICK, false));
            }
        }
        // wall lights
        for (int x = x1 + 2; x <= x2 - 2; x += 6) {
            k.lamp(x, F + 5, 95, Dir.NORTH);
            k.set(x, F + 6, 115, B.GLOW);
        }
        for (int z = z1 + 2; z <= z2 - 2; z += 6) {
            k.lamp(x1 - 1, F + 4, z, Dir.WEST);
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 6, B.GLOW);
        k.sign(-398, F + 4, 136, Dir.NORTH, "§lTURBINE HALL", "T-1  T-2", "§4Hearing prot.", "§4required");
        k.c.room("Turbine Hall", LV, -424, F, 95, -372, F + 7, 137, -398, F + 1, 115);
    }

    private void generator(int x1, int x2, String name) {
        int z1 = 146, z2 = 164;
        for (int x = x1 + 2; x <= x2 - 2; x += 4) {
            for (int z = 150; z <= 158; z += 4) {
                // generator block: iron casing, lit furnace core, exhaust
                k.set(x, F + 1, z, B.litFurnace(Dir.SOUTH));
                k.set(x + 1, F + 1, z, B.IRON);
                k.set(x, F + 2, z, B.IRON);
                k.set(x + 1, F + 2, z, B.of(B.NOTE_BLOCK));
                k.set(x, F + 3, z, B.of(B.NETHER_FENCE));
                k.set(x, F + 4, z, B.of(B.NETHER_FENCE));
            }
        }
        k.chest(x1, F + 1, z2, Dir.EAST, Loot.generator());
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        int mx = (x1 + x2) / 2;
        k.sign(mx, F + 4, z1, Dir.SOUTH, "§l" + (name.endsWith("A") ? "GENERATOR A" : "GENERATOR B"), "Diesel backup", "Auto-start", "");
        k.c.room(name, LV, x1 - 1, F, 145, x2 + 1, F + 7, 165, mx, F + 1, 147);
    }

    private void routing() {
        int x1 = -365, x2 = -299, z1 = 166, z2 = 184;
        // cable trays: coloured wool bundles along both walls and ceiling
        for (int x = x1; x <= x2; x++) {
            k.set(x, F + 5, z1, B.wool(B.RED));
            k.set(x, F + 4, z1, B.wool(B.YELLOW));
            k.set(x, F + 5, z2, B.wool(B.BLUE));
            k.set(x, F + 4, z2, B.wool(B.BLACK));
            k.set(x, F + 6, 175, Math.floorMod(x, 8) == 0 ? B.IRON : B.of(B.NETHER_FENCE));
            if (Math.floorMod(x, 8) == 4) {
                // breaker panels on the walls
                k.set(x, F + 2, z1, B.clay(B.GRAY));
                k.set(x, F + 2, z1 + 1, B.lever(Dir.SOUTH, Math.floorMod(x, 16) == 4));
                k.set(x, F + 2, z2, B.clay(B.GRAY));
                k.set(x, F + 2, z2 - 1, B.lever(Dir.NORTH, Math.floorMod(x, 16) != 4));
            }
        }
        // junction boxes down the middle
        for (int x = x1 + 4; x <= x2 - 4; x += 8) {
            k.set(x, F + 1, 175, B.dropper(Dir.UP));
            k.set(x, F + 2, 175, B.IRON);
            k.set(x + 1, F + 1, 175, B.dropper(Dir.UP));
            k.set(x + 1, F + 2, 175, B.IRON);
        }
        k.ceilingGrid(x1 + 1, z1 + 2, x2 - 1, z2 - 2, F + 7, 5, B.LAMP);
        k.sign(-334, F + 3, 166, Dir.SOUTH, "POWER ROUTING", "Bus A red", "Bus B yellow", "§4Live wires");
        k.c.room("Power Routing Hall", LV, -366, F, 165, -298, F + 7, 185, -334, F + 1, 170);
    }

    private void scram() {
        int x1 = -423, x2 = -383, z1 = 146, z2 = 164;
        // SCRAM console: a big red panel with the shutdown lever that switches the alarm beacons
        int cx = -403;
        k.fill(cx - 3, F + 2, z1 - 1, cx + 3, F + 5, z1 - 1, B.clay(B.RED));
        for (int x = cx - 3; x <= cx + 3; x++) {
            k.set(x, F + 1, z1, B.stairs(B.QUARTZ_STAIRS, Dir.SOUTH, true));
        }
        // SCRAM lever on the red panel: it powers the panel block, which lights the two alarm lamps beside it
        k.set(cx, F + 1, z1, B.slab(B.SLAB_QUARTZ, false));
        k.set(cx, F + 2, z1 - 1, B.clay(B.RED));
        k.set(cx - 1, F + 2, z1 - 1, B.of(B.LAMP_OFF));
        k.set(cx + 1, F + 2, z1 - 1, B.of(B.LAMP_OFF));
        k.set(cx, F + 2, z1, B.lever(Dir.SOUTH, false));
        k.c.marker("scram", cx, F + 2, z1, "SCRAM lever toggles the alarm lamps");
        k.sign(cx, F + 3, z1, Dir.SOUTH, "§4§lS C R A M", "§4Emergency", "§4shutdown", "§8pull lever");
        // wall of warning lights (static) and hazard floor
        for (int x = x1 + 1; x <= x2 - 1; x++) {
            if (Math.floorMod(x, 4) == 0) {
                k.set(x, F + 5, z2 + 1, B.GLOW);
            }
        }
        for (int x = cx - 5; x <= cx + 5; x++) {
            k.set(x, F + 1, z1 + 2, Math.floorMod(x, 2) == 0 ? B.slab(B.SLAB_SANDSTONE, false) : B.slab(B.SLAB_NETHER, false));
        }
        // radiation suits
        for (int x = x1 + 2; x <= x1 + 10; x += 4) {
            EntitySpec e = k.armorStand(x, F + 1, z2, Dir.NORTH);
            e.helmet = Loot.leather(298, Loot.HAZMAT, "Rad Suit Hood");
            e.chest = Loot.leather(299, Loot.HAZMAT, "Rad Suit");
            e.legs = Loot.leather(300, Loot.HAZMAT, "Rad Suit Trousers");
            e.boots = Loot.leather(301, Loot.BLACK, "Rad Boots");
        }
        k.chest(x2, F + 1, z2, Dir.WEST, Loot.medical());
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 5, B.LAMP);
        k.c.room("Emergency Shutdown (SCRAM) Room", LV, -424, F, 145, -382, F + 7, 165, -403, F + 1, 150);
    }

    private void batteries() {
        int x1 = -423, x2 = -383, z1 = 166, z2 = 184;
        for (int x = x1 + 2; x <= x2 - 2; x += 3) {
            for (int z = 169; z <= 181; z += 4) {
                k.set(x, F + 1, z, B.of(B.LAPIS_BLOCK));
                k.set(x, F + 2, z, B.of(B.LAPIS_BLOCK));
                k.set(x, F + 3, z, B.IRON);
                k.set(x, F + 4, z, B.of(B.DAYLIGHT_SENSOR));
            }
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.sign(-403, F + 3, 166, Dir.SOUTH, "BATTERY BANK", "§8UPS 48 h", "", "");
        k.c.room("Battery Bank", LV, -424, F, 165, -382, F + 7, 185, -403, F + 1, 168);
    }

    private void transformers() {
        int x1 = -381, x2 = -367, z1 = 154, z2 = 184;
        for (int z = 158; z <= 180; z += 7) {
            k.fill(x1 + 3, F + 1, z, x2 - 3, F + 3, z + 2, B.IRON);
            for (int x = x1 + 3; x <= x2 - 3; x += 2) {
                k.set(x, F + 4, z + 1, B.BARS);
                k.set(x, F + 2, z - 1, B.BARS);
                k.set(x, F + 2, z + 3, B.BARS);
            }
            k.sign(x1 + 2, F + 2, z + 1, Dir.WEST, "§4DANGER", "§4HIGH VOLTAGE", "", "");
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.c.room("Transformer Room", LV, -382, F, 153, -366, F + 7, 185, -369, F + 1, 170);
    }

    private void switchyard() {
        int x1 = -313, x2 = -299, z1 = 98, z2 = 136;
        for (int z = z1 + 1; z <= z2 - 1; z++) {
            k.set(x2, F + 2, z, B.clay(B.GRAY));
            k.set(x2 - 1, F + 2, z, Math.floorMod(z, 2) == 0 ? B.lever(Dir.WEST, Math.floorMod(z, 4) == 0)
                    : B.button(Dir.WEST, false));
            k.set(x2, F + 3, z, Math.floorMod(z, 3) == 0 ? B.LAMP : B.clay(B.BLACK));
            if (Math.floorMod(z, 3) == 0) {
                k.set(x2 + 1, F + 3, z, B.RS_BLOCK);
            }
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.LAMP);
        k.c.room("Switchyard", LV, -314, F, 97, -298, F + 7, 137, -306, F + 1, 134);
    }
}
