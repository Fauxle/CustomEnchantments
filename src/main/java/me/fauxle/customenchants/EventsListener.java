package me.fauxle.customenchants;

import me.fauxle.customenchants.enchants.EnchantFreeze;
import me.fauxle.customenchants.enchants.EnchantMachineGun;
import me.fauxle.customenchants.enchants.EnchantOneShot;
import me.fauxle.customenchants.enchants.EnchantVeinMiner;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EventsListener implements Listener {

    private final EnchantMachineGun enchantMachineGun;
    private final EnchantVeinMiner enchantVeinMiner;
    private final EnchantOneShot enchantOneShot;
    private final EnchantFreeze enchantFreeze;

    public EventsListener() {
        this.enchantMachineGun = new EnchantMachineGun();
        this.enchantVeinMiner = new EnchantVeinMiner();
        this.enchantOneShot = new EnchantOneShot();
        this.enchantFreeze = new EnchantFreeze();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        switch (event.getBlock().getType()) {
            case COAL_ORE, IRON_ORE, GOLD_ORE, COPPER_ORE, DIAMOND_ORE, REDSTONE_ORE, LAPIS_ORE, EMERALD_ORE,
                    NETHER_GOLD_ORE, NETHER_QUARTZ_ORE, DEEPSLATE_COAL_ORE, DEEPSLATE_COPPER_ORE, DEEPSLATE_DIAMOND_ORE,
                    DEEPSLATE_EMERALD_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_IRON_ORE, DEEPSLATE_LAPIS_ORE,
                    DEEPSLATE_REDSTONE_ORE -> {
                ItemStack itemUsed = event.getPlayer().getInventory().getItemInMainHand();
                if (enchantVeinMiner.isEnchantable(itemUsed.getType())) {
                    if (itemUsed.hasItemMeta()) {
                        ItemMeta meta = itemUsed.getItemMeta();
                        if (meta.hasLore()) {
                            List<String> lore = meta.getLore();
                            assert lore != null;
                            if (lore.contains(EnchantmentPlugin.ENCHANTMENT_LORE_PREFIX + enchantVeinMiner.getName())) {
                                enchantVeinMiner.execute(event.getBlock(), event.getPlayer(), itemUsed);
                            }
                        }
                    }
                }
            }
            default -> {
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.STICK) {
                if (event.getItem().hasItemMeta()) {
                    ItemMeta meta = event.getItem().getItemMeta();
                    if (meta.hasLore()) {
                        List<String> lore = meta.getLore();
                        assert lore != null;
                        if (lore.contains(EnchantmentPlugin.ENCHANTMENT_LORE_PREFIX + enchantMachineGun.getName())) {
                            enchantMachineGun.execute(event.getPlayer());
                            event.setUseInteractedBlock(Event.Result.DENY);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityAttacked(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (event.getEntity() instanceof LivingEntity victim && event.getDamager() instanceof Player attacker) {
                ItemStack itemUsed = attacker.getInventory().getItemInMainHand();
                List<String> lore = itemUsed.getLore();
                if (lore == null)
                    return;

                if (lore.contains(EnchantmentPlugin.ENCHANTMENT_LORE_PREFIX + enchantOneShot.getName())) {
                    enchantOneShot.execute(event, victim, attacker, itemUsed);
                } else if (lore.contains(EnchantmentPlugin.ENCHANTMENT_LORE_PREFIX + enchantFreeze.getName())) {
                    enchantFreeze.execute(victim);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        enchantMachineGun.onProjectileHit(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {

        ItemStack enchantedItem = event.getItem();
        ItemMeta meta = enchantedItem.getItemMeta();

        if (meta == null || meta.hasLore())
            return;

        List<String> lore = meta.getLore();
        if (lore == null)
            lore = new ArrayList<>();

        if (enchantMachineGun.shouldEnchant(event, lore)) {
            lore.add(EnchantmentPlugin.ENCHANTMENT_LORE_PREFIX + enchantMachineGun.getName());
        }
        if (enchantVeinMiner.shouldEnchant(event, lore)) {
            lore.add(EnchantmentPlugin.ENCHANTMENT_LORE_PREFIX + enchantVeinMiner.getName());
        }
        if (enchantFreeze.shouldEnchant(event, lore)) {
            lore.add(EnchantmentPlugin.ENCHANTMENT_LORE_PREFIX + enchantFreeze.getName());
        }

        meta.setLore(lore);
        enchantedItem.setItemMeta(meta);

    }

}
