package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandPlayerShop implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;

        addPLayerShop(player, null);

        return true;
    }

    public static void addPLayerShop(Player player, ItemStack item) {
        GUIBuilder playerShop = new GUIBuilder();

        List<GUIItem> items = new ArrayList<>();

        // Row 1
        items.add(new GUIItem(4));
        items.add(new GUIItem("Exit", Material.RED_DYE, 1, "Exit", 1));
        items.add(new GUIItem(4));

        // Row 2
        items.add(new GUIItem("Decrease price by 50", Material.RED_STAINED_GLASS_PANE, 50, "Decrease price", 1));
        items.add(new GUIItem("Decrease price by 10", Material.RED_STAINED_GLASS_PANE, 10, "Decrease price", 1));
        items.add(new GUIItem("Decrease price by 1", Material.RED_STAINED_GLASS_PANE, 1, "Decrease price", 1));

        items.add(new GUIItem(1));
        items.add(new GUIItem(item, 1));
        items.add(new GUIItem(1));


        items.add(new GUIItem("Increase price by 1", Material.GREEN_STAINED_GLASS_PANE, 1, "Increase price", 1));
        items.add(new GUIItem("Increase price by 10", Material.GREEN_STAINED_GLASS_PANE, 10, "Increase price", 1));
        items.add(new GUIItem("Increase price by 50", Material.GREEN_STAINED_GLASS_PANE, 50, "Increase price", 1));

        // Row 3
        items.add(new GUIItem(4));
        items.add(new GUIItem("Select an option", Material.GRAY_DYE, 1, "Confirm", 1));
        items.add(new GUIItem(4));

        playerShop.createInventory("Player Shop", items);
        playerShop.showInventory(player);

    }

    public static void addItem(Player player, ItemStack item, int amount){
        Map serialized = item.serialize();
        player.sendMessage(String.valueOf(serialized));
        //add items
    }

    public static void openPlayerShop(Player player){
        //open player shop
    }

}
