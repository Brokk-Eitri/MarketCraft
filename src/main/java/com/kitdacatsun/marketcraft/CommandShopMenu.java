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

    private static SettingsFile shopMenuFile;

    @Override
    public boolean onCommand(@NotNull CommandSender sender , @NotNull Command cmd, @NotNull String label, String[] args){
        if (!(sender instanceof Player)){
            return false;
        }

        Player player = (Player)sender;

        shopMenuFile = MarketCraft.shopMenus;
        doMenu("root", player);

        return true;
    }

    public void doMenu(String menu, Player player) {
        List<GUIItem> items = new ArrayList<>();

        List<String> children = MarketCraft.shopMenus.getStringList(menu + ".children");
        if (children.size() == 0) {
            CommandShop.openShop(player, new ItemStack(Objects.requireNonNull(Material.getMaterial(shopMenuFile.getString(menu + ".material")))));
            return;
        }

        for (String item: children) {
            GUIItem guiItem = new GUIItem();
            guiItem.name = item;
            guiItem.material = Material.getMaterial(shopMenuFile.getString(item + ".material"));
            items.add(guiItem);
        }

        GUIBuilder guiBuilder = new GUIBuilder();
        guiBuilder.createInventory("Shop Menu", items);
        guiBuilder.showInventory(player);
    }
}

