package com.kitdacatsun.marketcraft;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class GUIBuilder {

    public static final int TOP_MID = 4;
    public static final int MID = 13;
    public static final int BOT_MID = 22;

    public Inventory inventory;

    public void makeGUI(String title, List<GUIItem> itemPairs) {
        makeInventory(title, itemPairs, 27);
    }

    public void makeGUI(String title, List<GUIItem> itemPairs, int size) {
        makeInventory(title, itemPairs, size);
    }

    private void makeInventory(String title, List<GUIItem> itemPairs, int size) {
        inventory = Bukkit.createInventory(null, size, title);

        int i = 0;
        for (GUIItem item : itemPairs) {
            for (int j = 0; j < item.count; j++) {
                if (item.name != null) {
                    inventory.setItem(i + j, item.getItemStack());
                }
                i += 1;

                if (i > size) {
                    getLogger().warning("GUIBuilder given too many items");
                    return;
                }
            }
        }
    }

    public void showGUI(Player player) {
        player.openInventory(inventory);
    }
}

class GUIItem {
    public Material material = null;
    public String name = null;
    public int amount = 1;
    public List<String> lore = new ArrayList<>();
    public ItemMeta meta = null;
    public int count = 1;

    public GUIItem() { }

    public GUIItem(int count) {
        this.count = count;
    }

    public GUIItem(String name, Material material, int amount, String lore, int count) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.lore.add(lore);
        this.count = count;
    }

    public GUIItem(ItemStack itemStack, int count) {
        if (itemStack != null) {
            material = itemStack.getType();
            name = itemStack.getI18NDisplayName();
            amount = itemStack.getAmount();
            lore = itemStack.getLore();
            meta = itemStack.getItemMeta();
        }

        this.count = count;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material);

        if (meta != null) {
            itemStack.setItemMeta(meta);
        }

        if (name == null) {
            itemStack.getItemMeta().displayName(Component.text(itemStack.getType().name()));
        } else {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Component.text(name));
            itemStack.setItemMeta(itemMeta);
        }

        itemStack.setLore(lore);
        itemStack.setAmount(amount);

        return itemStack;
    }
}


