package com.blacksite.bunker.build;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/** Persistent build progress (plugins/BlacksiteBunker/state.yml). */
public final class BuildState {
    public enum Status { NONE, RUNNING, PAUSED, INTERRUPTED, COMPLETE }

    public Status status = Status.NONE;
    public String phase = Plan.Phase.CHUNKS.name();
    public int cursor = 0;
    public int repairPass = 0;
    public String designVersion = "";
    public String world = "";
    public int anchorX, anchorY, anchorZ;
    public boolean loot = true;
    public long placed, skipped, repaired, tiles, entities, tileErrors, entityErrors;
    public long startedAt, updatedAt, finishedAt;
    public String lastVerify = "";
    /** True when the last server session ended with a normal shutdown (worlds were saved after the build). */
    public boolean cleanStop = true;
    /**
     * Set on load when an unfinished build was cut off by a crash or kill: chunks written since the last world
     * save are gone, so work before the saved cursor can no longer be trusted.
     */
    public boolean uncleanRestart = false;

    private final File file;

    public BuildState(File file) {
        this.file = file;
    }

    public void load() {
        if (!file.exists()) {
            return;
        }
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        try {
            status = Status.valueOf(y.getString("status", "NONE"));
        } catch (IllegalArgumentException e) {
            status = Status.NONE;
        }
        phase = y.getString("phase", Plan.Phase.CHUNKS.name());
        cursor = y.getInt("cursor", 0);
        repairPass = y.getInt("repair-pass", 0);
        designVersion = y.getString("design-version", "");
        world = y.getString("world", "");
        anchorX = y.getInt("anchor.x");
        anchorY = y.getInt("anchor.y");
        anchorZ = y.getInt("anchor.z");
        loot = y.getBoolean("loot", true);
        placed = y.getLong("stats.placed");
        skipped = y.getLong("stats.skipped");
        repaired = y.getLong("stats.repaired");
        tiles = y.getLong("stats.tiles");
        entities = y.getLong("stats.entities");
        tileErrors = y.getLong("stats.tile-errors");
        entityErrors = y.getLong("stats.entity-errors");
        startedAt = y.getLong("started-at");
        updatedAt = y.getLong("updated-at");
        finishedAt = y.getLong("finished-at");
        lastVerify = y.getString("last-verify", "");
        cleanStop = y.getBoolean("clean-stop", true);
        uncleanRestart = !cleanStop && status != Status.COMPLETE && status != Status.NONE;
        if (status == Status.RUNNING) {
            // the server stopped while building
            status = Status.INTERRUPTED;
        }
    }

    public void save() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("status", status.name());
        y.set("phase", phase);
        y.set("cursor", cursor);
        y.set("repair-pass", repairPass);
        y.set("design-version", designVersion);
        y.set("world", world);
        y.set("anchor.x", anchorX);
        y.set("anchor.y", anchorY);
        y.set("anchor.z", anchorZ);
        y.set("loot", loot);
        y.set("stats.placed", placed);
        y.set("stats.skipped", skipped);
        y.set("stats.repaired", repaired);
        y.set("stats.tiles", tiles);
        y.set("stats.entities", entities);
        y.set("stats.tile-errors", tileErrors);
        y.set("stats.entity-errors", entityErrors);
        y.set("started-at", startedAt);
        y.set("updated-at", System.currentTimeMillis());
        y.set("finished-at", finishedAt);
        y.set("last-verify", lastVerify);
        y.set("clean-stop", cleanStop);
        try {
            file.getParentFile().mkdirs();
            y.save(file);
        } catch (IOException e) {
            // reported by caller logs; progress is still in memory
        }
    }
}
