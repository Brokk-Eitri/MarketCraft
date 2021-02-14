package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GuiListener implements Listener {


    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (event.getView().getBottomInventory().getType() == InventoryType.PLAYER){
            if (event.getView().getTitle().equals("Shop")) {
                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();
                assert clickedItem != null;

                Player player = (Player) event.getWhoClicked();


                int cost = 10;

                if(clickedItem.getLore().getType.equals("Null")) {

                    GuiBuilder Bank = new GuiBuilder();
                    Bank.openInventory(player, clickedItem.getType().toString(), 1);

                } else {
                    //blank for now lol

                }
            }

        }
    }

    private String displayNameOf(ItemStack item) {
        return item.getItemMeta().getDisplayName();
    }
}




