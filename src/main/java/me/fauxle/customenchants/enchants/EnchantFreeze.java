package me.fauxle.customenchants.enchants;

import me.fauxle.customenchants.EnchantmentPlugin;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantFreeze implements Enchant {

    @Override
    public String getName() {
        return "Freeze";
    }

    @Override
    public boolean isEnchantable(Material type) {
        return type == Material.WOODEN_SWORD
                || type == Material.GOLDEN_SWORD
                || type == Material.DIAMOND_SWORD
                || type == Material.NETHERITE_SWORD
                || type == Material.IRON_SWORD
                || type == Material.STONE_SWORD;
    }

    @Override
    public boolean shouldEnchant(EnchantItemEvent event, List<String> lore) {
        return event.getExpLevelCost() >= 30 &&
                (ThreadLocalRandom.current().nextInt(6) == 0) // 16%-ish chance
                && isEnchantable(event.getItem().getType());
    }

    public void execute(LivingEntity victim) {

        if (!victim.hasAI())
            return;

        if (ThreadLocalRandom.current().nextInt(3) == 0) {

            victim.setAI(false);

            new BukkitRunnable() {
                int iterations = 0;

                @Override
                public void run() {
                    if (!victim.isValid()) {
                        this.cancel();
                        return;
                    }
                    if (++iterations > 10) {
                        victim.setAI(true);
                        this.cancel();
                        return;
                    }
                    World world = victim.getWorld();
                    world.spawnParticle(Particle.ENCHANTMENT_TABLE, victim.getEyeLocation(), 10);
                }
            }.runTaskTimer(EnchantmentPlugin.getInstance(), 0, 10);

        }

    }

}
