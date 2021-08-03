package com.kitdacatsun.marketcraft;

import com.kitdacatsun.marketcraft.MarketCraft.files;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandPlayerShop implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;

        if (args[0].equals("open")){
            openPlayerShop(player, Integer.parseInt(args[1]));
        } else {
            addPLayerShop(player, null);
        }
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
        items.add(new GUIItem("Decrease price by 32", Material.RED_STAINED_GLASS_PANE, 32, "Decrease price", 1));
        items.add(new GUIItem("Decrease price by 16", Material.RED_STAINED_GLASS_PANE, 16, "Decrease price", 1));
        items.add(new GUIItem("Decrease price by 1", Material.RED_STAINED_GLASS_PANE, 1, "Decrease price", 1));

        items.add(new GUIItem(1));
        items.add(new GUIItem(item, 1));
        items.add(new GUIItem(1));


        items.add(new GUIItem("Increase price by 1", Material.GREEN_STAINED_GLASS_PANE, 1, "Increase price", 1));
        items.add(new GUIItem("Increase price by 16", Material.GREEN_STAINED_GLASS_PANE, 16, "Increase price", 1));
        items.add(new GUIItem("Increase price by 32", Material.GREEN_STAINED_GLASS_PANE, 32, "Increase price", 1));

        // Row 3
        items.add(new GUIItem(4));
        items.add(new GUIItem("Select an option", Material.GRAY_DYE, 1, "Confirm", 1));
        items.add(new GUIItem(4));


        playerShopMenu.makeGUI("Player Shop - Add", items);
        playerShopMenu.showGUI(player);

    }

    public static void addItem(Player player, ItemStack item, int price){

        List<String> uids = files.playerShop.getStringList("uid");
        int uid = uids.size();
        uids.add(String.valueOf(uid));
        files.playerShop.set("uid",uids);

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("item" , item);
        itemMap.put("price", price);
        itemMap.put("seller", player.getName());
        itemMap.put("uid", player.getUniqueId().toString());

        for (String key: itemMap.keySet()) {
            files.playerShop.set(uid + "." + key, itemMap.get(key));
        }
        files.playerShop.save();
    }

    public static void openPlayerShop(Player player, int page){
        List<String> uids = files.playerShop.getStringList("uid");
        List<GUIItem> items = new ArrayList<>();

        items.add(new GUIItem(3));
        items.add(new GUIItem("Previous page", Material.ORANGE_DYE, 1 , "Choose page", 1));
        items.add(new GUIItem("Page " + page, Material.PAPER, 1, "Current page", 1));
        items.add(new GUIItem("Next page", Material.ORANGE_DYE, 1 , "Choose page", 1));
        items.add(new GUIItem(3));

        int counter = 0;
        int addedAmount = 0;
        for (Object i : uids){
            if (page * 36 <= counter && counter < (page + 1) * 36){
                int price = (int) files.playerShop.get(i + ".price");
                int cost = (int) Math.ceil(price * 1.05);
                int tax = cost - price;
                String seller = String.valueOf(files.playerShop.get(i + ".seller"));
                ItemStack item = (ItemStack) files.playerShop.get(i + ".item");
                ArrayList<String> loreList = new ArrayList<>();
                loreList.add("Price: £" + price + " Tax: £" + tax + " Total: £" + cost + ", Seller: " + seller);
                item.setLore(loreList);
                items.add(new GUIItem(item, 1));
                addedAmount += 1;
            }
            counter += 1;
        }

        int empty = 36 - addedAmount;
        items.add(new GUIItem(empty));

        items.add(new GUIItem(3));
        items.add(new GUIItem("Cancel", Material.RED_DYE, 1 , "Cancel", 1));
        items.add(new GUIItem(1));
        items.add(new GUIItem("Select an item", Material.GRAY_DYE, 1 , "Confirm", 1));
        items.add(new GUIItem(3));

        GUIBuilder playerShop = new GUIBuilder();
        playerShop.makeGUI("Player Shop", items, 54);
        playerShop.showGUI(player);
    }
}
