package com.kitdacatsun.marketcraft;

import com.kitdacatsun.marketcraft.MarketCraft.files;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class CommandPay implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            pay(sender, args);
            return true;
        }
        Player player = (Player) sender;

        if (player.isOp() && player.getGameMode().toString().equals("CREATIVE")) {
            pay(sender, args);

        } else {

            if (args.length != 2) {
                return false;
            }

            Player receiver = MarketCraft.server.getPlayer(args[0]);
            if (receiver == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return true;
            }

            int amount = Integer.parseInt(args[1]);

            UUID Uuid = player.getUniqueId();
            String playerBalanceKey = "players." + Uuid.toString() + ".balance";

            if (!files.balance.contains(playerBalanceKey)) {
                files.balance.set(playerBalanceKey, 0);
            }

            if ((int) files.balance.get(playerBalanceKey) >= amount){

                int balance = (int) files.balance.get(playerBalanceKey) - amount;
                files.balance.set(playerBalanceKey, balance);

                receiver.sendMessage(ChatColor.GOLD + "You have been payed £" + amount + " by " + sender.getName());
                player.sendMessage(ChatColor.GOLD + "You have payed " + receiver.getDisplayName() + " £" + amount);

                Uuid = receiver.getUniqueId();
                playerBalanceKey = "players." + Uuid.toString() + ".balance";

                if (files.balance.contains(playerBalanceKey)) {
                    balance = (int) files.balance.get(playerBalanceKey) + amount;
                    files.balance.set(playerBalanceKey, balance);
                } else {
                    files.balance.set(playerBalanceKey, amount);
                    files.balance.options().copyDefaults(true);
                }
            } else {
                player.sendMessage(ChatColor.RED + "You don't have enough money to pay the specified player");
            }

        }

        return true;
    }

    public void pay(@NotNull CommandSender sender, String[] args){
        if (args.length != 2) {
            return;
        }

        Player receiver = MarketCraft.server.getPlayer(args[0]);
        if (receiver == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        UUID Uuid = receiver.getUniqueId();

        int amount = Integer.parseInt(args[1]);
        receiver.sendMessage(ChatColor.GOLD + "You have been payed £" + amount + " by " + sender.getName());

        String playerBalanceKey = "players." + Uuid.toString() + ".balance";

        if (files.balance.contains(playerBalanceKey)) {
            int balance = (int) files.balance.get(playerBalanceKey) + amount;
            files.balance.set(playerBalanceKey, balance);
        } else {
            files.balance.set(playerBalanceKey, amount);
            files.balance.options().copyDefaults(true);
        }

    }
}
