package me.jame.chestinterface;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandBank implements CommandExecutor {


    public CommandBank(ChestInterface chestInterface) {
    }

    public CommandBank() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GuiBuilder Bank = new GuiBuilder();
            Bank.openInventory(player, "BARRIER",0);

        }
        return true;
    }
}