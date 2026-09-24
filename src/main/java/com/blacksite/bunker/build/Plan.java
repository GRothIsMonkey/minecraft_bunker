package com.blacksite.bunker.build;

import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.BunkerDesign;
import com.blacksite.bunker.design.Canvas;
import com.blacksite.bunker.design.Layout;
import com.blacksite.bunker.design.TileSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The executable build plan: the design canvas, the world offset and the ordered block placements per phase.
 * Everything here is deterministic for a given (anchor, loot) pair, so a plan regenerated after a restart has
 * exactly the same order and a saved cursor stays valid.
 */
public final class Plan {
    public enum Phase {
        CHUNKS("Loading chunks"),
        STRUCTURE("Excavation & structure"),
        LIGHTS("Lighting"),
        FLUIDS("Water & sealed lava"),
        FIXTURES("Doors, signs, ladders & fixtures"),
        MECHANISMS("Pistons & mechanisms"),
        TILES("Chests, signs & tile data"),
        ENTITIES("Armor stands, frames, paintings, carts"),
        REPAIR("Repair pass"),
        CLEANUP("Cleanup"),
        DONE("Done");

        public final String title;

        Phase(String title) {
            this.title = title;
        }
    }

    public final Canvas canvas;
    public final int dx, dy, dz;
    public final boolean loot;
    /** canvas indices for each block phase */
    public final int[] structure, lights, fluids, fixtures, mechanisms;
    public final List<TileSpec> tiles;
    /** chunk coordinates (world) in build order */
    public final int[] chunkX, chunkZ;
    /** world bounds of all planned cells */
    public final int minX, minY, minZ, maxX, maxY, maxZ;
    public final int totalBlocks;

    private Plan(Canvas canvas, int dx, int dy, int dz, boolean loot) {
        this.canvas = canvas;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.loot = loot;
        int n = canvas.volume();
        int[] cnt = new int[5];
        int bx1 = Integer.MAX_VALUE, by1 = Integer.MAX_VALUE, bz1 = Integer.MAX_VALUE;
        int bx2 = Integer.MIN_VALUE, by2 = Integer.MIN_VALUE, bz2 = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int b = canvas.getRaw(i);
            if (b < 0) {
                continue;
            }
            cnt[category(b)]++;
            int x = canvas.xOf(i), y = canvas.yOf(i), z = canvas.zOf(i);
            bx1 = Math.min(bx1, x);
            by1 = Math.min(by1, y);
            bz1 = Math.min(bz1, z);
            bx2 = Math.max(bx2, x);
            by2 = Math.max(by2, y);
            bz2 = Math.max(bz2, z);
        }
        minX = bx1 + dx;
        minY = by1 + dy;
        minZ = bz1 + dz;
        maxX = bx2 + dx;
        maxY = by2 + dy;
        maxZ = bz2 + dz;
        long[][] keys = new long[5][];
        for (int c = 0; c < 5; c++) {
            keys[c] = new long[cnt[c]];
        }
        int[] fill = new int[5];
        int cxMin = (canvas.minX + dx) >> 4, czMin = (canvas.minZ + dz) >> 4;
        int ncz = ((canvas.maxZ + dz) >> 4) - czMin + 1;
        for (int i = 0; i < n; i++) {
            int b = canvas.getRaw(i);
            if (b < 0) {
                continue;
            }
            int c = category(b);
            int wx = canvas.xOf(i) + dx, wz = canvas.zOf(i) + dz;
            long chunk = (long) ((wx >> 4) - cxMin) * ncz + ((wz >> 4) - czMin);
            keys[c][fill[c]++] = (chunk << 32) | (i & 0xFFFFFFFFL);
        }
        int[][] out = new int[5][];
        int total = 0;
        for (int c = 0; c < 5; c++) {
            Arrays.sort(keys[c]);
            out[c] = new int[keys[c].length];
            for (int j = 0; j < keys[c].length; j++) {
                out[c][j] = (int) (keys[c][j] & 0xFFFFFFFFL);
            }
            total += out[c].length;
        }
        structure = out[0];
        lights = out[1];
        fluids = out[2];
        fixtures = out[3];
        mechanisms = out[4];
        totalBlocks = total;
        tiles = new ArrayList<TileSpec>(canvas.tiles.values());
        // chunks: the bounds plus one chunk margin so lighting at the edges is correct
        int c1x = (minX >> 4) - 1, c2x = (maxX >> 4) + 1, c1z = (minZ >> 4) - 1, c2z = (maxZ >> 4) + 1;
        int nc = (c2x - c1x + 1) * (c2z - c1z + 1);
        chunkX = new int[nc];
        chunkZ = new int[nc];
        int k = 0;
        for (int x = c1x; x <= c2x; x++) {
            for (int z = c1z; z <= c2z; z++) {
                chunkX[k] = x;
                chunkZ[k] = z;
                k++;
            }
        }
    }

    /** 0 structure, 1 lights, 2 fluids, 3 fixtures, 4 mechanisms. */
    static int category(int b) {
        int id = B.id(b);
        if (id == B.PISTON || id == B.STICKY_PISTON || id == B.PISTON_HEAD) {
            return 4;
        }
        if (B.isFluid(b)) {
            return 2;
        }
        if (B.isAttachable(b)) {
            return 3;
        }
        if (B.isLightSolid(b)) {
            return 1;
        }
        return 0;
    }

    public static Plan generate(int anchorX, int anchorY, int anchorZ, boolean loot) {
        Canvas c = BunkerDesign.generate(loot);
        return new Plan(c, anchorX - Layout.EX, anchorY - Layout.EY, anchorZ - Layout.EZ, loot);
    }

    public int[] blocksOf(Phase p) {
        switch (p) {
            case STRUCTURE: return structure;
            case LIGHTS: return lights;
            case FLUIDS: return fluids;
            case FIXTURES: return fixtures;
            case MECHANISMS: return mechanisms;
            default: return null;
        }
    }

    public int wx(int idx) {
        return canvas.xOf(idx) + dx;
    }

    public int wy(int idx) {
        return canvas.yOf(idx) + dy;
    }

    public int wz(int idx) {
        return canvas.zOf(idx) + dz;
    }

    public int block(int idx) {
        return canvas.getRaw(idx);
    }

    public boolean contains(int wx, int wy, int wz) {
        return wx >= minX && wx <= maxX && wy >= minY && wy <= maxY && wz >= minZ && wz <= maxZ;
    }

    public boolean chunkInBounds(int cx, int cz) {
        return cx >= (minX >> 4) - 1 && cx <= (maxX >> 4) + 1 && cz >= (minZ >> 4) - 1 && cz <= (maxZ >> 4) + 1;
    }

    public String boundsString() {
        return "x " + minX + ".." + maxX + ", y " + minY + ".." + maxY + ", z " + minZ + ".." + maxZ;
    }
}
