package com.kitdacatsun.marketcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class CommandBank implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GuiBuilder Bank = new GuiBuilder();
            Bank.openInventory(player, "BARRIER","",0,"center","BARRIER","",0);
            return true;
        } else {
            return false;
        }
    }
}