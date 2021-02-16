package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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

        ArrayList<String> children = new ArrayList<>();

        Object[] keys = MarketCraft.shopMenus.getKeys(true);
        for (Object keyObj : keys) {
            String key = (String) keyObj;
            if (key.startsWith(location)) {
                String child = key.substring(location.length());

                if (child.startsWith(".")) {
                    child = child.substring(1);
                }

                if (!child.contains(".") && !child.replace(" ", "").isEmpty()) {
                    children.add(child);
                }
            }
        }

        String directChildren;
        try {
            directChildren = MarketCraft.shopMenus.get(location).toString();
        } catch (NullPointerException e) {
            CommandShop.openShop(player, new ItemStack(Material.valueOf(item)));
            return;
        }

        if (!directChildren.contains("[")) {
            children.addAll(Arrays.asList(directChildren.split(" ")));
        }

        if (children.size() == 0) {
            CommandShop.openShop(player, new ItemStack(Material.valueOf(item)));
        } else {
            showGUI(children, player);
        }
    }

    private void showGUI(ArrayList<String> items, Player player) {
        GUIBuilder guiBuilder = new GUIBuilder();
        List<GUIItem> guiItemList = new ArrayList<>();

        for (String item : items) {
            try {
                GUIItem guiItem = new GUIItem(new ItemStack(Objects.requireNonNull(Material.getMaterial(item))), 1);
                guiItem.name = formatName(guiItem.name);
                guiItemList.add(guiItem);
            } catch (NullPointerException e) {
                MarketCraft.logger.warning("Could not find item '" + item + "'");
            }
        }

        guiBuilder.createInventory("Shop Menu", guiItemList);
        guiBuilder.showInventory(player);
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
}

