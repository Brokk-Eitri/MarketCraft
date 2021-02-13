package com.kitdacatsun.marketcraft;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.persistence.PersistentDataType;


public class EventListener implements Listener {

    @EventHandler
    public void EntityPickupItemEvent(EntityPickupItemEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        NamespacedKey key = new NamespacedKey(MarketCraft.plugin, "player-owned");


        MarketCraft.logger.info(event.getItem().getItemStack().getItemMeta().getPersistentDataContainer().getOrDefault(key, PersistentDataType.STRING, "NOT_PLAYER_OWNED"));

        event.getItem().getItemStack().getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.STRING, "PLAYER_OWNED");
        Add(event.getItem().getName(), event.getItem().getItemStack().getAmount());
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Remove(event.getItemDrop().getName(), event.getItemDrop().getItemStack().getAmount());
    }

    @EventHandler
    public void PlayerEggThrowEvent(PlayerEggThrowEvent event) {
        Remove(event.getEgg().getName(), 1);
    }

    @EventHandler
    public void PlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Remove(event.getItem().getType().name(), 1);
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        Remove(event.getItemInHand().getType().name(), 1);
    }

    private void Add(String name, int number) {
        MarketCraft.changeBuffer.add(new ItemChange(formatName(name), number));
        MarketCraft.logger.info("+" + number + " " + formatName(name));
    }

    private void Remove(String name, int number) {
        MarketCraft.changeBuffer.add(new ItemChange(formatName(name), -number));
        MarketCraft.logger.info("-" + number + " " + formatName(name));
    }

    private String formatName(String name) {
        return name.toUpperCase().replace(" ", "_");
    }
}
