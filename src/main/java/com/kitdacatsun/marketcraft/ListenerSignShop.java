package com.kitdacatsun.marketcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

public class ListenerSignShop implements Listener {

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (!(Objects.requireNonNull(event.getClickedBlock()).getBlockData() instanceof WallSign)) {
            return;
        }

        WallSign sign = (WallSign) event.getClickedBlock();
        String[] lines = ((Sign) sign).getLines();

        if (!lines[0].toUpperCase().contains("SHOP")) {
            return;
        }

        /*
SHOP
£15
15 Spruce Logs
KitDaCatsun
         */

        String notItemName = "\\d|[^a-zA-Z ]|[sS]\\Z";

        int price = Integer.parseInt(lines[1].replaceAll("\\D", ""));
        int amount = Integer.parseInt(lines[2].replaceAll("\\D", ""));
        Material item = Material.getMaterial(lines[2].replaceAll(notItemName, ""));
        Player seller = Bukkit.getServer().getPlayer(lines[3].replaceAll("\\W", ""));
        Player buyer = event.getPlayer();

        Vector chestDirection = sign.getFacing().getOppositeFace().getDirection();
        Location chestLocation = ((Block) sign).getLocation().add(chestDirection);

        if (seller == null) {
            Bukkit.getLogger().warning("Could not find player " + lines[3] + " for chest shop at " + chestLocation);
            return;
        }

        if (!(chestLocation.getBlock() instanceof Chest)) {
            return;
        }

        Inventory chest = ((Chest) chestLocation.getBlock()).getBlockInventory();

        if (chest.getContents().length < 1) {
            seller.sendMessage(ChatColor.YELLOW + "Your chest shop at " + chestLocation + " does not contain any items");
            buyer.sendMessage(ChatColor.YELLOW + "There are no items in this chest");
        }

        ItemStack itemStack = new ItemStack(chest.getContents()[0]);
        itemStack.setAmount(amount);

        if (!chest.containsAtLeast(itemStack, amount)) {
            seller.sendMessage(ChatColor.YELLOW + "Your chest shop at " + chestLocation + " does not contain enough items");
            buyer.sendMessage(ChatColor.YELLOW + "This chest does not contain enough items");
        }

        seller.sendMessage(ChatColor.RED + "Items bought from chest shop at " + chestLocation);
        buyer.sendMessage(ChatColor.GREEN + "You have bought " + amount + " " + )
        CommandPay.pay(event.getPlayer(), seller, price);
    }
}
