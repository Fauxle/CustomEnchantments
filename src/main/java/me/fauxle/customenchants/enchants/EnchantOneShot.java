package me.fauxle.customenchants.enchants;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantOneShot implements Enchant {

    @Override
    public String getName() {
        return "One-Shot";
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
                (ThreadLocalRandom.current().nextInt(99) == 0)
                && isEnchantable(event.getItem().getType()); // 1% chance
    }

    public void execute(EntityDamageByEntityEvent event, LivingEntity attacked, Player attacker, ItemStack itemUsed) {

        event.setDamage(100000000D); // Double.MAX_VALUE was too much lol

        Location lightningTarget = attacked.getLocation();
        World world = lightningTarget.getWorld();
        world.spigot().strikeLightningEffect(lightningTarget, true);

        for (Player nearby : lightningTarget.getNearbyPlayers(50)) {
            nearby.playSound(lightningTarget, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1F, 1F);
            nearby.playSound(lightningTarget, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1F, 1F);
        }

        world.spawnParticle(Particle.FLAME, lightningTarget, 20);
        world.spawnParticle(Particle.ELECTRIC_SPARK, lightningTarget, 10);

        applyDamage(itemUsed, (int) (attacked.getHealth() * 3));
        if (itemUsed.getType() == Material.AIR) {
            attacker.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            attacker.playSound(attacker.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }

    }

}
