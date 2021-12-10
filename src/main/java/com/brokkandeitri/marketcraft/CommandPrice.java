package com.brokkandeitri.marketcraft;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandPrice implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length != 1){
            return false;
        }

        if (args[0].equals("all")) {
            MarketCraft.displayPrices();
        } else {
            Material material = Material.getMaterial(args[0]);
            assert material != null;
            ItemStack item = new ItemStack(material, 1);
            sender.sendMessage(ChatColor.AQUA + "Price of " + item.getI18NDisplayName() + " is: " + MarketCraft.getPrice(item));
        }

        return true;
    }

    public static void OpenPriceHistory(Player player, ItemStack item){
        GUIBuilder priceHistory = new GUIBuilder();

        List<GUIItem> items = new ArrayList<>();

        // Row 1
        items.add(new GUIItem(4));
        items.add(new GUIItem("Exit", Material.RED_DYE, 1, "Exit", 1));
        items.add(new GUIItem(4));

        // Row 2
        items.add(new GUIItem("Decrease time period by 32", Material.RED_STAINED_GLASS_PANE, 32, "Decrease time", 1));
        items.add(new GUIItem("Decrease time period by 16", Material.RED_STAINED_GLASS_PANE, 16, "Decrease time", 1));
        items.add(new GUIItem("Decrease time period by 1", Material.RED_STAINED_GLASS_PANE, 1, "Decrease time", 1));

        items.add(new GUIItem(1));
        items.add(new GUIItem(item, 1));
        items.add(new GUIItem(1));


        items.add(new GUIItem("Increase time period by 1", Material.GREEN_STAINED_GLASS_PANE, 1, "Increase time", 1));
        items.add(new GUIItem("Increase time period by 16", Material.GREEN_STAINED_GLASS_PANE, 16, "Increase time", 1));
        items.add(new GUIItem("Increase time period by 32", Material.GREEN_STAINED_GLASS_PANE, 32, "Increase time", 1));

        // Row 3
        items.add(new GUIItem(4));
        items.add(new GUIItem("Click an item to select to view price history", Material.GRAY_DYE, 1, "Confirm", 1));
        items.add(new GUIItem(4));


        priceHistory.makeGUI("Price history", items);
        priceHistory.showGUI(player);

    }

    public static void DisplayStats(Player player, float mean, float median, int mode, int current, int max, int min, ItemStack selected) {
        GUIBuilder stats = new GUIBuilder();

        List<GUIItem> items = new ArrayList<>();

        // Row 1
        items.add(new GUIItem(4));
        items.add(new GUIItem("Exit", Material.RED_DYE, 1, "Exit", 1));
        items.add(new GUIItem(4));

        // Row 2
        items.add(new GUIItem("Mean: " + mean, GetMaterialType(current, mean), 1, "Mean", 1));
        items.add(new GUIItem(1));
        items.add(new GUIItem("Median: " + median, GetMaterialType(current, median), 1, "Median", 1));
        items.add(new GUIItem(1));
        items.add(new GUIItem("Mode: " + mode, GetMaterialType(current, mode), 1, "Mode", 1));
        items.add(new GUIItem(1));
        items.add(new GUIItem("Min price: " + min, GetMaterialType(current, min), 1, "Min", 1));
        items.add(new GUIItem(1));
        items.add(new GUIItem("Max price: " + max, GetMaterialType(current, max), 1, "Max", 1));

        // Row 3
        items.add(new GUIItem(4));
        items.add(new GUIItem("Current price : " + current, selected.getType(), 1, "Current", 1));
        items.add(new GUIItem(4));


        stats.makeGUI("Price history", items);
        stats.showGUI(player);

    }

    private static Material GetMaterialType(int current, float selected) {
        Material material;
        if (current > selected) {
            material = Material.RED_STAINED_GLASS_PANE;
        } else  if (current < selected) {
            material = Material.GREEN_STAINED_GLASS_PANE;
        } else {
            material = Material.ORANGE_STAINED_GLASS_PANE;
        }
        return material;
    }
}
