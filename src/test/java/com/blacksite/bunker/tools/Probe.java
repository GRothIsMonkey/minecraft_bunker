package com.blacksite.bunker.tools;

import com.blacksite.bunker.design.Analyzer;
import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.BunkerDesign;
import com.blacksite.bunker.design.Canvas;

/** Prints block ids in a small region: Probe <out> x1:x2:y1:y2:z1:z2 */
public final class Probe {
    public static void main(String[] args) {
        Canvas c = BunkerDesign.generate(true);
        Analyzer a = new Analyzer(c).runAll();
        String[] p = args[1].split(":");
        int x1 = Integer.parseInt(p[0]), x2 = Integer.parseInt(p[1]), y1 = Integer.parseInt(p[2]),
                y2 = Integer.parseInt(p[3]), z1 = Integer.parseInt(p[4]), z2 = Integer.parseInt(p[5]);
        for (int y = y2; y >= y1; y--) {
            System.out.println("y=" + y + "   (rows z, cols x " + x1 + ".." + x2 + ")");
            for (int z = z1; z <= z2; z++) {
                StringBuilder sb = new StringBuilder(String.format("%4d ", z));
                for (int x = x1; x <= x2; x++) {
                    int b = c.get(x, y, z);
                    String s = b < 0 ? "  ." : String.format("%3d", B.id(b));
                    boolean r = a.reached.get(c.index(x, y, z));
                    sb.append(r ? "*" : " ").append(s);
                }
                System.out.println(sb);
            }
        }
    }
}
