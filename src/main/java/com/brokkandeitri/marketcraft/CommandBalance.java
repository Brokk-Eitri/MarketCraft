package com.brokkandeitri.marketcraft;

import com.brokkandeitri.marketcraft.MarketCraft.files;
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

            UUID uUID = player.getUniqueId();
            String playerBalanceKey = "players." + uUID + ".balances";

            if (!files.balances.contains(playerBalanceKey)) {
                files.balances.set(playerBalanceKey, 0);
            }

            int balances = (int) files.balances.get(playerBalanceKey);

            sender.sendMessage(ChatColor.GOLD + "Your balances is £" + balances);

            return true;
        } else {
            return false;
        }
    }
}