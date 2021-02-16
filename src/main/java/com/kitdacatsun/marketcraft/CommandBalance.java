package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandBalance implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            UUID Uuid = player.getUniqueId();
            String playerBalanceKey = "Players." + Uuid.toString() + ".balance";

            int balance = (int) MarketCraft.playerBalances.get(playerBalanceKey);

            sender.sendMessage(ChatColor.GOLD + "Your balance is £" + balance);

            return true;
        } else {
            return false;
        }
    }
}