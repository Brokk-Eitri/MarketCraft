package com.brokkandeitri.marketcraft;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ListenerPlayerShopAdd implements Listener {
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Auction - Sell") || event.getCurrentItem() == null) {
            return;
        }
        addPlayerShopEvent(event);

        event.setCancelled(true);
    }

    private void addPlayerShopEvent(InventoryClickEvent event) {
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        assert clickedItem != null;

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem().getLore() != null) {
            switch (Objects.requireNonNull(clickedItem.getItemMeta().getLore()).get(0)) {
                case "Decrease price":
                case "Increase price":
                    playerShopAddEvent(event, player, clickedItem);
                    return;
                case "Confirm":
                    playerShopConfirmEvent(event, player);
                    return;
                case "Exit":
                    player.closeInventory();
                    return;
                default:
            }
        } else {
            Inventory shop = player.getOpenInventory().getTopInventory();
            shop.setItem(GUIBuilder.InvPos.MID, event.getCurrentItem());
        }

    }

    private void playerShopAddEvent(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        Inventory inventory = event.getClickedInventory();
        assert inventory != null;

        int price = playerShopPriceEvent(inventory);
        if (clickedItem.getItemMeta().getDisplayName().contains("Increase")) {
            price += clickedItem.getAmount();
        } else if (price > 0){
            price -= clickedItem.getAmount();
        }

        playerShopSwapEvent(inventory, price);


        player.openInventory(inventory);
    }

    private void playerShopSwapEvent(Inventory inventory, int price) {
        GUIItem item;
        item = new GUIItem();
        item.name = "Add to shop for £" + price;
        item.lore.add(Component.text("Confirm"));
        item.amount = 1;
        item.material = Material.LIME_DYE;
        inventory.setItem(GUIBuilder.InvPos.BOT_MID, item.getItemStack());
    }

    private int playerShopPriceEvent(Inventory inventory) {
        int price = 0;
        if (!Objects.requireNonNull(inventory.getItem(GUIBuilder.InvPos.BOT_MID)).getItemMeta().getDisplayName().equals("Select an option")) {
            price = Integer.parseInt(Objects.requireNonNull(inventory.getItem(GUIBuilder.InvPos.BOT_MID)).getItemMeta().getDisplayName().replaceAll("[^\\d.]", ""));
        }
        return price;
    }

    private void playerShopConfirmEvent(InventoryClickEvent event, Player player) {
        Inventory inventory = event.getClickedInventory();
        Inventory playerInventory = event.getView().getBottomInventory();
        assert inventory != null;

        int price = playerShopPriceEvent(inventory);

        if (inventory.getItem(GUIBuilder.InvPos.MID) == null){
            return;
        }
        ItemStack selected = Objects.requireNonNull(inventory.getItem(GUIBuilder.InvPos.MID));
        playerInventory.removeItemAnySlot(selected);

        inventory.setItem(GUIBuilder.InvPos.MID, null);

        CommandPlayerShop.addItem(player, selected, price);
    }
}
