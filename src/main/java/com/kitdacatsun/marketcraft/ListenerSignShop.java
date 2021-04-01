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

        Vector chestDirection = sign.getFacing().getOppositeFace().getDirection();
        Location chestLocation = ((Block) sign).getLocation().add(chestDirection);
        Inventory chest = ((Chest) chestLocation.getBlock()).getBlockInventory();

        if (!(chestLocation.getBlock() instanceof Chest)) {
            return;
        }

        String[] lines = ((Sign)event.getClickedBlock()).getLines();

        if (!lines[0].toUpperCase().contains("SHOP")) {
            return;
        }

        int price = extractInt(lines[1]);

        int amount = extractInt(lines[2]);
        Material item = extractMaterial(lines[2]);
        ItemStack itemStack = new ItemStack(item, amount);

        Player seller = extractPlayer(lines[3]);
        Player buyer = event.getPlayer();

        if (!chest.containsAtLeast(itemStack, itemStack.getAmount())) {
            seller.sendMessage(ChatColor.YELLOW + "Your chest shop at " + chestLocation + " does not contain enough items");
            buyer.sendMessage(ChatColor.YELLOW + "This chest does not contain enough items");
            return;
        }

        seller.sendMessage(ChatColor.RED + "" + itemStack.getAmount() + " " + itemStack.getI18NDisplayName() + " bought from chest shop at " + chestLocation);
        buyer.sendMessage(ChatColor.GREEN + "You have bought " + itemStack.getAmount() + " " + itemStack.getI18NDisplayName());

        CommandPay.pay(event.getPlayer(), seller, price);
    }

    private int extractInt(String string) {
        return Integer.parseInt(string.replaceAll("\\D", ""));
    }

    private Material extractMaterial(String string) {
        return Material.getMaterial(string.replaceAll("\\d|[^a-zA-Z ]|[sS]\\Z", ""));
    }

    private Player extractPlayer(String string) {
        return Bukkit.getServer().getPlayer(string.replaceAll("\\W", ""));
    }
}
