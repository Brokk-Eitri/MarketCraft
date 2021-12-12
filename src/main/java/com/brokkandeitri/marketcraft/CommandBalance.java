package com.brokkandeitri.marketcraft;

import com.brokkandeitri.marketcraft.MarketCraft.files;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandBalance implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        UUID uUID = player.getUniqueId();
        String playerBalanceKey = "players." + uUID + ".balance";

        if (!files.balances.contains(playerBalanceKey)) {
            files.balances.set(playerBalanceKey, 0);
        }

        int balance = (int) files.balances.get(playerBalanceKey);
        int assets = 0;

        for (ItemStack itemStack : ((Player) sender).getInventory().getContents()) {
            if (itemStack != null) {
                assets += MarketCraft.getPrice(itemStack) * itemStack.getAmount();
            }
        }

        sender.sendMessage(ChatColor.GOLD + "Balance: £" + balance);
        sender.sendMessage(ChatColor.GOLD + "Estimate Asset Value: £" + assets);
        sender.sendMessage(ChatColor.GOLD + "Estimate Net Worth: £" + (balance + assets));

        return true;
    }
}