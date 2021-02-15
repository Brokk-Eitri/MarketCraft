package com.kitdacatsun.marketcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;


public class GuiBuilder{

    public Inventory createGui(String title, int size){
        return Bukkit.getServer().createInventory(null, size, title);
    }

    public ItemStack build(Material material, String name, int amount, String loreString){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList();
        lore.add(loreString);
        meta.setLore(lore);
        meta.setDisplayName(name);
        item.setAmount(amount);
        item.setItemMeta(meta);

        return item;
    }


    public void openInventory(Player player, String material, String name, int amount, String position , String originalMaterial, String originalName, int originalAmount) {
        Inventory bank = this.createGui("Shop", 27);

        int cost = 10;
        ItemStack sell1 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 1",1, "Shop");
        ItemStack sell10 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 10",10, "Shop");
        ItemStack sell64 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 64",64, "Shop");
        ItemStack buy1 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 1",1, "Shop");
        ItemStack buy10 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 10",10, "Shop");
        ItemStack buy64 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 64",64, "Shop");
        ItemStack back = build(Material.RED_DYE,"Back",1,"Click to return to menu");
        ItemStack middle = null;
        ItemStack selector = null;


        if (position.equals("center")) {
            middle = build(Material.getMaterial(material), name, amount, "Selected Item");
            selector = build(Material.getMaterial(originalMaterial), originalName, originalAmount, "Click to confirm");

        }else {
            selector = build(Material.getMaterial(material), name, amount, "Click to confirm");
            middle = build(Material.getMaterial(originalMaterial),originalName,originalAmount,"Selected Item");
        }


        bank.setItem(11, sell1);
        bank.setItem(10, sell10);
        bank.setItem(9, sell64);
        bank.setItem(15, buy1);
        bank.setItem(16, buy10);
        bank.setItem(17, buy64);
        bank.setItem(4, back);
        bank.setItem(13, middle);
        bank.setItem(22, selector);



        player.openInventory(bank);

    }
}


