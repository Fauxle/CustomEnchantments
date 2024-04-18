package me.fauxle.customenchants;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class EnchantmentPlugin extends JavaPlugin {

    public static final String ENCHANTMENT_LORE_PREFIX = (ChatColor.LIGHT_PURPLE.toString());

    public static EnchantmentPlugin getInstance() {
        return getPlugin(EnchantmentPlugin.class);
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new EventsListener(), this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {

            if (!player.isOp())
                return true;

            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
                return true;
            }
            ItemMeta meta = hand.getItemMeta();

            if ("elore".equals(command.getName())) {

                List<String> lore = Arrays.asList(args);

                for (int i = 0; i < lore.size(); i++)
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i).replace('_', ' ')));

                meta.setLore(lore);
                hand.setItemMeta(meta);

                player.getInventory().setItemInMainHand(hand);
                player.sendMessage(ChatColor.GREEN + "The lore of this item has been changed successfully!");

            } else {

                String name = String.join(" ", args).trim();

                name = ChatColor.translateAlternateColorCodes('&', name);

                meta.setDisplayName(name);
                hand.setItemMeta(meta);
                player.getInventory().setItemInMainHand(hand);
                player.sendMessage(ChatColor.GREEN + "The name of this item has been changed successfully!");

            }

        }

        return true;
    }

}
