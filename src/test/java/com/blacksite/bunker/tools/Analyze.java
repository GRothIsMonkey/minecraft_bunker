package com.blacksite.bunker.tools;

import com.blacksite.bunker.design.Analyzer;
import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.BunkerDesign;
import com.blacksite.bunker.design.Canvas;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Offline design analysis. */
public final class Analyze {
    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        Canvas c = BunkerDesign.generate(true);
        System.out.println("generated in " + (System.currentTimeMillis() - t) + " ms");
        System.out.println("cells set: " + c.countSet() + ", tiles: " + c.tiles.size() + ", entities: "
                + c.entities.size() + ", rooms: " + c.rooms.size());
        @SuppressWarnings("unchecked")
        List<String> w = (List<String>) c.meta.get("warnings");
        System.out.println("finisher warnings: " + w.size() + " sealed: " + c.meta.get("sealed") + " toppers: "
                + c.meta.get("toppers"));
        for (int i = 0; i < Math.min(20, w.size()); i++) {
            System.out.println("  W " + w.get(i));
        }
        t = System.currentTimeMillis();
        Analyzer a = new Analyzer(c).runAll();
        System.out.println("analysis in " + (System.currentTimeMillis() - t) + " ms");
        System.out.print(a.summary());
        int limit = args.length > 1 ? Integer.parseInt(args[1]) : 40;
        print("UNREACHABLE", a.unreachable, 200);
        print("DOOR", a.doorProblems, 100);
        print("REDSTONE", a.redstoneProblems, 60);
        print("LAVA", a.lavaProblems, 40);
        print("DARK", a.darkSpawns, limit);
        print("SLIME", a.slimeSpawns, limit);
        // chest count
        int chests = 0, bounds = 0;
        Map<Integer, Integer> counts = new TreeMap<Integer, Integer>();
        for (int i = 0; i < c.volume(); i++) {
            int b = c.getRaw(i);
            if (b < 0) {
                continue;
            }
            counts.merge(B.id(b), 1, Integer::sum);
        }
        System.out.println("chests: " + counts.get(B.CHEST) + " trapped: " + counts.get(B.TRAPPED_CHEST) + " furnaces: "
                + counts.get(B.FURNACE) + " hoppers: " + counts.get(B.HOPPER) + " beds: " + counts.get(B.BED)
                + " signs: " + (counts.getOrDefault(B.WALL_SIGN, 0) + counts.getOrDefault(B.SIGN_POST, 0)));
        if (args.length > 2 && args[2].equals("rooms")) {
            for (Canvas.Room r : c.rooms) {
                System.out.println("ROOM " + r.level + " | " + r.name);
            }
        }
    }

    static void print(String tag, List<String> l, int max) {
        for (int i = 0; i < Math.min(max, l.size()); i++) {
            System.out.println(tag + " " + l.get(i));
        }
    }
}
