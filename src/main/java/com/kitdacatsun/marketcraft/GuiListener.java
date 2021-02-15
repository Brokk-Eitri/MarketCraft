package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {


    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (event.getView().getBottomInventory().getType() == InventoryType.PLAYER) {
            if (event.getView().getTitle().equals(ChatColor.AQUA + "Bank")) {
                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();
                assert clickedItem != null;

                Player player = (Player) event.getWhoClicked();
                player.sendMessage(clickedItem.getType().toString() + " " + clickedItem.getAmount());

                int cost = 10;
                String sellAll = ChatColor.GREEN + "Sell all for " + cost + " each";
                String sellOne = ChatColor.GREEN + "Sell 1 for " + cost;

                    if (clickedItem.getLore() != null) {
                        if (clickedItem.getItemMeta().getLore().toString().equals("[Shop]")){
                            GuiBuilder bank = new GuiBuilder();
                            if (event.getView().getItem(13) != null){
                                String originalMaterial = event.getView().getItem(13).getType().toString();
                                String originalName = event.getView().getItem(13).getItemMeta().getDisplayName();
                                int originalAmount = event.getView().getItem(13).getAmount();
                                bank.openInventory(player, clickedItem.getType().toString(),clickedItem.getItemMeta().getDisplayName(), clickedItem.getAmount(),"selector",originalMaterial,originalName,originalAmount);

                            } else {
                                bank.openInventory(player, clickedItem.getType().toString(),clickedItem.getItemMeta().getDisplayName(), clickedItem.getAmount(),"selector","BARRIER","",0);
                            }
                        } else {
                            //return to previous menu
                        }

                    } else {

                        GuiBuilder bank = new GuiBuilder();
                        if (event.getView().getItem(22) != null){
                            String originalMaterial = event.getView().getItem(22).getType().toString();
                            String originalName = event.getView().getItem(22).getItemMeta().getDisplayName();
                            int originalAmount = event.getView().getItem(22).getAmount();

                            bank.openInventory(player, clickedItem.getType().toString(),clickedItem.getItemMeta().getDisplayName(), 1,"center",originalMaterial,originalName,originalAmount);

                        } else {
                            bank.openInventory(player, clickedItem.getType().toString(),clickedItem.getItemMeta().getDisplayName(), 1,"center","BARRIER","",0);
                        }
                    }
                }
            }

        }

    private String displayNameOf(ItemStack item) {
        return item.getItemMeta().getDisplayName();
    }
}




