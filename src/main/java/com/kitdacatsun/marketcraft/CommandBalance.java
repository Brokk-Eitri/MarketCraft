package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandBalance implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender , @NotNull Command cmd, @NotNull String label, String[] args){
        if (sender instanceof Player) {
            String name = ((Player) sender).getUniqueId().toString();
            String balance = (String) MarketCraft.playerBalances.get("Players." + name + ".balance");
            sender.sendMessage(ChatColor.GOLD + "Your balance is £" + balance);

            return true;
        } else {
            return false;
        }
    }
}