package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class CommandPrice implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length != 1){
            return false;
        }

        if (args[0].equals("all")) {
            Level startLevel = MarketCraft.server.getLogger().getLevel();
            MarketCraft.server.getLogger().setLevel(Level.WARNING);
            MarketCraft.updatePrices();
            MarketCraft.server.getLogger().setLevel(startLevel);
        } else {
            Material material = Material.getMaterial(args[0]);
            assert material != null;
            ItemStack item = new ItemStack(material, 1);
            sender.sendMessage("Price of " + item.getI18NDisplayName() + " is: " + MarketCraft.getPrice(item));
        }

        return true;
    }
}
