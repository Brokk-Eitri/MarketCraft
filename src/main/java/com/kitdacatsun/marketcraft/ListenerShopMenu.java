package com.kitdacatsun.marketcraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListenerShopMenu implements Listener {
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Shop Menu") || event.getCurrentItem() == null) {
            return;
        }

        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        new CommandShopMenu().doMenu(name, (Player) event.getWhoClicked());
    }
}
