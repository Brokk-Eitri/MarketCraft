package com.kitdacatsun.marketcraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
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
            } catch (NullPointerException ignored) { }
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
                Inventory inventory = player.getInventory();

                GUIItem item;

                // Confirm item
                item = new GUIItem();
                item.name = clickedItem.getItemMeta().getDisplayName();
                item.lore = "Confirm";
                item.amount = 1;
                item.material = clickedItem.getType();
                inventory.setItem(GUIBuilder.BOT_MID, item.getItemStack());

                // Selected item
                item = new GUIItem();
                item.material = Objects.requireNonNull(inventory.getItem(GUIBuilder.MID)).getType();
                item.amount = clickedItem.getAmount();
                item.lore = "Selected Item";

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
        player.sendMessage("Clicked item in inventory");
    }
}




