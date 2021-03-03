package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;


public class ItemPickupListener implements Listener {

    @EventHandler
    private void BlockDropItemEvent(BlockDropItemEvent event) {
        for (Item item: event.getItems()) {
            logItemChange(item.getName(), item.getItemStack().getAmount());
        }
    }

    @EventHandler
    private void PlayerHarvestBlockEvent(PlayerHarvestBlockEvent event) {
        for (ItemStack item: event.getItemsHarvested()) {
            logItemChange(item.getType(), item.getAmount());
        }
    }

    @EventHandler
    private void CraftItemEvent(CraftItemEvent event) {
        logItemChange(event.getRecipe().getResult().getType(), event.getRecipe().getResult().getAmount());
    }

    @EventHandler
    private void InventoryPickupItemEvent(InventoryPickupItemEvent event) {
        logItemChange(event.getItem().getName(), event.getItem().getItemStack().getAmount());
    }

    @EventHandler
    private void EntityDropItemEvent(EntityDropItemEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            return;
        }

        logItemChange(event.getItemDrop().getName(), event.getItemDrop().getItemStack().getAmount());
    }

    @EventHandler
    private void EntityDeathEvent(EntityDeathEvent event) {
        for (ItemStack item: event.getDrops()) {
            logItemChange(item.getType(), item.getAmount());
        }
    }

    private void logItemChange(String name, int change) {
        ItemChange itemChange = new ItemChange();
        itemChange.name = name.toUpperCase().replace(" ", "_");
        itemChange.change = change;
        MarketCraft.changeBuffer.add(itemChange);

        log(itemChange);
    }

    private void logItemChange(Material material, int change) {
        ItemChange itemChange = new ItemChange();
        itemChange.name = material.name();
        itemChange.change = change;
        MarketCraft.changeBuffer.add(itemChange);

        log(itemChange);
    }

    private void log(ItemChange change) {
        if (change.change > 0) {
            MarketCraft.logger.info(ChatColor.GREEN + "[ITEM CHANGE] " + change.name + ", +" + change.change);
        } else if (change.change < 0) {
            MarketCraft.logger.info(ChatColor.RED + "[ITEM CHANGE] " + change.name + ", " + change.change);
        }
    }
}
