package com.kitdacatsun.marketcraft;

import com.kitdacatsun.marketcraft.MarketCraft.files;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class CommandPay implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        Player receiver = Objects.requireNonNull(MarketCraft.server.getPlayer(args[0]));
        int amount = Integer.parseInt(args[1]);

        pay(sender, receiver, amount);

        return true;
    }

    public static void pay(CommandSender sender, Player receiver, int amount) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            int balance = 999;

            if (player.getGameMode() != GameMode.CREATIVE) {
                String senderKey = "players." + player.getUniqueId().toString() + ".balance";
                balance = files.balance.getInt(senderKey);

                if (balance - amount < 0) {
                    player.sendMessage(ChatColor.GOLD + "You do not have enough money to do that (balance: £" + balance + ")");
                    return;
                }

                files.balance.set(senderKey, balance - amount);
            }

            sender.sendMessage(ChatColor.GOLD + "You sent £" + amount + " to " + receiver.getName());
            receiver.sendMessage(ChatColor.GOLD + "New balance: £" + balance);
        }

        String receiverKey = "players." + receiver.getUniqueId().toString() + ".balance";
        int balance = files.balance.getInt(receiverKey);
        files.balance.set(receiverKey, balance + amount);

        receiver.sendMessage(ChatColor.GOLD + "You have been payed £" + amount + " by " + sender.getName());
        receiver.sendMessage(ChatColor.GOLD + "New balance: £" + balance);
    }
}
