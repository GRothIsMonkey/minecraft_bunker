package com.blacksite.bunker.tools;

import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.BunkerDesign;
import com.blacksite.bunker.design.Canvas;

public final class RoomStats {
    public static void main(String[] args) {
        Canvas c = BunkerDesign.generate(true);
        int total = 0;
        for (Canvas.Room r : c.rooms) {
            int ch = 0;
            for (int x = r.x1; x <= r.x2; x++) {
                for (int y = r.y1; y <= r.y2; y++) {
                    for (int z = r.z1; z <= r.z2; z++) {
                        int b = c.get(x, y, z);
                        if (b >= 0 && B.id(b) == B.CHEST) {
                            ch++;
                        }
                    }
                }
            }
            total += ch;
            if (ch >= 10) {
                System.out.println(ch + "\t" + r.level + " | " + r.name);
            }
        }
        System.out.println("sum(room boxes)=" + total);
    }
}
