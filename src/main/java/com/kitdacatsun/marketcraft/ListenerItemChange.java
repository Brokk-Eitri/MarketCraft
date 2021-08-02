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
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class ListenerItemChange implements Listener {

    private List<Material> inventoryMaterials() {
        List<Material> materials = new ArrayList<>();
        materials.add(Material.CHEST);
        materials.add(Material.CHEST_MINECART);
        materials.add(Material.HOPPER);
        materials.add(Material.HOPPER_MINECART);
        materials.add(Material.BREWING_STAND);

        return materials;
    }

    @EventHandler
    private void BlockDropItemEvent(BlockDropItemEvent event) {
        if (event.getItems().size() == 0) {
            return;
        }

        Item lastItem = event.getItems().get(event.getItems().size() - 1);

        if (inventoryMaterials().contains(lastItem.getItemStack().getType())) {
            logItemChange(lastItem.getName(), lastItem.getItemStack().getAmount());
            return;
        }

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
    private void FurnaceSmeltEvent(FurnaceSmeltEvent event) {
        logItemChange(event.getResult().getType(), event.getResult().getAmount());
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
        MarketCraft.server.getLogger().info(ChatColor.GREEN + (change > 0 ? "+" : "-") + change + " " + name);

        ItemChange itemChange = new ItemChange();
        itemChange.name = name.toUpperCase().replace(" ", "_");
        itemChange.change = change;
        MarketCraft.changeBuffer.add(itemChange);
    }

    private void logItemChange(Material material, int change) {
        logItemChange(material.name(), change);
    }

}
