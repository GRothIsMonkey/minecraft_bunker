package com.blacksite.bunker.design;

import java.util.ArrayList;
import java.util.List;

/** Plain-data description of an item stack (turned into a Bukkit ItemStack by the plugin). */
public final class ItemSpec {
    public final int id;
    public final int amount;
    public final int damage;
    public String name;
    public List<String> lore;
    /** enchantment id -> level pairs (1.8 numeric enchant ids). */
    public int[] enchants;
    /** leather armour colour (RGB) or -1. */
    public int leatherColor = -1;
    /** written book */
    public String bookTitle, bookAuthor;
    public List<String> bookPages;
    /** potion (damage value encodes the potion in 1.8) */

    public ItemSpec(int id, int amount, int damage) {
        this.id = id;
        this.amount = amount;
        this.damage = damage;
    }

    public static ItemSpec of(int id, int amount) {
        return new ItemSpec(id, amount, 0);
    }

    public static ItemSpec of(int id, int amount, int damage) {
        return new ItemSpec(id, amount, damage);
    }

    public ItemSpec named(String n) {
        this.name = n;
        return this;
    }

    public ItemSpec lore(String... lines) {
        this.lore = new ArrayList<String>();
        for (String l : lines) {
            this.lore.add(l);
        }
        return this;
    }

    public ItemSpec ench(int... pairs) {
        this.enchants = pairs;
        return this;
    }

    public ItemSpec leather(int rgb) {
        this.leatherColor = rgb;
        return this;
    }

    public static ItemSpec book(String title, String author, String... pages) {
        ItemSpec s = new ItemSpec(387, 1, 0);
        s.bookTitle = title;
        s.bookAuthor = author;
        s.bookPages = new ArrayList<String>();
        for (String p : pages) {
            s.bookPages.add(p);
        }
        return s;
    }

    public String describe() {
        return id + ":" + damage + "x" + amount + (name != null ? "(" + name + ")" : "")
                + (bookTitle != null ? "[book " + bookTitle + "]" : "");
    }
}
