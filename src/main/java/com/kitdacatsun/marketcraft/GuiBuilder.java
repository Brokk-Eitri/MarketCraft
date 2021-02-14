package com.kitdacatsun.marketcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;



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


    public void openInventory(Player player, String material, int amount) {
        Inventory Bank = this.createGui("Shop", 27);

        int cost = 10;
        ItemStack sell1 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 1",1, "Shop");
        ItemStack sell10 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 10",10, "Shop");
        ItemStack sell64 = build(Material.RED_STAINED_GLASS_PANE,ChatColor.RED + "Sell 64",64, "Shop");
        ItemStack buy1 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 1",1, "Shop");
        ItemStack buy10 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 10",10, "Shop");
        ItemStack buy64 = build(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN + "Buy 64",64, "Shop");
        ItemStack back = build(Material.RED_DYE,"Back",1);


        ItemStack middle = build(Material.getMaterial(material),Material.getMaterial(material).getName() ,amount);

        Bank.setItem(11, sell1);
        Bank.setItem(10, sell10);
        Bank.setItem(9, sell64);
        Bank.setItem(13, middle);
        Bank.setItem(15, buy1);
        Bank.setItem(16, buy10);
        Bank.setItem(17, buy64);
        Bank.setItem(4, back);



        player.openInventory(Bank);

    }
}


