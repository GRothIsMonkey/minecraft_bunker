package com.blacksite.bunker.design;

/** Generates the complete, deterministic SITE-7 design into a canvas. Pure Java: no Bukkit dependency. */
public final class BunkerDesign {
    private BunkerDesign() {
    }

    public static final String DESIGN_VERSION = "site7-1.0.0";

    public static Canvas generate(boolean loot) {
        Loot.enabled = loot;
        Canvas c = new Canvas(Layout.MIN_X, Layout.MIN_Y, Layout.MIN_Z, Layout.MAX_X, Layout.MAX_Y, Layout.MAX_Z);
        Kit k = new Kit(c);
        Conduit.reserve(k);
        new Surface(k).build();
        new Entrance(k).build();
        new Level1(k).build();
        new Level2(k).build();
        new Level3(k).build();
        new Level4(k).build();
        new Level5(k).build();
        new Level6(k).build();
        new Reactor(k).build();
        new Cores(k).build();
        Conduit.finish(k);
        Finisher.finish(k);
        return c;
    }
}
