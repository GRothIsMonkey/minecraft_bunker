package com.blacksite.bunker.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dense voxel canvas holding the complete final design. Every design operation writes into the canvas
 * ("last write wins" unless a cell is locked). The build engine later places exactly the final state, which
 * makes the build deterministic, idempotent and verifiable.
 */
public final class Canvas {
    private static final int SET = 1 << 16;
    private static final int LOCK = 1 << 17;

    public final int minX, minY, minZ, maxX, maxY, maxZ;
    public final int sx, sy, sz;
    private final int[] cells;

    public final Map<Long, TileSpec> tiles = new LinkedHashMap<Long, TileSpec>();
    public final List<EntitySpec> entities = new ArrayList<EntitySpec>();
    public final List<Room> rooms = new ArrayList<Room>();
    public final List<Marker> markers = new ArrayList<Marker>();
    /** pairs of lift sign positions that the plugin links (for navigation analysis) */
    public final List<int[]> liftLinks = new ArrayList<int[]>();
    public final Map<String, Object> meta = new HashMap<String, Object>();

    public Canvas(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.sx = maxX - minX + 1;
        this.sy = maxY - minY + 1;
        this.sz = maxZ - minZ + 1;
        this.cells = new int[sx * sy * sz];
    }

    public boolean inBounds(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public int index(int x, int y, int z) {
        return ((y - minY) * sz + (z - minZ)) * sx + (x - minX);
    }

    public int volume() {
        return cells.length;
    }

    public int xOf(int idx) {
        return idx % sx + minX;
    }

    public int zOf(int idx) {
        return (idx / sx) % sz + minZ;
    }

    public int yOf(int idx) {
        return idx / (sx * sz) + minY;
    }

    public void set(int x, int y, int z, int b) {
        if (!inBounds(x, y, z)) {
            throw new IllegalArgumentException("Out of canvas: " + x + "," + y + "," + z);
        }
        int i = index(x, y, z);
        int c = cells[i];
        if ((c & LOCK) != 0) {
            return;
        }
        cells[i] = (b & 0xFFFF) | SET;
    }

    public void setLocked(int x, int y, int z, int b) {
        int i = index(x, y, z);
        cells[i] = (b & 0xFFFF) | SET | LOCK;
    }

    public void lock(int x, int y, int z) {
        int i = index(x, y, z);
        cells[i] |= LOCK;
    }

    public void unlockAll() {
        for (int i = 0; i < cells.length; i++) {
            cells[i] &= ~LOCK;
        }
    }

    public void setIfUnset(int x, int y, int z, int b) {
        if (!isSet(x, y, z)) {
            set(x, y, z, b);
        }
    }

    public void clear(int x, int y, int z) {
        int i = index(x, y, z);
        if ((cells[i] & LOCK) == 0) {
            cells[i] = 0;
        }
    }

    public boolean isSet(int x, int y, int z) {
        return inBounds(x, y, z) && (cells[index(x, y, z)] & SET) != 0;
    }

    public boolean isLocked(int x, int y, int z) {
        return inBounds(x, y, z) && (cells[index(x, y, z)] & LOCK) != 0;
    }

    /** Block at position, or -1 when the cell is not part of the design. */
    public int get(int x, int y, int z) {
        if (!inBounds(x, y, z)) {
            return -1;
        }
        int c = cells[index(x, y, z)];
        return (c & SET) != 0 ? (c & 0xFFFF) : -1;
    }

    public int getRaw(int idx) {
        int c = cells[idx];
        return (c & SET) != 0 ? (c & 0xFFFF) : -1;
    }

    public int countSet() {
        int n = 0;
        for (int c : cells) {
            if ((c & SET) != 0) {
                n++;
            }
        }
        return n;
    }

    public static long key(int x, int y, int z) {
        return ((long) (x + 30000000) << 38) | ((long) (z + 30000000) << 12) | (long) (y & 0xFFF);
    }

    public void tile(TileSpec t) {
        tiles.put(key(t.x, t.y, t.z), t);
    }

    public TileSpec tileAt(int x, int y, int z) {
        return tiles.get(key(x, y, z));
    }

    /** Deterministic pseudo random in [0, 1) for a position and salt. */
    public static double rand(int x, int y, int z, int salt) {
        long h = x * 73856093L ^ y * 19349663L ^ z * 83492791L ^ salt * 2654435761L;
        h ^= (h >>> 33);
        h *= 0xff51afd7ed558ccdL;
        h ^= (h >>> 33);
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= (h >>> 33);
        return (h >>> 11) * 0x1.0p-53;
    }

    /** A named room/area of the facility, used for documentation and verification. */
    public static final class Room {
        public final String name;
        public final String level;
        public final int x1, y1, z1, x2, y2, z2;
        /** a floor-level point inside the room a player must be able to reach (feet position) */
        public final int nx, ny, nz;
        public final boolean secret;

        public Room(String name, String level, int x1, int y1, int z1, int x2, int y2, int z2, int nx, int ny, int nz,
                    boolean secret) {
            this.name = name;
            this.level = level;
            this.x1 = Math.min(x1, x2);
            this.y1 = Math.min(y1, y2);
            this.z1 = Math.min(z1, z2);
            this.x2 = Math.max(x1, x2);
            this.y2 = Math.max(y1, y2);
            this.z2 = Math.max(z1, z2);
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
            this.secret = secret;
        }
    }

    /** Point of interest (doors that need activators, lift signs, beacon, ...). */
    public static final class Marker {
        public final String type;
        public final int x, y, z;
        public final String note;

        public Marker(String type, int x, int y, int z, String note) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
            this.note = note;
        }
    }

    public void room(String name, String level, int x1, int y1, int z1, int x2, int y2, int z2, int nx, int ny, int nz) {
        rooms.add(new Room(name, level, x1, y1, z1, x2, y2, z2, nx, ny, nz, false));
    }

    public void secretRoom(String name, String level, int x1, int y1, int z1, int x2, int y2, int z2, int nx, int ny,
                           int nz) {
        rooms.add(new Room(name, level, x1, y1, z1, x2, y2, z2, nx, ny, nz, true));
    }

    public void marker(String type, int x, int y, int z, String note) {
        markers.add(new Marker(type, x, y, z, note));
    }
}
