package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
        GUIBuilder playerShopMenu = new GUIBuilder();

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


        playerShopMenu.createInventory("Player Shop", items);
        playerShopMenu.showInventory(player);

    }

    public static void addItem(Player player, ItemStack item, int price){

        List<String> uids = MarketCraft.playerShop.getStringList("uid");
        int uid = uids.size();
        uids.add(String.valueOf(uid));
        MarketCraft.playerShop.set("uid",uids);



        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("item" , item);
        itemMap.put("price", price);
        itemMap.put("seller", player.getName());

        player.sendMessage(String.valueOf(item));
        for (String key: itemMap.keySet()) {
            MarketCraft.playerShop.set(uid + "." + key, itemMap.get(key));
        }
    }

    public static void openPlayerShop(Player player){
        List<String> uids = MarketCraft.playerShop.getStringList("uid");
        GUIBuilder playerShop = new GUIBuilder();

        List<GUIItem> items = new ArrayList<>();
        int x = 0;
        for (Object i : uids) {
            x += 1;

            if (x < 27) {
                player.sendMessage("test");
                String price  = String.valueOf(MarketCraft.playerShop.get(i + ".price"));
                String seller = String.valueOf(MarketCraft.playerShop.get(i + ".seller"));
                ItemStack item = (ItemStack) MarketCraft.playerShop.get(i + ".item");

                player.getInventory().addItem(item);

                player.sendMessage(price + seller);
                items.add(new GUIItem(item.getI18NDisplayName() , item.getType() , item.getAmount() ,seller + price , 1));
            }
        }


        player.sendMessage(String.valueOf(items));
        playerShop.createInventory("Player Shop", items);
        playerShop.showInventory(player);
    }
}
