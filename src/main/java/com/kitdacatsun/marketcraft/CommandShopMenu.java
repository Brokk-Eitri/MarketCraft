package com.kitdacatsun.marketcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandShopMenu implements CommandExecutor {

    private static String location;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

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
            for (Object key : keys) {
                String keyStr = (String) key;
                if (keyStr.startsWith(location) && !keyStr.replace(location, "").contains(".")) {
                    children.add(keyStr.replace(location, ""));
                }
            }

            showGUI(children, player);
        }
    }

    private static void showGUI(ArrayList<String> items, Player player) {
        // TODO Switch to using GUIBuilder
    }
}
