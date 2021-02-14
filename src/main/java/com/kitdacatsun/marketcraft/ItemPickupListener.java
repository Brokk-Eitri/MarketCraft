package com.kitdacatsun.marketcraft;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;


public class ItemPickupListener implements Listener {

    @EventHandler
    public void EntityPickupItemEvent(EntityPickupItemEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        // TODO record if it has been picked up before

        ItemChange itemChange = new ItemChange();
        itemChange.name = event.getItem().getName().toUpperCase().replace(" ", "_");
        itemChange.change = event.getItem().getItemStack().getAmount();

        MarketCraft.changeBuffer.add(itemChange);
    }
}
