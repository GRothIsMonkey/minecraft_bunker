package com.blacksite.bunker.design;

/**
 * Master coordinate plan of SITE-7 for the default anchor (-291, 68, 141). The whole design is written in these
 * absolute coordinates; a configurable anchor simply translates the finished plan.
 *
 * <pre>
 * Facing: the main gate faces EAST (+X). Players approach from the east along the service road and walk WEST
 * through the round blast door into the facility. North = -Z, South = +Z.
 *
 * Vertical plan (floor block y; rooms have air floor+1..floor+6 and a ceiling at floor+7):
 *   SURFACE  67   courtyard, facade, blast door, checkpoint Alpha, loading bay
 *   LEVEL 1  55   Security & Command
 *   LEVEL 2  45   Operations & Living
 *   LEVEL 3  35   Science & Medical
 *   LEVEL 4  25   Storage & Engineering (hangar + storage hall are 13 blocks tall)
 *   LEVEL 5  15   Reactor & Power
 *   LEVEL 6   5   Containment & Restricted (+ hidden core)
 * </pre>
 */
public final class Layout {
    private Layout() {
    }

    public static final int EX = -291, EY = 68, EZ = 141;

    public static final int SURF = 67;
    public static final int L1 = 55, L2 = 45, L3 = 35, L4 = 25, L5 = 15, L6 = 5;
    public static final int[] LEVELS = {L1, L2, L3, L4, L5, L6};
    public static final String[] LEVEL_NAMES = {"Security & Command", "Operations & Living", "Science & Medical",
            "Storage & Engineering", "Reactor & Power", "Containment"};

    /** Canvas bounds (inclusive). */
    public static final int MIN_X = -440, MAX_X = -216, MIN_Y = 4, MAX_Y = 127, MIN_Z = 72, MAX_Z = 210;

    /** Main spine corridor interior z range. */
    public static final int SPINE_Z1 = 138, SPINE_Z2 = 144;

    /** Stairwell box. */
    public static final int SW_X1 = -376, SW_X2 = -366, SW_Z1 = 145, SW_Z2 = 153;
    /** Personnel lift box. */
    public static final int PL_X1 = -381, PL_X2 = -377, PL_Z1 = 145, PL_Z2 = 149;
    /** Freight lift box (hangar L4 to surface). */
    public static final int FL_X1 = -311, FL_X2 = -301, FL_Z1 = 155, FL_Z2 = 165;

    /** Beacon conduit column ("the Spine of Light"). */
    public static final int CX = -330, CZ = 108;

    /** Reactor chamber box. */
    public static final int RC_X1 = -345, RC_X2 = -315, RC_Z1 = 93, RC_Z2 = 123, RC_Y1 = 5, RC_Y2 = 43;

    /** Hangar and storage hall (L4, tall). */
    public static final int HG_X1 = -341, HG_X2 = -297, HG_Z1 = 145, HG_Z2 = 181;
    public static final int ST_X1 = -421, ST_X2 = -383, ST_Z1 = 145, ST_Z2 = 185;

    /** Escape rail: tunnel on L6 along z, then incline to the surface pump house. */
    public static final int ESC_Z = 88;

    public static int level(int i) {
        return LEVELS[i - 1];
    }
}
