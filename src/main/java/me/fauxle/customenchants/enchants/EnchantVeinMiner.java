package me.fauxle.customenchants.enchants;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantVeinMiner implements Enchant {

    private static final BlockFace[] CHECK_FACES = new BlockFace[]{
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH_EAST
    };
    private final List<Block> visited = new ArrayList<>();
    private boolean isExecuting = false;

    @Override
    public String getName() {
        return "VeinMiner";
    }

    @Override
    public boolean isEnchantable(Material type) {
        return type == Material.DIAMOND_PICKAXE
                || type == Material.IRON_PICKAXE
                || type == Material.GOLDEN_PICKAXE
                || type == Material.NETHERITE_PICKAXE
                || type == Material.STONE_PICKAXE
                || type == Material.WOODEN_PICKAXE;
    }

    @Override
    public boolean shouldEnchant(EnchantItemEvent event, List<String> lore) {
        return event.getExpLevelCost() >= 30 &&
                (ThreadLocalRandom.current().nextInt(9) == 0)
                && isEnchantable(event.getItem().getType()); // 10% chance
    }

    public void execute(Block start, Player player, ItemStack itemUsed) {
        if (isExecuting) // Condition here because we're calling BlockBreakEvent (no infinite recursions)
            return;

        isExecuting = true;

        visited.add(start);
        searchBlocks(start);
        visited.remove(start);
        int extraDamage = 0;
        for (Block b : visited) {
            BlockBreakEvent cevent = new BlockBreakEvent(b, player);
            Bukkit.getPluginManager().callEvent(cevent);
            if (!cevent.isCancelled()) {
                extraDamage++;
                if (cevent.isDropItems()) {
                    cevent.getBlock().breakNaturally(itemUsed);
                } else {
                    cevent.getBlock().setType(Material.AIR);
                }
            }
        }
        visited.clear();
        applyDamage(itemUsed, extraDamage);
        if (itemUsed.getType() == Material.AIR) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }

        isExecuting = false;
    }

    private void searchBlocks(Block block) {
        for (BlockFace face : CHECK_FACES) {
            Block relative = block.getRelative(face);
            if (relative.getType() == block.getType()) {
                if (!visited.contains(relative)) {
                    visited.add(relative);
                    searchBlocks(relative);
                }
            }
        }
    }

}
