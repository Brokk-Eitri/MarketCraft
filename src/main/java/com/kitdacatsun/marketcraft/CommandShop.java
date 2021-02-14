package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandShop implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender , @NotNull Command cmd, @NotNull String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;
            player.sendMessage(ChatColor.DARK_GRAY + "HEY IT FINALLY WORKED");
        }
        return true;
    }
}