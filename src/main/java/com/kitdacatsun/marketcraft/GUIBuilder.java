package com.kitdacatsun.marketcraft;

import jdk.internal.net.http.common.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUIBuilder {

    public static int TOP_MID = 4;
    public static int MID = 13;
    public static int BOT_MID = 27;

    public static HashMap<Player, Inventory> playerInventories = new HashMap<>();

    private Inventory inventory;
    private List<Pair<GUIItem, Integer>> itemPairs;

    public void createInventory(String title, List<Pair<GUIItem, Integer>> itemPairs) {
        inventory = MarketCraft.server.createInventory(null, 27, title);
        this.itemPairs = itemPairs;

        int i = 0;
        for (Pair<GUIItem, Integer> item : itemPairs) {
            for (int j = 0; j < item.second; j++) {
                if (item.first != null) {
                    inventory.setItem(i + j, item.first.getItemStack());
                }
            }

            i++;
        }
    }

    public void showInventory(Player player) {
        player.openInventory(inventory);
        playerInventories.put(player, inventory);
    }

    public GUIItem getItem(int index) {
        int i = 0;
        for (Pair<GUIItem, Integer> item : itemPairs) {
            for (int j = 0; j < item.second; j++) {
                if (i + j == index) {
                    return itemPairs.get(i).first;
                }
            }

            i++;
        }

        return null;
    }

    public void setItem(int index, GUIItem newItem) {
        inventory.setItem(index, newItem.getItemStack());
    }

//    public Inventory createGui(String title, int size){
//        return Bukkit.getServer().createInventory(null, size, title);
//    }
//
//    public ItemStack build(Material material, String name, int amount, String loreString){
//        ItemStack item = new ItemStack(material);
//        ItemMeta meta = item.getItemMeta();
//        ArrayList<String> lore = new ArrayList();
//        lore.add(loreString);
//        meta.setLore(lore);
//        meta.setDisplayName(name);
//        item.setAmount(amount);
//        item.setItemMeta(meta);
//
//        return item;
//    }
//
//
//    public void openInventory(Player player, String material, String name, int amount, String position , String originalMaterial, String originalName, int originalAmount) {
//        Inventory bank = this.createGui("Shop", 27);
//
//        int cost = 10;
//        ItemStack sell1 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 1",1, "Shop");
//        ItemStack sell10 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 10",10, "Shop");
//        ItemStack sell64 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 64",64, "Shop");
//        ItemStack buy1 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 1",1, "Shop");
//        ItemStack buy10 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 10",10, "Shop");
//        ItemStack buy64 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 64",64, "Shop");
//        ItemStack back = build(Material.RED_DYE,"Back",1,"Click to return to menu");
//        ItemStack middle = null;
//        ItemStack selector = null;
//
//
//        if (position.equals("center")) {
//            middle = build(Material.getMaterial(material), name, amount, "Selected Item");
//            selector = build(Material.getMaterial(originalMaterial), originalName, originalAmount, "Click to confirm");
//
//        } else {
//            selector = build(Material.getMaterial(material), name, amount, "Click to confirm");
//            middle = build(Material.getMaterial(originalMaterial),originalName,originalAmount,"Selected Item");
//        }
//
//        bank.setItem(11, sell1);
//        bank.setItem(10, sell10);
//        bank.setItem(9, sell64);
//        bank.setItem(15, buy1);
//        bank.setItem(16, buy10);
//        bank.setItem(17, buy64);
//        bank.setItem(4, back);
//        bank.setItem(13, middle);
//        bank.setItem(22, selector);
//
//        player.openInventory(bank);
//    }


}

class GUIItem {
    public Material material;
    public String name;
    public int amount;
    public String lore;

    public GUIItem() {

    }

    public GUIItem(String name, Material material, int amount, String lore) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.lore = lore;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material);

        if (lore != null) {
            ArrayList<String> loreList = new ArrayList<>();
            loreList.add(lore);
            itemStack.setLore(loreList);
        }

        if (name != null) {
            itemStack.getItemMeta().setDisplayName(name);
        }

        itemStack.setAmount(amount);

        return itemStack;
    }
}


