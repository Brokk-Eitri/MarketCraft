package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commandshop implements CommandExecutor {

    public Commandshop(ChestInterface chestInterface) {
    }

    public Commandshop() {

    }

    @Override
    public boolean onCommand(CommandSender sender , Command cmd, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;
            player.sendMessage(ChatColor.DARK_GRAY + "HEY IT FINALY WORKED");
        }
        return true;
    }
}