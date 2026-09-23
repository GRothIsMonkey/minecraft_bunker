package com.blacksite.bunker.design;

import static com.blacksite.bunker.design.Layout.*;

/**
 * The beacon conduit ("Spine of Light"): a tier-2 iron beacon at the bottom of the reactor pit whose beam rises
 * through the reactor core, the Operations Hall (L2), the Command Center holo-table (L1), the berm and out of the
 * north exhaust stack. The column is locked so no other design element can block the beam.
 */
public final class Conduit {
    private Conduit() {
    }

    public static final int BEACON_Y = 7;

    public static void reserve(Kit k) {
        Canvas c = k.c;
        // pyramid (tier 2): 5x5 at y5, 3x3 at y6
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                c.setLocked(CX + dx, BEACON_Y - 2, CZ + dz, B.IRON);
                if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) {
                    c.setLocked(CX + dx, BEACON_Y - 1, CZ + dz, B.IRON);
                }
            }
        }
        c.setLocked(CX, BEACON_Y, CZ, B.of(B.BEACON));
        int stackBase = Surface.bermTop(CX, CZ) - 2;
        for (int y = BEACON_Y + 1; y <= MAX_Y; y++) {
            int b;
            if (y == BEACON_Y + 1) {
                b = B.sglass(B.LIGHT_BLUE); // tints the beam light blue
            } else if (y < stackBase) {
                b = B.GLS;
            } else {
                b = B.A;
            }
            c.setLocked(CX, y, CZ, b);
        }
        c.marker("beacon", CX, BEACON_Y, CZ, "tier-2 iron beacon");
    }

    public static void finish(Kit k) {
        // nothing to do: the column is locked. Kept as an explicit step for clarity.
    }
}
