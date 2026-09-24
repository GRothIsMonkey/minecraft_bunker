package com.blacksite.bunker.build;

import com.blacksite.bunker.design.ItemSpec;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/** Converts plain {@link ItemSpec}s into Bukkit 1.8 item stacks. */
public final class Items {
    private Items() {
    }

    @SuppressWarnings("deprecation")
    public static ItemStack stack(ItemSpec s) {
        if (s == null) {
            return null;
        }
        Material m = Material.getMaterial(s.id);
        if (m == null) {
            return null;
        }
        ItemStack st = new ItemStack(m, Math.max(1, s.amount), (short) s.damage);
        ItemMeta meta = st.getItemMeta();
        if (meta == null) {
            return st;
        }
        if (s.name != null) {
            meta.setDisplayName(s.name);
        }
        if (s.lore != null) {
            meta.setLore(s.lore);
        }
        if (s.enchants != null) {
            for (int i = 0; i + 1 < s.enchants.length; i += 2) {
                Enchantment e = Enchantment.getById(s.enchants[i]);
                if (e != null) {
                    meta.addEnchant(e, s.enchants[i + 1], true);
                }
            }
        }
        if (s.leatherColor >= 0 && meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(s.leatherColor & 0xFFFFFF));
        }
        if (s.bookTitle != null && meta instanceof BookMeta) {
            BookMeta bm = (BookMeta) meta;
            bm.setTitle(s.bookTitle);
            bm.setAuthor(s.bookAuthor);
            bm.setPages(s.bookPages);
        }
        st.setItemMeta(meta);
        return st;
    }
}
