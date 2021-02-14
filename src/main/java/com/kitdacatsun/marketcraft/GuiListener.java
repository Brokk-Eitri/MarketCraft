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
            if (event.getView().getTitle().equals(ChatColor.AQUA + "Bank")) {
                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();
                assert clickedItem != null;

                Player player = (Player) event.getWhoClicked();
                player.sendMessage(clickedItem.getType().toString() + " " + clickedItem.getAmount());

                int cost = 10;
                String sellAll = ChatColor.GREEN + "Sell all for " + cost + " each";
                String sellOne = ChatColor.GREEN + "Sell 1 for " + cost;


                if(!displayNameOf(clickedItem).equals(sellOne) && !displayNameOf(clickedItem).equals(sellAll)) {
                    //player.getInventory().addItem(Objects.requireNonNull(event.getView().getItem(13)));
                    player.sendMessage("Item not in bank");

                    GuiBuilder Bank = new GuiBuilder();
                    Bank.openInventory(player, clickedItem.getType().toString(), clickedItem.getAmount());
                    clickedItem.setAmount(0);
                } else {
                    player.sendMessage("Item in bank");
                }
            }

        }
    }

    private String displayNameOf(ItemStack item) {
        return item.getItemMeta().getDisplayName();
    }
}




