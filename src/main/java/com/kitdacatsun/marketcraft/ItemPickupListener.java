package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.event.Listener;


public class ItemPickupListener implements Listener {

    // BlockPlaceEvent
    // CraftItemEvent
    // InventoryPickupItemEvent
    // EntityDropItemEvent (Not by player)


    private void logItemChange(String name, int change) {
        ItemChange itemChange = new ItemChange();
        itemChange.name = name.toUpperCase().replace(" ", "_");
        itemChange.change = change;
        MarketCraft.changeBuffer.add(itemChange);
    }

    private void logItemChange(Material material, int change) {
        ItemChange itemChange = new ItemChange();
        itemChange.name = material.name();
        itemChange.change = change;
        MarketCraft.changeBuffer.add(itemChange);
    }
}
