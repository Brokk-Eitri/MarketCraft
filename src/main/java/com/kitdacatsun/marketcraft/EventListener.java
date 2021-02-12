package com.kitdacatsun.marketcraft;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EventListener implements Listener {

    @EventHandler
    public void EntityPickupItemEvent(EntityPickupItemEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        MarketCraft.itemChanges.add(new ItemChange(event.getItem(), 1));
        MarketCraft.logger.info(event.getEntity().getName() + " picked up " + event.getItem().getName());
    }

    @EventHandler
    public void EntityDropItemEvent(EntityDropItemEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        MarketCraft.itemChanges.add(new ItemChange(event.getItemDrop(), -1));
        MarketCraft.logger.info(event.getEntity().getName() + " dropped " + event.getItemDrop().getName());
    }

}
