package com.brokkandeitri.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerFishEvent;
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
    private void BrewEvent(BrewEvent event) {
        event.getContents();
        MarketCraft.server.getLogger().warning(event.getContents().toString());
    }

    @EventHandler
    private void  BrewFuel(BrewingStandFuelEvent event) {
        logItemChange(event.getFuel(), -1);
    }

    @EventHandler
    private void FurnaceFuel(FurnaceBurnEvent event) {
        logItemChange(event.getFuel(), -1);
    }



    @EventHandler
    private void FishingCatchEvent(PlayerFishEvent event) {
        if (event.getCaught() == null) {
            return;
        }
        assert event.getCaught() != null;
        String itemName = event.getCaught().getName();

        if (Material.matchMaterial(itemName) == null) {
            return;
        }
        assert Material.matchMaterial(itemName) != null;
        Material material = Material.matchMaterial(itemName);
        logItemChange(material, 1);
    }

    @EventHandler
    private void EatFoodEvent(FoodLevelChangeEvent event) {
        ItemStack item = event.getItem();
        if (item != null) {
            logItemChange(item, -1);
        }
    }

    @EventHandler
    private void InventoryInteractEvent(InventoryClickEvent event) {
        int amount;

        if (event.getClickedInventory() == null) {
            return;
        }

        InventoryType inventoryType = Objects.requireNonNull(event.getClickedInventory()).getType();
        if (!inventoryType.equals(InventoryType.STONECUTTER)) {
            return;
        }

        if (event.getRawSlot() == 1) {
            if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
                amount = Objects.requireNonNull(event.getInventory().getItem(0)).getAmount();
            } else {
                amount = 1;
            }

            logItemChange(Objects.requireNonNull(event.getInventory().getItem(0)), -amount);
            logItemChange(Objects.requireNonNull(event.getInventory().getItem(1)), amount);
        }
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

        for (Item item : event.getItems()) {
            logItemChange(item.getItemStack());
        }
    }

    @EventHandler
    private void PlayerHarvestBlockEvent(PlayerHarvestBlockEvent event) {
        for (ItemStack item : event.getItemsHarvested()) {
            logItemChange(item.getType(), item.getAmount());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler
    private void CraftItemEvent(CraftItemEvent event) {
        ItemStack product = event.getInventory().getResult();

        int crafted = 1;

        if (event.getClick().isShiftClick()) {
            crafted = 64;
            for (ItemStack itemStack : event.getInventory().getMatrix()) {
                crafted = Math.min(itemStack == null ? 64 : itemStack.getAmount(), crafted);
            }
        }

        logItemChange(product, crafted * product.getAmount());
        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            if (itemStack != null) {
                logItemChange(itemStack.getType(), -crafted);
            }
        }
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
        if (event.getEntity().getType() == EntityType.PLAYER) {
            return;
        }

        for (ItemStack item: event.getDrops()) {
            logItemChange(item.getType(), item.getAmount());
        }
    }

    @EventHandler
    private void BlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            logItemChange(event.getItemInHand(), -1);
        }
    }

    private void logItemChange(ItemStack itemStack) {
        logItemChange(itemStack.getType(), itemStack.getAmount());
    }

    private void logItemChange(ItemStack itemStack, int change) {
        logItemChange(itemStack.getType(), change);
    }

    private void logItemChange(Material material, int change) {
        if (!MarketCraft.files.config.getBool("ALLOW_NEGATIVE_COUNTS") && change < 0) {
            return;
        }

        ItemChange itemChange = new ItemChange();
        itemChange.name = material.name();
        itemChange.change = change;

        MarketCraft.logItemChange(itemChange);

        MarketCraft.server.getLogger().info(ChatColor.GREEN + (change > 0 ? "+" : "") + change + " " + material.name());
    }
}
