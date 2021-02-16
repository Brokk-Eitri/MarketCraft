package com.kitdacatsun.marketcraft;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GuiListener implements Listener {


    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (event.getView().getBottomInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        if (event.getView().getTitle().equals("Shop")) {
            try {
                if (Objects.requireNonNull(event.getCurrentItem()).getLore() != null) {
                    shopEvent(event);
                } else {
                    switchView(event);
                }
            } catch (NullPointerException ignored) {
            }
        }
    }

    public void shopEvent(InventoryClickEvent event) {
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        assert clickedItem != null;

        Player player = (Player) event.getWhoClicked();

        switch (Objects.requireNonNull(clickedItem.getItemMeta().getLore()).get(0)) {
            case "Buy":
            case "Sell":
                Inventory inventory = event.getClickedInventory();
                assert inventory != null;

                GUIItem item;

                if (inventory.getItem(GUIBuilder.BOT_MID) != null) {
                    item = new GUIItem();
                    item.name = clickedItem.getItemMeta().getDisplayName();
                    item.lore = "Confirm";
                    item.amount = 1;
                    item.material = Material.LIME_DYE;
                    inventory.setItem(GUIBuilder.BOT_MID, item.getItemStack());
                }


                // Selected item
                item = new GUIItem();
                item.material = Objects.requireNonNull(inventory.getItem(GUIBuilder.MID)).getType();
                item.amount = clickedItem.getAmount();
                item.lore = "Selected Item";
                inventory.setItem(GUIBuilder.MID, item.getItemStack());

                player.openInventory(inventory);

                return;
            case "Back":
                // Go back
                return;
            case "Confirm":
                // Do order
                return;
            default:
        }

    }

    public void switchView(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        Inventory shop = player.getOpenInventory().getTopInventory();

        shop.setItem(GUIBuilder.MID, event.getCurrentItem());
    }
}




