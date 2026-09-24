package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/** LEVEL 4 - Storage & Engineering (floor y=25). Hangar and storage hall are 13 blocks tall. */
public final class Level4 {
    private final Kit k;
    private final Style s = Style.engineering();
    private static final int F = L4;
    private static final String LV = "Level 4";

    public Level4(Kit k) {
        this.k = k;
    }

    public void build() {
        Style tall = s.copy();
        k.shell(-424, 137, -298, 145, F, 5, s, 6);          // spine
        k.shell(HG_X1, HG_Z1, HG_X2, HG_Z2, F, 13, hangarStyle(), 6); // hangar
        k.shell(ST_X1, ST_Z1, ST_X2, ST_Z2, F, 13, tall, 6);  // storage hall
        k.shell(-366, 145, -341, 181, F, 6, s, 6);           // repair bay
        k.shell(-383, 153, -366, 185, F, 6, s, 5);           // super smelter
        k.shell(-424, 113, -346, 117, F, 5, s, 6);           // engineering corridor
        k.shell(-372, 117, -346, 137, F, 6, s, 6);           // engineering control
        k.shell(-398, 117, -372, 137, F, 6, serverStyle(), 4); // server room
        k.shell(-424, 117, -398, 137, F, 6, s, 6);           // crafting workshop
        k.shell(-372, 91, -346, 113, F, 6, s, 6);            // machine / HVAC room
        k.shell(-398, 91, -372, 113, F, 6, arcaneStyle(), 4);  // arcane lab (enchanting & brewing)
        k.shell(-424, 91, -398, 113, F, 6, s, 6);            // redstone workshop
        k.shell(-314, 97, -298, 137, F, 6, s, 5);            // coolant pump station
        openings();
        spine();
        hangar();
        storageHall();
        repairBay();
        smelter();
        engCorridor();
        engControl();
        serverRoom();
        craftingWorkshop();
        machineRoom();
        arcaneLab();
        redstoneWorkshop();
        pumpStation();
    }

    static Style hangarStyle() {
        Style h = Style.engineering();
        h.wall = B.SB;
        h.wallLow = B.clay(B.BLACK);
        h.floor = B.SMOOTH;
        h.floorSlab = B.slab(B.SLAB_STONE, false);
        h.pillar = B.IRON;
        return h;
    }

    static Style serverStyle() {
        Style h = Style.engineering();
        h.wall = B.clay(B.BLACK);
        h.wallLow = B.IRON;
        h.wallHigh = B.clay(B.BLACK);
        h.pillar = B.IRON;
        h.floorSlab = B.slab(B.SLAB_QUARTZ, false);
        return h;
    }

    static Style arcaneStyle() {
        Style h = Style.engineering();
        h.wall = B.BOOKS;
        h.wallLow = B.planks(B.DARK_OAK);
        h.wallHigh = B.planks(B.DARK_OAK);
        h.pillar = B.log(B.DARK_OAK, 0);
        h.floorSlab = B.woodSlab(B.DARK_OAK, false);
        h.ceiling = B.planks(B.DARK_OAK);
        return h;
    }

    private void openings() {
        k.opening(-333, 145, -327, 145, F, 6, s);     // spine -> hangar (vehicle door)
        k.opening(-405, 145, -399, 145, F, 5, s);     // spine -> storage hall
        k.opening(-341, 157, -341, 169, F, 6, s);     // repair bay <-> hangar
        k.ironDoubleDoor(-356, F + 1, 145, Dir.NORTH); // spine -> repair bay
        k.opening(-366, 170, -366, 172, F, 3, s);     // repair bay -> smelter
        k.opening(-383, 170, -383, 172, F, 3, s);     // smelter -> storage hall
        k.opening(-360, 137, -358, 137, F, 3, s);     // spine -> engineering control
        k.ironDoubleDoor(-386, F + 1, 137, Dir.NORTH); // spine -> server room
        k.opening(-412, 137, -410, 137, F, 3, s);     // spine -> crafting
        k.opening(-360, 117, -359, 117, F, 3, s);     // eng control -> corridor
        k.opening(-386, 117, -385, 117, F, 3, s);     // server -> corridor
        k.opening(-411, 117, -410, 117, F, 3, s);     // crafting -> corridor
        k.opening(-360, 113, -359, 113, F, 3, s);     // corridor -> machine room
        k.door(-386, F + 1, 113, B.DARK_OAK_DOOR, Dir.NORTH, false); // corridor -> arcane lab
        k.opening(-411, 113, -410, 113, F, 3, s);     // corridor -> redstone workshop
        k.opening(-308, 137, -304, 137, F, 3, s);     // spine -> pump station
    }

    private void spine() {
        for (int x = -423; x <= -299; x++) {
            k.set(x, F + 1, 141, B.carpet(B.ORANGE));
            if (Math.floorMod(x, 6) == 0) {
                k.set(x, F + 6, 141, B.GLOW);
                k.set(x, F + 5, 138, B.IRON);
                k.set(x, F + 5, 144, B.IRON);
                k.fill(x, F + 5, 139, x, F + 5, 143, B.BARS);
            }
        }
        // pipe runs along the ceiling
        for (int x = -423; x <= -299; x++) {
            k.set(x, F + 5, 139, Math.floorMod(x, 6) == 0 ? B.IRON : B.of(B.NETHER_FENCE));
        }
        k.sign(-423, F + 3, 141, Dir.EAST, "§lLEVEL 4", "Storage &", "Engineering", "");
        k.sign(-330, F + 5, 143, Dir.NORTH, "§lHANGAR", "Vehicle bay", "Freight lift", ""); // on the beam
        k.sign(-402, F + 5, 144, Dir.NORTH, "§lMAIN STORES", "Storage hall", "", "");
        k.c.room("Level 4 Main Corridor", LV, -424, F, 137, -298, F + 6, 145, -365, F + 1, 141);
    }

    // ------------------------------------------------------------------------------------------
    private void hangar() {
        int x1 = HG_X1 + 1, x2 = HG_X2 - 1, z1 = HG_Z1 + 1, z2 = HG_Z2 - 1, top = F + 14;
        // floor markings: yellow lane lines and parking bays
        for (int x = x1; x <= x2; x++) {
            k.set(x, F + 1, 151, B.slab(B.SLAB_SANDSTONE, false));
            k.set(x, F + 1, 175, B.slab(B.SLAB_SANDSTONE, false));
        }
        for (int z = 152; z <= 174; z++) {
            if (Math.floorMod(z, 2) == 0) {
                k.set(-322, F + 1, z, B.slab(B.SLAB_SANDSTONE, false));
            }
        }
        // freight lift landing (x -311..-301, z 155..165): corner pillars, hazard pad, lift post
        for (int x = FL_X1; x <= FL_X2; x++) {
            for (int z = FL_Z1; z <= FL_Z2; z++) {
                boolean edge = x == FL_X1 || x == FL_X2 || z == FL_Z1 || z == FL_Z2;
                k.set(x, F, z, edge ? B.clay(Math.floorMod(x + z, 2) == 0 ? B.YELLOW : B.BLACK) : B.IRON);
                k.set(x, F + 1, z, B.A);
            }
        }
        for (int x : new int[]{FL_X1, FL_X2}) {
            for (int z : new int[]{FL_Z1, FL_Z2}) {
                k.fill(x, F + 1, z, x, top - 1, z, B.IRON);
            }
        }
        k.fill(-311, F + 1, 156, -311, top - 1, 156, B.IRON);
        k.set(-303, F + 1, 156, B.IRON);
        k.set(-303, F + 2, 156, B.IRON);
        k.sign(-303, F + 2, 157, Dir.SOUTH, "[Lift Up]", "Loading Bay B", "Surface", "§8freight");
        k.sign(FL_X1, F + 3, FL_Z2 + 1, Dir.SOUTH, "§eFREIGHT LIFT", "Stand on pad", "use [Lift Up]", "");
        // vehicles
        apc(-338, 154);
        truck(-338, 166);
        helicopter(-322, 164);
        // cargo containers
        container(-318, 147, B.BLUE);
        container(-318, 176, B.RED);
        container(-326, 176, B.GREEN);
        // overhead gantry crane rails and a hook
        for (int x = x1; x <= x2; x++) {
            k.set(x, top - 1, 150, B.IRON);
            k.set(x, top - 1, 176, B.IRON);
        }
        for (int z = 150; z <= 176; z++) {
            k.set(-326, top - 2, z, B.IRON);
        }
        k.fill(-326, top - 6, 162, -326, top - 3, 162, B.BARS);
        k.set(-326, top - 7, 162, B.IRON);
        // fuel station and pallets
        for (int z = 170; z <= 174; z += 2) {
            k.set(-299, F + 1, z, B.IRON);
            k.set(-299, F + 2, z, B.of(B.CAULDRON, 3));
        }
        k.sign(-298, F + 3, 172, Dir.WEST, "§4FUEL POINT", "No smoking", "", "");
        for (int x = -300; x >= -306; x -= 3) {
            k.set(x, F + 1, 147, B.woodSlab(B.OAK, false));
            k.set(x, F + 2, 147, B.of(B.HAY_BALE));
        }
        // cargo pallets and a supply chest by the freight lift
        for (int x = -318; x <= -312; x += 2) {
            k.set(x, F + 1, 172, B.woodSlab(B.SPRUCE, false));
            k.set(x + 1, F + 1, 172, B.log(B.SPRUCE, 1));
        }
        k.chest(-315, F + 1, 173, Dir.NORTH, Loot.loadingBay());
        // lighting: big overhead lamps
        for (int x = x1 + 3; x <= x2 - 2; x += 6) {
            for (int z = z1 + 3; z <= z2 - 2; z += 6) {
                if (x >= FL_X1 && x <= FL_X2 && z >= FL_Z1 && z <= FL_Z2) {
                    continue;
                }
                k.hangingLight(x, top - 3, z, top, B.BARS, B.GLOW);
            }
        }
        for (int z = z1 + 2; z <= z2 - 2; z += 5) {
            k.set(HG_X2, F + 5, z, B.GLOW);
            k.set(HG_X1, F + 9, z, B.GLOW);
        }
        k.wallBanner(-330, top - 3, z2, Dir.NORTH, B.YELLOW, "bs", "black", "ts", "black");
        k.c.room("Hangar & Vehicle Bay", LV, HG_X1, F, HG_Z1, HG_X2, top, HG_Z2, -320, F + 1, 160);
        k.c.room("Freight Lift (Hangar)", LV, FL_X1, F, FL_Z1, FL_X2, F + 4, FL_Z2, -305, F + 1, 160);
    }

    private void apc(int x, int z) {
        int y = F + 1;
        // 8 long (x), 5 wide (z), olive armour, coal wheels
        for (int dx = 0; dx < 8; dx++) {
            for (int dz = 0; dz < 5; dz++) {
                boolean wheel = (dx == 1 || dx == 3 || dx == 5) && (dz == 0 || dz == 4);
                k.set(x + dx, y, z + dz, wheel ? B.COAL : B.clay(B.GREEN));
                k.set(x + dx, y + 1, z + dz, dz == 0 || dz == 4 ? B.clay(B.GREEN) : B.A);
            }
        }
        k.fill(x, y + 2, z, x + 7, y + 2, z + 4, B.clay(B.GREEN));
        k.fill(x + 6, y + 1, z + 1, x + 7, y + 1, z + 3, B.clay(B.GREEN));
        k.set(x + 7, y + 1, z + 2, B.PANE);
        k.set(x + 3, y + 3, z + 2, B.clay(B.GREEN));
        k.fill(x + 4, y + 3, z + 2, x + 6, y + 3, z + 2, B.of(B.NETHER_FENCE));
        k.set(x, y + 1, z + 2, B.doorLower(B.IRON_DOOR, Dir.WEST, false));
        k.set(x, y + 2, z + 2, B.doorUpper(B.IRON_DOOR, false));
        k.set(x - 1, y + 2, z + 3, B.button(Dir.WEST, false));
        k.c.marker("irondoor", x, y + 1, z + 2, "WEST");
        k.set(x + 8, y, z + 1, B.GLOW);
        k.set(x + 8, y, z + 3, B.GLOW);
        k.fill(x + 1, y + 1, z + 1, x + 5, y + 1, z + 3, B.A);
        k.stoneChair(x + 5, y + 1, z + 1, B.SPRUCE_STAIRS, Dir.EAST);
        k.stoneChair(x + 5, y + 1, z + 3, B.SPRUCE_STAIRS, Dir.EAST);
        k.set(x + 2, y + 1, z + 1, B.chest(Dir.SOUTH));
    }

    private void truck(int x, int z) {
        int y = F + 1;
        for (int dx = 0; dx < 10; dx++) {
            for (int dz = 0; dz < 4; dz++) {
                boolean wheel = (dx == 1 || dx == 2 || dx == 7 || dx == 8) && (dz == 0 || dz == 3);
                k.set(x + dx, y, z + dz, wheel ? B.COAL : B.clay(B.GRAY));
            }
        }
        k.fill(x, y + 1, z, x + 6, y + 3, z + 3, B.wool(B.GREEN));
        k.fill(x + 7, y + 1, z, x + 9, y + 2, z + 3, B.clay(B.GREEN));
        k.fill(x + 9, y + 2, z, x + 9, y + 2, z + 3, B.PANE);
        k.fill(x + 7, y + 3, z, x + 9, y + 3, z + 3, B.slab(B.SLAB_STONE, false));
    }

    private void helicopter(int x, int z) {
        int y = F + 1;
        // skids
        for (int dx = 0; dx < 7; dx++) {
            k.set(x + dx, y, z, B.of(B.NETHER_FENCE));
            k.set(x + dx, y, z + 4, B.of(B.NETHER_FENCE));
        }
        // body
        k.fill(x + 1, y + 1, z + 1, x + 6, y + 3, z + 3, B.clay(B.BLACK));
        k.fill(x + 6, y + 2, z + 1, x + 6, y + 3, z + 3, B.sglass(B.BLACK));
        k.fill(x + 2, y + 2, z + 1, x + 4, y + 2, z + 1, B.sglass(B.BLACK));
        // tail boom and rotor
        k.fill(x - 6, y + 3, z + 2, x, y + 3, z + 2, B.clay(B.BLACK));
        k.fill(x - 6, y + 4, z + 2, x - 6, y + 5, z + 2, B.clay(B.BLACK));
        k.set(x + 3, y + 4, z + 2, B.IRON);
        for (int d = -5; d <= 5; d++) {
            k.set(x + 3 + d, y + 5, z + 2, B.of(B.NETHER_FENCE));
            k.set(x + 3, y + 5, z + 2 + d, B.of(B.NETHER_FENCE));
        }
        k.set(x + 3, y + 5, z + 2, B.IRON);
    }

    private void container(int x, int z, int color) {
        int y = F + 1;
        for (int dx = 0; dx < 7; dx++) {
            for (int dz = 0; dz < 3; dz++) {
                for (int dy = 0; dy < 3; dy++) {
                    k.set(x + dx, y + dy, z + dz, B.clay(color));
                }
            }
            if (dx % 2 == 0) {
                k.set(x + dx, y + 1, z, B.clay(B.BLACK));
            }
        }
    }

    // ------------------------------------------------------------------------------------------
    private void storageHall() {
        int x1 = ST_X1 + 1, x2 = ST_X2 - 1, z1 = ST_Z1 + 1, z2 = ST_Z2 - 1, top = F + 14;
        String[][] aisles = {
                {"Stone", "Cobble", "Bricks", "Stone Bricks", "Sand/Glass", "Dirt/Gravel", "Clay", "Quartz"},
                {"Oak", "Spruce", "Birch", "Jungle", "Acacia", "Dark Oak", "Planks", "Sticks"},
                {"Coal", "Iron", "Gold", "Diamond", "Emerald", "Lapis", "Redstone", "Obsidian"},
                {"Redstone", "Pistons", "Repeaters", "Hoppers", "Rails", "Lamps", "TNT", "Misc Mech"},
                {"Bones", "String", "Gunpowder", "Rotten Flesh", "Slime", "Ender Pearls", "Spider Eyes",
                        "Leather"},
                {"Swords", "Bows/Arrows", "Iron Armour", "Diamond Arm.", "Shields/Misc", "Enchanted",
                        "Horse Gear", "Spares"},
                {"Wheat", "Seeds", "Carrots", "Potatoes", "Meat", "Fish", "Baked Goods", "Golden Food"},
                {"Pickaxes", "Shovels", "Axes", "Hoes", "Shears/Rods", "Buckets", "Flint/Steel",
                        "Compasses"},
                {"Nether Wart", "Blaze Rods", "Ghast Tears", "Glowstone", "Bottles", "Potions",
                        "Magma Cream", "Brew Misc"},
                {"Wool", "Dyes", "Glass", "Stained", "Carpet", "Banners", "Paintings", "Deco"},
        };
        // five double-sided shelving units (along x) on the ground floor
        int unit = 0;
        for (int zi = 151; zi <= 172; zi += 7) {
            // back-to-back rows share a solid iron spine: chests that touch sideways would merge (1.8 joins any
            // two neighbouring chests), and the spine also carries the label signs of both rows
            int zn = zi, zs = zi + 2;
            for (int x = -417; x <= -388; x++) {
                for (int y = F + 1; y <= F + 3; y++) {
                    k.set(x, y, zi + 1, B.IRON);
                }
            }
            k.storageRow(-416, -389, zn, F + 1, Dir.NORTH, B.IRON, aisles[unit * 2 % aisles.length], 2);
            k.storageRow(-416, -389, zs, F + 1, Dir.SOUTH, B.IRON, aisles[(unit * 2 + 1) % aisles.length], 2);
            // end caps with category banners
            k.set(-417, F + 1, zn, B.IRON);
            k.set(-417, F + 1, zs, B.IRON);
            k.set(-417, F + 2, zn, B.IRON);
            k.set(-417, F + 2, zs, B.IRON);
            k.set(-388, F + 1, zn, B.IRON);
            k.set(-388, F + 1, zs, B.IRON);
            unit++;
        }
        // mezzanine along the west and south walls at y F+6 with chests against the wall
        int my = F + 6;
        for (int x = x1; x <= x2; x++) {
            for (int z = z2 - 2; z <= z2; z++) {
                k.set(x, my, z, B.SB);
                k.set(x, my + 1, z, B.slab(B.SLAB_STONE, false));
            }
            k.set(x, my + 1, z2 - 3, B.BARS);
            k.set(x, my, z2 - 3, B.SB);
        }
        for (int z = z1; z <= z2; z++) {
            for (int x = x1; x <= x1 + 2; x++) {
                k.set(x, my, z, B.SB);
                k.set(x, my + 1, z, B.slab(B.SLAB_STONE, false));
            }
            if (z < z2 - 3) {
                k.set(x1 + 3, my, z, B.SB);
                k.set(x1 + 3, my + 1, z, B.BARS);
            }
        }
        String[] mezz = {"Overflow 1", "Overflow 2", "Overflow 3", "Overflow 4", "Overflow 5", "Overflow 6",
                "Overflow 7", "Overflow 8", "Overflow 9", "Overflow 10", "Overflow 11"};
        k.storageRow(x1 + 3, x2 - 1, z2, my + 1, Dir.NORTH, B.SB, mezz, 1);
        String[] mezzW = {"Nether", "End", "Music", "Maps", "Books", "Heads", "Rare", "Trophies", "Misc"};
        k.storageRow(z1 + 1, z2 - 4, x1, my + 1, Dir.EAST, B.SB, mezzW, 1);
        // stairs up to the mezzanine along the east side of the south mezzanine
        for (int i = 0; i < 6; i++) {
            int x = x2 - 6 + i, y = F + 1 + i;
            k.set(x, y, z2 - 3, B.stairs(B.STONE_BRICK_STAIRS, Dir.EAST, false));
            for (int yy = F + 1; yy < y; yy++) {
                k.set(x, yy, z2 - 3, B.SB);
            }
            k.set(x, y + 1, z2 - 4, B.BARS);
        }
        k.set(x2, my + 1, z2 - 3, B.slab(B.SLAB_STONE, false));
        k.set(x2, my, z2 - 3, B.SB);
        for (int x = x2 - 3; x <= x2 - 1; x++) {
            k.set(x, my + 1, z2 - 3, B.A);
            k.set(x, my + 2, z2 - 3, B.A);
            if (x < x2 - 1) {
                k.set(x, my, z2 - 3, B.A);
            }
        }
        // lighting
        for (int x = x1 + 3; x <= x2 - 3; x += 6) {
            for (int z = z1 + 2; z <= z2 - 4; z += 7) {
                k.hangingLight(x, top - 3, z + 2, top, B.BARS, B.GLOW);
            }
        }
        for (int x = x1 + 1; x <= x2; x += 5) {
            k.set(x, my + 4, ST_Z2, B.GLOW);
        }
        for (int z = z1 + 1; z <= z2; z += 5) {
            k.set(ST_X1, my + 4, z, B.GLOW);
            k.set(ST_X2, F + 4, z, B.GLOW);
        }
        k.sign(-402, F + 5, 146, Dir.SOUTH, "§lMAIN STORES", "Categorised", "storage", "§8label = sign");
        k.c.room("Main Storage Hall", LV, ST_X1, F, ST_Z1, ST_X2, top, ST_Z2, -402, F + 1, 147);
        k.c.room("Storage Mezzanine", LV, ST_X1, my, ST_Z1, ST_X2, top, ST_Z2, -400, my + 1, z2 - 1);
    }

    // ------------------------------------------------------------------------------------------
    private void repairBay() {
        int x1 = -365, x2 = -342, z1 = 146, z2 = 180;
        // vehicle lift platform with a jeep on it
        for (int x = -360; x <= -350; x++) {
            for (int z = 163; z <= 167; z++) {
                k.set(x, F + 1, z, (x == -360 || x == -350 || z == 163 || z == 167) ? B.clay(B.YELLOW) : B.IRON);
            }
        }
        for (int x = -358; x <= -352; x++) {
            for (int z = 164; z <= 166; z++) {
                boolean wheel = (x == -357 || x == -353) && (z == 164 || z == 166);
                k.set(x, F + 2, z, wheel ? B.COAL : B.clay(B.GREEN));
            }
        }
        k.fill(-354, F + 3, 164, -353, F + 3, 166, B.PANE);
        k.fill(-358, F + 3, 164, -356, F + 3, 166, B.clay(B.GREEN));
        // tool wall
        for (int z = z1 + 2; z <= z2 - 2; z += 3) {
            if (z >= 168 && z <= 173) {
                continue; // doorway to the smelter
            }
            k.set(x1, F + 1, z, B.anvil(false));
            k.set(x1, F + 1, z + 1, B.WORKBENCH_B);
            k.itemFrame(x1, F + 3, z, Dir.EAST, ItemSpec.of(z % 2 == 0 ? 257 : 256, 1));
        }
        k.chest(x1, F + 1, z2, Dir.EAST, Loot.repairBay());
        k.chest(x1, F + 1, z2 - 1, Dir.EAST, Loot.workshop());
        for (int x = x1 + 2; x <= x2 - 2; x += 4) {
            k.set(x, F + 1, z1, B.furnace(Dir.SOUTH));
        }
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.sign(-356, F + 4, 146, Dir.SOUTH, "§lREPAIR BAY", "Vehicles &", "equipment", "");
        k.c.room("Repair Bay", LV, -366, F, 145, -341, F + 7, 181, -350, F + 1, 150);
    }

    /**
     * Hopper-fed 12 furnace smelting array. 1.8 hoppers pull from the container above them, so a hopper
     * distribution chain would starve all but the first furnace. Instead every furnace pair has its own ore
     * double chest on top and fuel double chest behind (chest / trapped chest alternate so pairs never merge);
     * all smelted items run down one hopper collection line into the output chest.
     */
    private void smelter() {
        int x1 = -382, x2 = -367, z1 = 154, z2 = 184;
        int zf = 160, xs = -380, xe = -369;
        for (int x = xs; x <= xe; x++) {
            int pair = (x - xs) / 2;
            boolean trapped = pair % 2 == 1;
            boolean left = (x - xs) % 2 == 0;
            k.set(x, F + 1, zf, B.hopper(Dir.EAST));                // output collection line
            k.set(x, F + 2, zf, B.furnace(Dir.SOUTH));
            k.set(x, F + 3, zf, B.hopper(Dir.DOWN));                 // ore into the furnace top
            k.set(x, F + 4, zf, trapped ? B.trappedChest(Dir.SOUTH) : B.chest(Dir.SOUTH)); // ore chest (front)
            k.set(x, F + 2, zf - 1, B.hopper(Dir.SOUTH));           // fuel into the furnace back
            k.set(x, F + 3, zf - 1, trapped ? B.trappedChest(Dir.NORTH) : B.chest(Dir.NORTH)); // fuel chest (aisle)
            k.set(x, F + 1, zf - 1, B.SB);
            k.set(x, F + 1, zf + 1, B.slab(B.SLAB_STONE, false));
            if (left) {
                k.c.tile(new TileSpec.Inventory(x, F + 3, zf - 1, Painter.spread(Loot.smelterFuel(), 27, x, F, zf)));
                k.sign(x, F + 4, zf + 1, Dir.SOUTH, "\u00a7lORE IN", "furnaces", (x - xs + 1) + " & " + (x - xs + 2),
                        "\u00a78fuel: behind");
                k.sign(x, F + 3, zf - 2, Dir.NORTH, "\u00a7lFUEL IN", "furnaces", (x - xs + 1) + " & " + (x - xs + 2), "");
            }
        }
        k.chest(xe + 1, F + 1, zf, Dir.EAST);                       // output chest (double)
        k.chest(xe + 1, F + 1, zf + 1, Dir.EAST);
        k.set(xe + 1, F + 2, zf, B.slab(B.SLAB_STONE, false));
        k.sign(xe + 2, F + 2, zf, Dir.EAST, "\u00a7lOUTPUT", "smelted items", "arrive here", "");
        // manual furnace row and crafting along the south part
        for (int x = x1 + 1; x <= x2 - 1; x += 2) {
            k.set(x, F + 1, z2, B.furnace(Dir.NORTH));
            k.set(x + 1, F + 1, z2, B.WORKBENCH_B);
        }
        k.chest(x2, F + 1, 176, Dir.WEST, Loot.smelterFuel());
        k.chest(x2, F + 1, 177, Dir.WEST);
        // lighting (glowstone only: no redstone near the hoppers)
        for (int x = x1 + 1; x <= x2 - 1; x += 4) {
            for (int z = z1 + 9; z <= z2 - 1; z += 4) {
                k.set(x, F + 7, z, B.GLOW);
            }
        }
        k.set(x1, F + 3, 156, B.GLOW);
        k.set(x2, F + 3, 156, B.GLOW);
        k.set(-374, F + 7, 162, B.GLOW);
        k.sign(-367, F + 3, 168, Dir.WEST, "§lSUPER SMELTER", "12 furnaces", "hopper fed", "");
        k.c.room("Super Smelter", LV, -383, F, 153, -366, F + 7, 185, -375, F + 1, 165);
        k.c.marker("smelter", xs, F + 2, zf, "12 furnace hopper smelter");
    }

    // ------------------------------------------------------------------------------------------
    private void engCorridor() {
        for (int x = -423; x <= -347; x++) {
            k.set(x, F + 1, 115, B.carpet(B.ORANGE));
            if (Math.floorMod(x, 6) == 0) {
                k.set(x, F + 6, 115, B.GLOW);
            }
            k.set(x, F + 5, 114, Math.floorMod(x, 4) == 0 ? B.IRON : B.BARS);
        }
        k.c.room("Engineering Corridor", LV, -424, F, 113, -346, F + 6, 117, -385, F + 1, 115);
    }

    private void engControl() {
        int x1 = -371, x2 = -347, z1 = 118, z2 = 136;
        // facility status board (wool mosaic of the levels) on the west wall
        for (int y = F + 2; y <= F + 6; y++) {
            for (int z = z1 + 2; z <= z2 - 2; z++) {
                int lvl = F + 6 - y;
                int[] cols = {B.RED, B.GREEN, B.LIGHT_BLUE, B.ORANGE, B.YELLOW};
                k.set(x1 - 1, y, z, z % 3 == 0 ? B.wool(B.BLACK) : B.wool(cols[Math.max(0, Math.min(4, lvl))]));
            }
        }
        // control desks with levers/buttons
        for (int z = z1 + 2; z <= z2 - 2; z++) {
            k.set(x1 + 3, F + 1, z, B.stairs(B.STONE_BRICK_STAIRS, Dir.EAST, true));
            k.set(x1 + 3, F + 2, z, z % 3 == 0 ? B.comparator(Dir.WEST) : (z % 3 == 1 ? B.repeater(Dir.EAST, 2)
                    : B.of(B.DAYLIGHT_SENSOR)));
            if (z % 3 == 0) {
                k.chair(x1 + 4, F + 1, z, B.SPRUCE, Dir.WEST);
            }
        }
        // reactor access airlock on the east wall (door carved by the reactor builder)
        k.sign(x2, F + 3, 123, Dir.WEST, "§e§lREACTOR", "§eACCESS", "Catwalk L4", "§4Dosimeter!");
        k.chest(x2, F + 1, z2, Dir.WEST, Loot.engineering());
        k.set(x2, F + 1, z2 - 1, B.WORKBENCH_B);
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.c.room("Engineering Control", LV, -372, F, 117, -346, F + 7, 137, -358, F + 1, 130);
    }

    private void serverRoom() {
        int x1 = -397, x2 = -373, z1 = 118, z2 = 136;
        for (int x = x1 + 2; x <= x2 - 2; x += 3) {
            for (int z = z1 + 2; z <= z2 - 2; z++) {
                if (z == 127) {
                    continue;
                }
                k.set(x, F + 1, z, B.IRON);
                k.set(x, F + 2, z, Math.floorMod(z, 2) == 0 ? B.clay(B.BLACK) : B.of(B.NOTE_BLOCK));
                k.set(x, F + 3, z, Math.floorMod(z + x, 3) == 0 ? B.GLOW : B.clay(B.BLACK));
                k.set(x, F + 4, z, B.IRON);
            }
        }
        k.set(x2, F + 1, z2, B.chest(Dir.WEST));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 3, B.SEA);
        k.sign(-385, F + 3, 136, Dir.NORTH, "SERVER ROOM", "§8Mainframe", "§8DEEPWATCH-2", "");
        k.c.room("Server Room", LV, -398, F, 117, -372, F + 7, 137, -385, F + 1, 127);
    }

    private void craftingWorkshop() {
        int x1 = -423, x2 = -399, z1 = 118, z2 = 136;
        for (int x = x1 + 2; x <= x2 - 2; x += 3) {
            k.set(x, F + 1, 125, B.WORKBENCH_B);
            k.set(x + 1, F + 1, 125, B.WORKBENCH_B);
            k.set(x, F + 1, 129, x % 2 == 0 ? B.anvil(true) : B.WORKBENCH_B);
        }
        String[] labels = {"Planks", "Sticks", "Iron", "String", "Leather", "Feathers", "Flint", "Paper"};
        k.storageRow(x1 + 1, x2 - 1, z1, F + 1, Dir.SOUTH, B.planks(B.SPRUCE), labels, 2);
        for (int x = x1 + 1; x <= x2 - 1; x += 2) {
            k.set(x, F + 1, z2, B.furnace(Dir.NORTH));
        }
        k.c.tile(new TileSpec.Inventory(x1 + 1, F + 1, z1, Painter.spread(Loot.workshop(), 27, 1, 1, 7)));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.sign(-411, F + 3, 136, Dir.NORTH, "WORKSHOP", "Crafting", "", "");
        k.c.room("Crafting Workshop", LV, -424, F, 117, -398, F + 7, 137, -411, F + 1, 133);
    }

    private void machineRoom() {
        int x1 = -371, x2 = -347, z1 = 92, z2 = 112;
        // HVAC fans: rings of pistons/iron around a bars hub on the north wall
        for (int f = 0; f < 3; f++) {
            int fx = x1 + 4 + f * 8;
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    k.set(fx + dx, F + 3 + dy, z1, (dx == 0 && dy == 0) ? B.IRON : B.BARS);
                }
            }
            k.set(fx, F + 3, z1 - 1, B.IRON);
        }
        // boilers (lit furnaces) and pipe runs
        for (int x = x1 + 2; x <= x2 - 2; x += 4) {
            k.set(x, F + 1, 104, B.furnace(Dir.SOUTH));
            k.set(x, F + 2, 104, B.GLOW);
            k.set(x, F + 3, 104, B.of(B.CAULDRON));
            for (int y = F + 4; y <= F + 6; y++) {
                k.set(x, y, 104, B.of(B.NETHER_FENCE));
            }
        }
        for (int x = x1; x <= x2; x++) {
            k.set(x, F + 6, 100, B.IRON);
            k.set(x, F + 6, 108, B.GLS);
        }
        k.chest(x2, F + 1, z2, Dir.WEST, Loot.engineering());
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 5, B.GLOW);
        k.set(-359, F + 3, 112, B.GLOW);
        k.c.room("Machine Room & HVAC", LV, -372, F, 91, -346, F + 7, 113, -360, F + 1, 110);
    }

    private void arcaneLab() {
        int x1 = -397, x2 = -373, z1 = 92, z2 = 112;
        k.enchantingNook(-385, F + 1, 99, Dir.SOUTH);
        // the ring next to the table must stay air for the bookshelves to count: glowstone floor below it
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    k.set(-385 + dx, F, 99 + dz, B.GLOW);
                }
            }
        }
        // brewing corner
        for (int x = x1 + 1; x <= x1 + 7; x += 2) {
            k.set(x, F + 1, z2, B.stairs(B.QUARTZ_STAIRS, Dir.NORTH, true));
            k.set(x, F + 2, z2, B.of(B.BREWING_STAND));
            k.set(x + 1, F + 1, z2, B.of(B.CAULDRON, 3));
        }
        k.chest(x2, F + 1, z2, Dir.WEST, Loot.brewing());
        k.chest(x2, F + 1, z2 - 1, Dir.WEST, Loot.enchanting());
        k.set(x2, F + 1, z2 - 3, B.anvil(false));
        k.set(x2, F + 1, z2 - 5, B.enderChest(Dir.WEST));
        k.rug(-389, 104, -381, 108, F, B.carpet(B.PURPLE));
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.set(-385, F + 7, 99, B.GLOW);
        k.sign(-385, F + 3, 114, Dir.SOUTH, "§5ARCANE LAB", "Enchanting", "& Brewing", "");
        k.c.room("Arcane Lab (Enchanting & Brewing)", LV, -398, F, 91, -372, F + 7, 113, -385, F + 1, 106);
    }

    private void redstoneWorkshop() {
        int x1 = -423, x2 = -399, z1 = 92, z2 = 112;
        // test benches: grid of smooth stone slabs with components
        for (int x = x1 + 3; x <= x2 - 3; x++) {
            for (int z = 96; z <= 104; z += 4) {
                k.set(x, F + 1, z, B.SMOOTH);
                int m = Math.floorMod(x + z, 5);
                if (m == 0) {
                    k.set(x, F + 2, z, B.repeater(Dir.EAST, 2));
                } else if (m == 2) {
                    k.set(x, F + 2, z, B.comparator(Dir.WEST));
                } else if (m == 3) {
                    k.set(x, F + 2, z, B.of(B.LAMP_OFF));
                }
            }
        }
        k.set(x1 + 1, F + 1, z2, B.of(B.PISTON, 1));
        k.set(x1 + 2, F + 1, z2, B.of(B.STICKY_PISTON, 1));
        k.set(x1 + 3, F + 1, z2, B.dropper(Dir.UP));
        k.set(x1 + 4, F + 1, z2, B.dispenser(Dir.UP));
        k.set(x1 + 5, F + 1, z2, B.of(B.NOTE_BLOCK));
        k.chest(x2, F + 1, z2, Dir.WEST, Loot.redstoneLab());
        k.chest(x2, F + 1, z2 - 1, Dir.WEST);
        k.set(x2, F + 1, z2 - 2, B.WORKBENCH_B);
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.sign(-411, F + 3, 114, Dir.SOUTH, "§4REDSTONE", "§4WORKSHOP", "", "");
        k.c.room("Redstone Workshop", LV, -424, F, 91, -398, F + 7, 113, -411, F + 1, 108);
    }

    private void pumpStation() {
        int x1 = -313, x2 = -299, z1 = 98, z2 = 136;
        for (int z = z1 + 2; z <= z2 - 4; z += 5) {
            // pump: iron casing, cauldron, glass pipe with water to the ceiling
            k.set(x1 + 3, F + 1, z, B.IRON);
            k.set(x1 + 3, F + 2, z, B.of(B.CAULDRON, 3));
            k.set(x1 + 4, F + 1, z, B.of(B.PISTON, 1));
            k.set(x1 + 2, F + 1, z, B.of(B.NOTE_BLOCK));
            for (int y = F + 3; y <= F + 6; y++) {
                k.set(x1 + 3, y, z, B.GLS);
            }
            for (int x = x1 + 4; x <= x2; x++) {
                k.set(x, F + 6, z, B.GLS);
            }
        }
        k.chest(x2, F + 1, z2, Dir.WEST, Loot.engineering());
        k.ceilingGrid(x1 + 1, z1 + 1, x2 - 1, z2 - 1, F + 7, 4, B.GLOW);
        k.sign(-306, F + 3, 136, Dir.NORTH, "COOLANT PUMPS", "§8Loop A / B", "", "");
        k.c.room("Coolant Pump Station", LV, -314, F, 97, -298, F + 7, 137, -306, F + 1, 134);
    }
}
