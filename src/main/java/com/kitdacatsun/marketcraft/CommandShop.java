package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CommandShop implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        GUIBuilder shop = new GUIBuilder();

        List<GUIItem> items = new ArrayList<>();

        // Row 1
        items.add(new GUIItem(4));
        items.add(new GUIItem("Return to Previous Menu", Material.RED_DYE, 1, "Back", 1));
        items.add(new GUIItem(4));

        // Row 2
        items.add(new GUIItem("Sell 64", Material.RED_STAINED_GLASS_PANE,   64,"Sell", 1));
        items.add(new GUIItem("Sell 10", Material.RED_STAINED_GLASS_PANE,   10,"Sell", 1));
        items.add(new GUIItem("Sell 1",  Material.RED_STAINED_GLASS_PANE,   1, "Sell", 1));
        items.add(new GUIItem(3));
        items.add(new GUIItem("Buy 1",   Material.GREEN_STAINED_GLASS_PANE, 1, "Buy",  1));
        items.add(new GUIItem("Buy 10",  Material.GREEN_STAINED_GLASS_PANE, 10,"Buy",  1));
        items.add(new GUIItem("Buy 64",  Material.GREEN_STAINED_GLASS_PANE, 64,"Buy",  1));

        // Row 3
        items.add(new GUIItem(4));
        items.add(new GUIItem("Select an option", Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1, null, 1));
        items.add(new GUIItem(4));

        shop.createInventory("Shop", items);
        shop.showInventory(player);

        return true;
    }
}