package com.brokkandeitri.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CommandCount implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1){
            player.sendMessage(ChatColor.GOLD + "Count of " + player.getItemInHand().getI18NDisplayName() + " is: " + MarketCraft.getCount(player.getItemInHand()));
            return true;
        }

        Material material = Material.getMaterial(args[0]);
        assert material != null;
        ItemStack item = new ItemStack(material, 1);
        player.sendMessage(ChatColor.GOLD + "Count of " + item.getI18NDisplayName() + " is: " + MarketCraft.getCount(item));

        return true;
    }
}
