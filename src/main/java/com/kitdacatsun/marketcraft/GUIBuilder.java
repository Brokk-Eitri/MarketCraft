package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIBuilder {

    public static final int TOP_MID = 4;
    public static final int MID = 13;
    public static final int BOT_MID = 22;

    public Inventory inventory;

    public void createInventory(String title, List<GUIItem> itemPairs) {
        inventory = MarketCraft.server.createInventory(null, 27, title);

        int i = 0;
        for (GUIItem item : itemPairs) {
            for (int j = 0; j < item.count; j++) {
                if (item.name != null) {
                    inventory.setItem(i + j, item.getItemStack());
                }
                i += 1;

                if (i > 27) {
                    return;
                }
            }
        }
    }

    public void showInventory(Player player) {
        player.openInventory(inventory);
    }
}

class GUIItem {
    public Material material;
    public String name;
    public int amount;
    public String lore;
    public int count;

    public GUIItem() {}

    public GUIItem(int count) {
        this.count = count;
        this.name = null;
    }

    public GUIItem(String name, Material material, int amount, String lore, int count) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.lore = lore;
        this.count = count;
    }

    public GUIItem(ItemStack itemStack, int count) {
        this.material = itemStack.getType();
        this.name = material.name();
        this.amount = 1;
        if (itemStack.getLore() != null) {
            this.lore = itemStack.getLore().get(0);
        }
        this.count = count;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(lore);
        meta.setLore(loreList);

        if (name == null) {
            meta.setDisplayName(itemStack.getItemMeta().getDisplayName());
        } else {
            meta.setDisplayName(name);
        }

        itemStack.setAmount(amount);

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}


