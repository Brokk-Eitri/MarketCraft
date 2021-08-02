package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
            logItemChange(lastItem.getItemStack());

            return;
        }

        for (Item item: event.getItems()) {
            logItemChange(item.getItemStack());
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
        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            //noinspection ConstantConditions
            if (itemStack != null) {
                logItemChange(itemStack.getType(), -1);
            }
        }

        logItemChange(Objects.requireNonNull(event.getInventory().getResult()));
    }

    @EventHandler
    private void FurnaceSmeltEvent(FurnaceSmeltEvent event) {
        logItemChange(Objects.requireNonNull(event.getRecipe()).getInput().getType(), -event.getRecipe().getInput().getAmount());
        logItemChange(event.getResult().getType(), event.getResult().getAmount());
    }

    @EventHandler
    private void EntityDropItemEvent(EntityDropItemEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            return;
        }

        logItemChange(event.getItemDrop().getItemStack());
    }

    @EventHandler
    private void EntityDeathEvent(EntityDeathEvent event) {
        for (ItemStack item: event.getDrops()) {
            logItemChange(item.getType(), item.getAmount());
        }
    }

    @EventHandler
    private void BlockPlaceEvent(BlockPlaceEvent event) {
        logItemChange(event.getItemInHand(), -1);
    }

    private void logItemChange(ItemStack itemStack) {
        logItemChange(itemStack.getType(), itemStack.getAmount());
    }

    private void logItemChange(ItemStack itemStack, int change) {
        logItemChange(itemStack.getType(), change);
    }

    private void logItemChange(Material material, int change) {
        ItemChange itemChange = new ItemChange();
        itemChange.name = material.name();
        itemChange.change = change;
        MarketCraft.changeBuffer.add(itemChange);

        MarketCraft.server.getLogger().info(ChatColor.GREEN + (change > 0 ? "+" : "") + change + " " + material.name());
    }
}
