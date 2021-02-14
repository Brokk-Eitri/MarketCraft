package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class CommandPay implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender , @NotNull Command cmd, @NotNull String label, String[] args) {

        if (args.length != 2) {
            return false;
        }

        Player player = MarketCraft.server.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED  + "Player not found");
            return true;
        }

        UUID Uuid = player.getUniqueId();

        int amount = Integer.parseInt(args[1]);
        player.sendMessage(ChatColor.GOLD + "You have been payed £" + amount + " by " + sender.getName());

        String playerBalanceKey = "Players." + Uuid.toString() + ".balance";

        if (MarketCraft.playerBalances.contains(playerBalanceKey)) {
            int balance = (int) MarketCraft.playerBalances.get("Players." + Uuid.toString() + ".balance") + amount;
            sender.sendMessage(String.valueOf(balance));
            MarketCraft.playerBalances.set(playerBalanceKey, balance);
        } else {
            MarketCraft.playerBalances.set(playerBalanceKey, amount);
            MarketCraft.playerBalances.options().copyDefaults(true);
        }

        return true;
    }


}
