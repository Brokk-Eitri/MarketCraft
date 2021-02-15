package com.kitdacatsun.marketcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class GuiBuilder {

    public Inventory createGui(String title, int size){
        return Bukkit.getServer().createInventory(null, size, title);
    }

    public ItemStack build(Material material, String name, int amount){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setAmount(amount);
        item.setItemMeta(meta);

        return item;
    }


    public void openInventory(Player player, String material, int amount) {
        Inventory Bank = this.createGui(ChatColor.AQUA + "Bank", 27);

        int cost = 10;
        ItemStack sellOne = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Sell 1 for " + cost,1);
        ItemStack sellAll = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Sell all for " + cost + " each",1);

        ItemStack middle = build(Material.getMaterial(material),ChatColor.BLUE+ "Selected Item",amount);

        Bank.setItem(15, sellOne);
        Bank.setItem(11, sellAll);
        Bank.setItem(13, middle);

        player.openInventory(Bank);
    }
}


