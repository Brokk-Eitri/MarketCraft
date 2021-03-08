package com.kitdacatsun.marketcraft;

import com.kitdacatsun.marketcraft.MarketCraft.files;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandShopMenu implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender , @NotNull Command cmd, @NotNull String label, String[] args){
        if (!(sender instanceof Player)){
            return false;
        }

        Player player = (Player)sender;

        doMenu("root", player);

        return true;
    }

    public void doMenu(String menu, Player player) {
        int itemSpace = 36;

        List<String> children = files.shop.getStringList(menu + ".children");

        if (children.size() == 0) {
            GUIItem item = new GUIItem();
            item.material = Objects.requireNonNull(Material.getMaterial(files.shop.getString(menu + ".material")));
            item.name = menu;
            CommandVillager.openShop(player, item.getItemStack());
            return;
        }

        List<GUIItem> items = new ArrayList<>();

        items.add(new GUIItem(4));
        items.add(new GUIItem("Back", Material.RED_DYE, 1, "Return to Previous Menu", 1));
        items.add(new GUIItem(4));

        for (String item: children) {
            GUIItem guiItem = new GUIItem();
            if (!item.equals("BLANK")) {
                guiItem.name = item;
                guiItem.material = Objects.requireNonNull(Material.getMaterial(files.shop.getString(item + ".material")));
            }
            items.add(guiItem);
        }

        items.add(new GUIItem(itemSpace - children.size()));

        items.add(new GUIItem(4));
        items.add(new GUIItem("Home", Material.LIME_DYE, 1, "Return Home", 1));
        items.add(new GUIItem(4));

        GUIBuilder guiBuilder = new GUIBuilder();
        guiBuilder.makeGUI("Shop Menu", items, 54);
        guiBuilder.showGUI(player);
    }
}
