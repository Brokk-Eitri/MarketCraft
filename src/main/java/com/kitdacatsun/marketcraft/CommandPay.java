package com.kitdacatsun.marketcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


public class Commandpay implements CommandExecutor {
    private static ChestInterface plugin;

    public Commandpay(ChestInterface chestInterface) {
    }

    public Commandpay(){

    }
    @Override
    public boolean onCommand(CommandSender sender , Command cmd, String label, String[] args) {

        if (args.length != 2) {
            return false;
        }
        Player player = Bukkit.getServer().getPlayer(args[0]);
        if( player == null ) {
            sender.sendMessage( ChatColor.RED  + "Error: Player not found" );
            return true;
        }
        UUID Uuid = player.getUniqueId();

        int amount = Integer.parseInt(args[1]);
        int balance = 0;
        player.sendMessage(ChatColor.GOLD+"You have been payed £"+amount+" by "+sender.getName());


        if(Files.get().contains("Players."+ Uuid.toString()+".balance")) {
            balance = Files.get().getInt("Players."+Uuid.toString()+".balance");
            balance = balance+amount;
            sender.sendMessage(String.valueOf(balance));
            Files.get().set("Players." + Uuid.toString() + ".balance",balance);
            Files.save();
        }
        else{
            Files.get().set("Players." + Uuid.toString() + ".balance", amount);
            Files.get().options().copyDefaults(true);
            Files.save();
        }
        return true;
    }


}
