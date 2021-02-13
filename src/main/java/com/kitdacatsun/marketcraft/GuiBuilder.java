package me.jame.chestinterface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;


public class GuiBuilder{

    public Inventory createGui(String title, int size){
        return Bukkit.getServer().createInventory(null, size, title);
    }

    public ItemStack bulid(Material material,String name,int amount){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setAmount(amount);
        item.setItemMeta(meta);
        return item;
    }


    public void openInventory(Player player, String material, int amount) {
        int cost = 10;
        Inventory Bank = this.createGui(ChatColor.AQUA + "Bank", 27);
        ItemStack sellone = bulid(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN+"Sell 1 for "+cost,1);
        ItemStack sellall = bulid(Material.GREEN_STAINED_GLASS_PANE,ChatColor.GREEN+"Sell all for "+cost+" each",1);
        ItemStack middle = bulid(Material.getMaterial(material),ChatColor.BLUE+ "Selected Item",amount);


        Bank.setItem(15, sellone);
        Bank.setItem(11, sellall);
        Bank.setItem(13, middle);

        player.openInventory(Bank);

    }
}


