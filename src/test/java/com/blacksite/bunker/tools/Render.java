package com.blacksite.bunker.tools;

import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.BunkerDesign;
import com.blacksite.bunker.design.Canvas;
import com.blacksite.bunker.design.Layout;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/** Offline design renderer: top-down plans per level and isometric cutaways. Dev tool, not shipped. */
public final class Render {

    public static void main(String[] args) throws Exception {
        String out = args.length > 0 ? args[0] : "renders";
        new File(out).mkdirs();
        long t = System.currentTimeMillis();
        Canvas c = BunkerDesign.generate(true);
        System.out.println("generated in " + (System.currentTimeMillis() - t) + " ms, cells=" + c.countSet());
        String which = args.length > 1 ? args[1] : "all";
        if (which.equals("all") || which.equals("plans")) {
            plan(c, out + "/plan_surface.png", -1, 4);
            for (int i = 1; i <= 6; i++) {
                plan(c, out + "/plan_L" + i + ".png", Layout.level(i), 4);
            }
        }
        if (which.equals("all") || which.equals("iso")) {
            iso(c, out + "/iso_surface.png", 0, 127, false, 3);
            for (int i = 1; i <= 6; i++) {
                int f = Layout.level(i);
                iso(c, out + "/iso_L" + i + ".png", f - 1, f + 6, true, 3);
            }
            iso(c, out + "/iso_reactor.png", Layout.RC_Y1, Layout.RC_Y2 + 1, true, 4, Layout.RC_X1 - 30,
                    Layout.RC_X2 + 5, Layout.RC_Z1 - 5, Layout.RC_Z2 + 20);
        }
        if (which.startsWith("isoy")) {
            // isoy:<y1>:<y2>:<x1>:<x2>:<z1>:<z2>:<scale>:<name>
            String[] p = which.split(":");
            iso(c, out + "/" + p[8] + ".png", Integer.parseInt(p[1]), Integer.parseInt(p[2]), true,
                    Integer.parseInt(p[7]), Integer.parseInt(p[3]), Integer.parseInt(p[4]), Integer.parseInt(p[5]),
                    Integer.parseInt(p[6]));
        }
        if (which.startsWith("planf")) {
            String[] p = which.split(":");
            plan(c, out + "/" + p[2] + ".png", Integer.parseInt(p[1]), 6);
        }
    }

    // ---------------------------------------------------------------------------------------------
    static int color(int b) {
        int id = B.id(b), d = B.data(b);
        switch (id) {
            case B.AIR: return 0x000000;
            case B.STONE:
                switch (d) {
                    case 1: return 0x9A6A59; case 2: return 0xA87462; case 3: return 0xBDBDBD; case 4: return 0xCFCFD2;
                    case 5: return 0x858585; case 6: return 0x8A8C8C; default: return 0x7D7D7D;
                }
            case B.GRASS: return 0x5E9D34;
            case B.DIRT: return 0x7A5436;
            case B.COBBLE: return 0x6E6E6E;
            case B.MOSSY_COBBLE: return 0x5E6E55;
            case B.PLANKS:
            case B.WOOD_SLAB:
            case B.DOUBLE_WOOD_SLAB:
                switch (d & 7) {
                    case 1: return 0x6B4E2F; case 2: return 0xC4B27B; case 3: return 0xA0714A; case 4: return 0xAA5A30;
                    case 5: return 0x3F2A15; default: return 0x9C7F4E;
                }
            case B.LOG: case B.LOG2: return 0x4E3B24;
            case B.LEAVES: case B.LEAVES2: return 0x3A7A25;
            case B.GLASS: return 0xC8E6F0;
            case B.GLASS_PANE: return 0xB4D8E6;
            case B.STAINED_GLASS: case B.STAINED_PANE: return tint(dye(d), 0.4);
            case B.WOOL: case B.CARPET: return dye(d);
            case B.STAINED_CLAY: return clay(d);
            case B.HARDENED_CLAY: return 0x965C44;
            case B.IRON_BLOCK: return 0xDCDCDC;
            case B.GOLD_BLOCK: return 0xF8D33E;
            case B.DIAMOND_BLOCK: return 0x5DECF5;
            case B.EMERALD_BLOCK: return 0x2ECC5A;
            case B.LAPIS_BLOCK: return 0x1D47A6;
            case B.REDSTONE_BLOCK: return 0xAA1A0A;
            case B.COAL_BLOCK: return 0x151515;
            case B.QUARTZ_BLOCK: case B.QUARTZ_STAIRS: return 0xEBE5DE;
            case B.STONE_BRICK: case B.STONE_BRICK_STAIRS: return d == 2 ? 0x6F6F6F : d == 1 ? 0x6A7A62 : 0x7A7A7A;
            case B.DOUBLE_SLAB: case B.SLAB:
                switch (d & 7) {
                    case 1: return 0xD8CB9B; case 3: return 0x6E6E6E; case 4: return 0x985542; case 5: return 0x7A7A7A;
                    case 6: return 0x3A1E24; case 7: return 0xEBE5DE; default: return 0xA6A6A6;
                }
            case B.BRICK: case B.BRICK_STAIRS: return 0x985542;
            case B.NETHER_BRICK: case B.NETHER_BRICK_STAIRS: case B.NETHER_FENCE: return 0x3A1E24;
            case B.OBSIDIAN: return 0x1B1528;
            case B.GLOWSTONE: return 0xFFE8A0;
            case B.SEA_LANTERN: return 0xD8F0EC;
            case B.LAMP_ON: return 0xF2B25A;
            case B.LAMP_OFF: return 0x6A4A2A;
            case B.TORCH: return 0xFFD040;
            case B.WATER: case B.WATER_FLOW: return 0x3355DD;
            case B.LAVA: case B.LAVA_FLOW: return 0xFF6A00;
            case B.IRON_BARS: return 0x9A9A9A;
            case B.CHEST: case B.TRAPPED_CHEST: return 0xA86F2A;
            case B.ENDER_CHEST: return 0x1F3A3A;
            case B.FURNACE: case B.LIT_FURNACE: case B.DISPENSER: case B.DROPPER: return 0x5A5A5A;
            case B.BOOKSHELF: return 0x7A5A36;
            case B.BED: return 0xB02020;
            case B.WORKBENCH: return 0x8A6034;
            case B.SANDSTONE: case B.SANDSTONE_STAIRS: return 0xD8CB9B;
            case B.PRISMARINE: return 0x5AA89A;
            case B.HAY_BALE: return 0xC8A020;
            case B.SPONGE: return 0xC8C850;
            case B.PACKED_ICE: case B.ICE: return 0xA0C0F0;
            case B.TNT: return 0xDB2E2E;
            case B.BEACON: return 0x80F0F0;
            case B.SOUL_SAND: return 0x54402F;
            case B.END_STONE: return 0xDEDEA0;
            case B.END_PORTAL_FRAME: return 0x3A6A5A;
            case B.DRAGON_EGG: return 0x200030;
            case B.FARMLAND: return 0x5A3A20;
            case B.WHEAT: case B.CARROTS: case B.POTATOES: return 0x7AB030;
            case B.RED_SANDSTONE: case B.RED_SANDSTONE_SLAB: case B.RED_SANDSTONE_STAIRS: return 0xB5621E;
            case B.OAK_DOOR: case B.SPRUCE_DOOR: case B.DARK_OAK_DOOR: case B.BIRCH_DOOR: return 0x8A6034;
            case B.IRON_DOOR: return 0xC0C0C0;
            case B.LADDER: return 0x9A7A40;
            case B.RAIL: case B.POWERED_RAIL: case B.ACTIVATOR_RAIL: case B.DETECTOR_RAIL: return 0xA0A0A0;
            case B.WALL_SIGN: case B.SIGN_POST: return 0xB89A5A;
            case B.HOPPER: return 0x404040;
            case B.ANVIL: return 0x404040;
            case B.CAULDRON: return 0x303030;
            case B.ENCHANT_TABLE: return 0x5A1030;
            case B.BREWING_STAND: return 0x806030;
            case B.TALL_GRASS: return 0x4F8A2A;
            case B.FLOWER: return 0xC03030;
            case B.DANDELION: return 0xE0D020;
            case B.COBBLE_WALL: return 0x707070;
            case B.WEB: return 0xD0D0D0;
            case B.GRAVEL: return 0x857F7C;
            case B.FENCE: case B.SPRUCE_FENCE: case B.DARK_OAK_FENCE: case B.FENCE_GATE: return 0x6B4E2F;
            case B.TRAPDOOR: return 0x8A6034;
            case B.IRON_TRAPDOOR: return 0xB0B0B0;
            case B.NOTE_BLOCK: case B.JUKEBOX: return 0x6A4028;
            case B.DAYLIGHT_SENSOR: return 0xB8A070;
            case B.LEVER: case B.STONE_BUTTON: case B.WOOD_BUTTON: case B.STONE_PLATE: case B.WOOD_PLATE: return 0x909090;
            case B.REDSTONE_TORCH: case B.REDSTONE_WIRE: return 0xD02020;
            case B.COMPARATOR: case B.REPEATER: return 0xA0A0A0;
            case B.PISTON: case B.STICKY_PISTON: case B.PISTON_HEAD: return 0x9A8060;
            case B.SKULL: return 0xC0C0B0;
            case B.WALL_BANNER: case B.STANDING_BANNER: return 0x202020;
            case B.FLOWER_POT: return 0x7A3A20;
            case B.CAKE: return 0xF0E0D0;
            case B.SUGAR_CANE: return 0x80C050;
            case B.MELON: return 0x70A030;
            case B.PUMPKIN: case B.JACK_O_LANTERN: return 0xD08010;
            case B.VINE: return 0x3A7A25;
            case B.SNOW_BLOCK: case B.SNOW_LAYER: return 0xF0F0F0;
            case B.CLAY: return 0x9EA4B0;
            case B.MELON_STEM: case B.PUMPKIN_STEM: return 0x60A030;
            case B.NETHERRACK: return 0x6E3530;
            default:
                if (B.isStairs(b)) {
                    return 0x8A7050;
                }
                return 0xFF00FF;
        }
    }

    static int dye(int d) {
        int[] c = {0xE9ECEC, 0xF07613, 0xBD44B3, 0x3AAFD9, 0xF8C627, 0x70B919, 0xED8DAC, 0x3E4447, 0x8E8E86, 0x158991,
                0x792AAC, 0x35399D, 0x724728, 0x546D1B, 0xA12722, 0x141519};
        return c[d & 15];
    }

    static int clay(int d) {
        int[] c = {0xD1B1A1, 0xA15325, 0x95576C, 0x706C8A, 0xBA8523, 0x677534, 0xA14E4E, 0x392A23, 0x876A61, 0x565B5B,
                0x764656, 0x4A3B5B, 0x4D3323, 0x4C532A, 0x8F3D2E, 0x251610};
        return c[d & 15];
    }

    static int tint(int rgb, double f) {
        int r = (rgb >> 16) & 255, g = (rgb >> 8) & 255, b = rgb & 255;
        r = (int) (r + (255 - r) * f);
        g = (int) (g + (255 - g) * f);
        b = (int) (b + (255 - b) * f);
        return (r << 16) | (g << 8) | b;
    }

    static int shade(int rgb, double f) {
        int r = (int) (((rgb >> 16) & 255) * f), g = (int) (((rgb >> 8) & 255) * f), b = (int) ((rgb & 255) * f);
        return (Math.min(255, r) << 16) | (Math.min(255, g) << 8) | Math.min(255, b);
    }

    /** Top-down plan: for a level, looks down from floor+3 to the floor; for the surface (f<0), the highest block. */
    static void plan(Canvas c, String file, int f, int s) throws Exception {
        int w = c.sx, h = c.sz;
        BufferedImage img = new BufferedImage(w * s, h * s, BufferedImage.TYPE_INT_RGB);
        for (int x = c.minX; x <= c.maxX; x++) {
            for (int z = c.minZ; z <= c.maxZ; z++) {
                int rgb = 0x202020;
                int top = f < 0 ? c.maxY : f + 3;
                int bottom = f < 0 ? 50 : f - 1;
                for (int y = top; y >= bottom; y--) {
                    int b = c.get(x, y, z);
                    if (b < 0) {
                        continue;
                    }
                    if (b == B.A) {
                        continue;
                    }
                    rgb = color(b);
                    double sh = f < 0 ? 0.55 + 0.45 * Math.min(1, Math.max(0, (y - 55) / 40.0)) : (y >= f + 2 ? 1.0 : 0.75);
                    if (f >= 0 && y == f + 3 && B.isOpaqueCube(b)) {
                        sh = 0.45; // walls appear dark
                    }
                    rgb = shade(rgb, sh);
                    break;
                }
                int px = (x - c.minX) * s, pz = (z - c.minZ) * s;
                for (int i = 0; i < s; i++) {
                    for (int j = 0; j < s; j++) {
                        img.setRGB(px + i, pz + j, rgb);
                    }
                }
            }
        }
        // grid every 10 blocks
        for (int x = c.minX; x <= c.maxX; x++) {
            if (Math.floorMod(x, 10) == 0) {
                for (int pz = 0; pz < h * s; pz += 2) {
                    img.setRGB((x - c.minX) * s, pz, 0x404080);
                }
            }
        }
        for (int z = c.minZ; z <= c.maxZ; z++) {
            if (Math.floorMod(z, 10) == 0) {
                for (int px = 0; px < w * s; px += 2) {
                    img.setRGB(px, (z - c.minZ) * s, 0x404080);
                }
            }
        }
        ImageIO.write(img, "png", new File(file));
        System.out.println("wrote " + file);
    }

    static void iso(Canvas c, String file, int y1, int y2, boolean cut, int s) throws Exception {
        iso(c, file, y1, y2, cut, s, c.minX, c.maxX, c.minZ, c.maxZ);
    }

    /** Isometric view from the south-east (looking north-west), clipped to y1..y2 and the x/z window. */
    static void iso(Canvas c, String file, int y1, int y2, boolean cut, int s, int x1, int x2, int z1, int z2)
            throws Exception {
        // screen: u = (x - z) * s, v = (x + z) * s/2 - y * s
        int umin = Integer.MAX_VALUE, umax = Integer.MIN_VALUE, vmin = Integer.MAX_VALUE, vmax = Integer.MIN_VALUE;
        for (int x : new int[]{x1, x2}) {
            for (int z : new int[]{z1, z2}) {
                for (int y : new int[]{y1, y2}) {
                    int u = (x - z) * s, v = (x + z) * s / 2 - y * s;
                    umin = Math.min(umin, u);
                    umax = Math.max(umax, u);
                    vmin = Math.min(vmin, v);
                    vmax = Math.max(vmax, v);
                }
            }
        }
        int W = umax - umin + 4 * s, H = vmax - vmin + 4 * s;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(new Color(0x16181c));
        g.fillRect(0, 0, W, H);
        // draw far to near: increasing x+z, then y
        for (int sum = x1 + z1; sum <= x2 + z2; sum++) {
            for (int y = y1; y <= y2; y++) {
                for (int x = Math.max(x1, sum - z2); x <= Math.min(x2, sum - z1); x++) {
                    int z = sum - x;
                    int b = c.get(x, y, z);
                    if (b < 0 || b == B.A) {
                        continue;
                    }
                    boolean topVis = y == y2 || air(c, x, y + 1, z);
                    boolean eVis = x == x2 || air(c, x + 1, y, z);
                    boolean sVis = z == z2 || air(c, x, y, z + 1);
                    if (!topVis && !eVis && !sVis) {
                        continue;
                    }
                    int col = color(b);
                    double h = B.isHalfSlab(b) && (B.data(b) & 8) == 0 ? 0.5 : 1.0;
                    int u = (x - z) * s - umin + 2 * s, v = (x + z) * s / 2 - y * s - vmin + 2 * s;
                    // cube corners in screen space: top face diamond
                    int[] tx = {u, u + s, u, u - s};
                    int ty0 = v - (int) Math.round((h - 1) * s);
                    int[] ty = {ty0 - s / 2, ty0, ty0 + s / 2, ty0};
                    // top
                    if (topVis) {
                        g.setColor(new Color(col));
                        g.fillPolygon(new Polygon(tx, ty, 4));
                    }
                    int hh = (int) Math.round(h * s);
                    // east face (x+1): right-lower side
                    if (eVis) {
                        g.setColor(new Color(shade(col, 0.72)));
                        g.fillPolygon(new Polygon(new int[]{u + s, u, u, u + s},
                                new int[]{ty0, ty0 + s / 2, ty0 + s / 2 + hh, ty0 + hh}, 4));
                    }
                    if (sVis) {
                        g.setColor(new Color(shade(col, 0.55)));
                        g.fillPolygon(new Polygon(new int[]{u - s, u, u, u - s},
                                new int[]{ty0, ty0 + s / 2, ty0 + s / 2 + hh, ty0 + hh}, 4));
                    }
                }
            }
        }
        g.dispose();
        ImageIO.write(img, "png", new File(file));
        System.out.println("wrote " + file + " " + W + "x" + H);
    }

    static boolean air(Canvas c, int x, int y, int z) {
        int b = c.get(x, y, z);
        return b < 0 || b == B.A || !B.isOpaqueCube(b);
    }
}
