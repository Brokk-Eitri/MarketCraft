package com.kitdacatsun.marketcraft;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class GuiListener implements Listener {


    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (event.getView().getBottomInventory().getType() == InventoryType.PLAYER){
            if (event.getView().getTitle().equals(ChatColor.AQUA + "Bank")) {
                Player player = (Player) event.getWhoClicked();
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                player.sendMessage(clickedItem.getType().toString() + " " + clickedItem.getAmount());
                int cost = 10;

                player.sendMessage(String.valueOf(!(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Sell all for "+cost+" each"))));
                player.sendMessage(String.valueOf(!(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Sell 1 for "+cost)) && !(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Sell all for "+cost+" each"))));
                if(!(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Sell 1 for "+cost)) && !(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Sell all for "+cost+" each"))){
                    //if (event.getView().getItem(13))
                    player.getInventory().addItem(event.getView().getItem(13));
                    player.sendMessage("Item not in bank");
                    GuiBuilder Bank = new GuiBuilder();
                    Bank.openInventory(player, clickedItem.getType().toString(), clickedItem.getAmount());
                    clickedItem.setAmount(0);

                }
                else{
                    player.sendMessage("Item in bank");
                }


            }

        }
    }
}




