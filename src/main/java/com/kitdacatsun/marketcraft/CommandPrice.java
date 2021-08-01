package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CommandPrice implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length != 1){
            return false;
        }

        if (args[0].equals("all")) {
            MarketCraft.updatePrices(true);
        } else {
            Material material = Material.getMaterial(args[0]);
            assert material != null;
            ItemStack item = new ItemStack(material, 1);
            sender.sendMessage(ChatColor.AQUA + "Price of " + item.getI18NDisplayName() + " is: " + MarketCraft.getPrice(item));
            MarketCraft.server.getLogger().info(ChatColor.AQUA + "Price of " + item.getI18NDisplayName() + " is: " + MarketCraft.getPrice(item));
        }
        return true;
    }
}
