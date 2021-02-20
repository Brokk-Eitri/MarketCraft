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
        makeInv(title, itemPairs, 27);
    }

    public void createInventory(String title, List<GUIItem> itemPairs, int size) {
        makeInv(title, itemPairs, size);
    }

    private void makeInv(String title, List<GUIItem> itemPairs, int size) {
        inventory = MarketCraft.server.createInventory(null, size, title);

        int i = 0;
        for (GUIItem item : itemPairs) {
            for (int j = 0; j < item.count; j++) {
                if (item.name != null) {
                    inventory.setItem(i + j, item.getItemStack());
                }
                i += 1;

                if (i > size) {
                    MarketCraft.logger.warning("GUIBuilder given too many items");
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
    public int amount = 1;
    public String lore = null;
    public int count = 1;

    public GUIItem() { }

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
        if (itemStack != null) {
            this.material = itemStack.getType();
            this.name = material.name();
            this.amount = 1;
            if (itemStack.getLore() != null) {
                this.lore = itemStack.getLore().get(0);
            }
        } else {
            name = null;
            this.count = count;
        }

        this.count = count;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        if (lore != null) {
            ArrayList<String> loreList = new ArrayList<>();
            loreList.add(lore);
            meta.setLore(loreList);
        }

        if (name == null) {
            meta.setDisplayName(itemStack.getItemMeta().getDisplayName());
        } else {
            meta.setDisplayName(name);
        }

        itemStack.setItemMeta(meta);
        itemStack.setAmount(amount);

        return itemStack;
    }
}


