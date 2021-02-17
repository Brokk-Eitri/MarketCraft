package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandShopMenu implements CommandExecutor {

    private static String location;

    @Override
    public boolean onCommand(@NotNull CommandSender sender , @NotNull Command cmd, @NotNull String label, String[] args){
        if (!(sender instanceof Player)){
            return false;
        }

        Player player = (Player)sender;

        doMenu("", player);

        return true;
    }

    public void doMenu(String item, Player player) {
        if (item.isEmpty()) {
            location = "";
        } else {
            location = location + "." + item;
        }

        if (location.startsWith(".")) {
            location = location.substring(1);
        }

        ArrayList<GUIItem> items = new ArrayList<>();


        // Get children of item
        Object[] keys = MarketCraft.shopMenus.getKeys(true);
        for (Object keyObj : keys) {
            String key = (String) keyObj;
            if (key.startsWith(location)) {
                String child = key.substring(location.length());

                if (child.startsWith(".")) {
                    child = child.substring(1);
                }

                if (!child.contains(".") && !child.replace(" ", "").isEmpty()) {
                    GUIItem guiItem = new GUIItem();
                    guiItem.name = getName(Objects.requireNonNull(Material.getMaterial(child)), location);
                    guiItem.amount = 1;
                    guiItem.lore = "Go to section";
                    items.add(guiItem);
                }
            }
        }

        // Has no children
        String directChildren;
        try {
            directChildren = MarketCraft.shopMenus.get(location).toString();
        } catch (NullPointerException e) {
            CommandShop.openShop(player, new ItemStack(Material.valueOf(item)));
            return;
        }

        // Non-Parent Children
        if (!directChildren.contains("[")) {
            for (String i : directChildren.split(" ")) {
                GUIItem guiItem = new GUIItem();
                guiItem.name = getName(Objects.requireNonNull(Material.getMaterial(i)), location);
                guiItem.amount = 1;
                guiItem.lore = "Go to shop";
                guiItem.material = Material.getMaterial(i);
                items.add(guiItem);
            }
        }

        if (items.size() == 0) {
            CommandShop.openShop(player, new ItemStack(Material.valueOf(item)));
        } else {
            GUIBuilder guiBuilder = new GUIBuilder();
            guiBuilder.createInventory("Shop Menu", items);
            guiBuilder.showInventory(player);
        }
    }

    private String formatName(String name) {
        List<String> out = new ArrayList<>();
        String previous = " ";
        for (char character : name.toCharArray()) {
            if (character == '_') {
                out.add(" ");
                continue;
            }

            if (previous.equals(" ")) {
                out.add(String.valueOf(character).toUpperCase());
            } else {
                out.add(String.valueOf(character).toLowerCase());
            }

            previous = String.valueOf(character);
        }

        return String.join("", out);
    }

    private String getName(Material material, String location) {
        String name = formatName(material.name());

        // Try and get name from yaml if it exists
        try {
            MarketCraft.shopMenus.get(location + ".name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }
}

