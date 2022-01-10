package com.brokkandeitri.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
            for (Item item : event.getItems()) {
                if (item != lastItem) {
                    logItemChange(item.getItemStack().getType(), -1 * item.getItemStack().getAmount());
                }
            }
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
    }

    @EventHandler
    private void DropItemEvent(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        logItemChange(item.getItemStack().getType(), -1 * item.getItemStack().getAmount());
    }

    @EventHandler
    private void PickupItemEvent(EntityPickupItemEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) {
            return;
        }
        logItemChange(event.getItem().getItemStack());
    }


//    @EventHandler
//    private void BrewEvent(BrewEvent event) {
//        event.getContents();
//        MarketCraft.server.getLogger().warning(event.getContents().toString());
//    }
//
//    @EventHandler
//    private void  BrewFuel(BrewingStandFuelEvent event) {
//        logItemChange(event.getFuel(), -1);
//    }
//
//    @EventHandler
//    private void FurnaceFuel(FurnaceBurnEvent event) {
//        logItemChange(event.getFuel(), -1);
//    }



//    @EventHandler
//    private void FishingCatchEvent(PlayerFishEvent event) {
//        event.
//    }




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
