package com.kitdacatsun.marketcraft;

import jdk.vm.ci.code.site.Mark;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CommandShop implements CommandExecutor {

    private static String location;

    @Override
    public boolean onCommand(@NotNull CommandSender sender , @NotNull Command cmd, @NotNull String label, String[] args){
        if (!(sender instanceof Player)){
            return false;
        }

        Player player = (Player)sender;

        doMenu("", player);

        Inventory shop = MarketCraft.server.createInventory(null, 27, "Shop");
        player.openInventory(shop);

        return true;
    }

    public static void doMenu(String item, Player player) {
        if (item.isEmpty()) {
            location = "";
        } else {
            location = location + "." + item;
        }

        if (!Arrays.asList(MarketCraft.shopMenus.getKeys(true)).contains(location) && !location.isEmpty()) {
            player.sendMessage("Taking you to shop for " + item);
        } else {
            ArrayList<String> children = new ArrayList<>();

            Object[] keys = MarketCraft.shopMenus.getKeys(true);
            for (Object key: keys) {
                String keyStr = (String) key;
                if (keyStr.startsWith(location) && !keyStr.replace(location, "").contains(".")) {
                    children.add(keyStr.replace(location, ""));
                }
            }

            showGUI(children, player);
        }
    }

    private static void showGUI(ArrayList<String> items, Player player) {
        Inventory shop = MarketCraft.server.createInventory(null, 27, "Shop");

        ItemStack item;
        for (int i = 0; i < items.size(); i++) {
            item = new ItemStack(Objects.requireNonNull(Material.getMaterial(items.get(1))));
            shop.addItem(new ItemStack(item));
        }

        player.openInventory(shop);
    }
}
