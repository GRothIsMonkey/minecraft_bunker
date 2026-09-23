package com.blacksite.bunker.design;

/** Six world directions. North = -Z, South = +Z, West = -X, East = +X. */
public enum Dir {
    NORTH(0, 0, -1), SOUTH(0, 0, 1), WEST(-1, 0, 0), EAST(1, 0, 0), UP(0, 1, 0), DOWN(0, -1, 0);

    public final int dx, dy, dz;

    Dir(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public Dir opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            case EAST: return WEST;
            case UP: return DOWN;
            default: return UP;
        }
    }

    /** Rotate 90 degrees clockwise when seen from above (N -> E -> S -> W). */
    public Dir cw() {
        switch (this) {
            case NORTH: return EAST;
            case EAST: return SOUTH;
            case SOUTH: return WEST;
            case WEST: return NORTH;
            default: return this;
        }
    }

    public Dir ccw() {
        return cw().cw().cw();
    }

    public boolean isHorizontal() {
        return dy == 0;
    }

    public boolean alongX() {
        return dx != 0;
    }

    public static final Dir[] HORIZONTAL = {NORTH, EAST, SOUTH, WEST};
}
