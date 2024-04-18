package me.fauxle.customenchants.enchants;

import org.bukkit.Material;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface Enchant {

    String getName();

    boolean isEnchantable(Material type);

    boolean shouldEnchant(EnchantItemEvent event, List<String> lore);

    default void applyDamage(ItemStack item, int damage) {
        if (damage > 0) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable dmg) {
                dmg.setDamage(dmg.getDamage() + damage);
                if (dmg.getDamage() > item.getType().getMaxDurability()) {
                    item.setType(Material.AIR);
                } else {
                    item.setItemMeta(meta);
                }
            }
        }
    }

}
