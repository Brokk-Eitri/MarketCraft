package com.brokkandeitri.marketcraft;

import com.brokkandeitri.marketcraft.GUIBuilder.InvPos;
import com.brokkandeitri.marketcraft.MarketCraft.files;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class ListenerShop implements Listener {

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Shop |") || event.getCurrentItem() == null || event.getClickedInventory() == null) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        Inventory botInventory = player.getOpenInventory().getBottomInventory();

        if (event.getClickedInventory().equals(botInventory)) {
            ItemStack item = event.getCurrentItem();
            new CommandShopMenu().openShop(item, player, item.getI18NDisplayName());
            return;
        }

        List<String> itemLore = Objects.requireNonNull(clickedItem.getItemMeta().getLore());
        int cost = MarketCraft.getPrice(Objects.requireNonNull(topInventory.getItem(InvPos.MID)));

        switch (itemLore.get(0)) {
            case "Buy":
                cost = (int) Math.ceil(cost * 1.05);
                changeOrder(player, clickedItem, topInventory, cost);
                return;
                
            case "Sell":
                changeOrder(player, clickedItem, topInventory, cost);
                return;

            case "Confirm":
                doOrder(player, topInventory, botInventory, ((TextComponent)(clickedItem.getItemMeta().displayName())).content().split(" ")[0]);
                return;

            case "Back":
            case "Return to Previous Menu":
                openPrevious(player);
                return;

            default:
        }
    }

    private void changeOrder(Player player, ItemStack option, Inventory inventory, int cost) {
        ItemStack order = inventory.getItem(InvPos.MID);
        if (order == null) {
            player.sendMessage(ChatColor.RED + "Order is null select an order to continue");
            return;
        }

        GUIItem item;

        item = new GUIItem();
        item.name = option.getItemMeta().getDisplayName() + " for £" + cost * option.getAmount();
        item.lore.add(Component.text("Confirm"));
        item.amount = 1;
        item.material = Material.LIME_DYE;
        inventory.setItem(InvPos.BOT_MID, item.getItemStack());

        item = new GUIItem();
        item.material = Objects.requireNonNull(inventory.getItem(InvPos.MID)).getType();
        item.amount = option.getAmount();
        inventory.setItem(InvPos.MID, item.getItemStack());

        player.openInventory(inventory);
    }

    private void doOrder(Player player, Inventory shopInv, Inventory playerInv, String type) {
        Material orderMaterial = Objects.requireNonNull(shopInv.getItem(InvPos.MID)).getType();
        int orderAmount = Objects.requireNonNull(shopInv.getItem(InvPos.MID)).getAmount();
        ItemStack order = new ItemStack(orderMaterial, orderAmount);

        String balanceKey = "players." + player.getUniqueId() + ".balance";
        int balances = files.balances.getInt(balanceKey);
        int cost = MarketCraft.getPrice(order) * order.getAmount();

        switch (type) {
            case "Sell":
                if (!playerInv.containsAtLeast(order, order.getAmount())) {
                    player.sendMessage(ChatColor.RED + "Not enough of that item type to sell");
                    return;
                }

                playerInv.removeItemAnySlot(order);
                logItemChange(orderMaterial, 10 * orderAmount);

                files.balances.set(balanceKey, balances + cost);

                player.sendMessage(ChatColor.GOLD + "You have sold " + order.getAmount() + " " + order.getI18NDisplayName() + " for £" + cost);

                return;


            case "Buy":
                cost = (int) Math.ceil(cost * 1.05);
                if (player.getInventory().firstEmpty() != -1) {

                    if (balances >= cost) {

                        playerInv.addItem(order);
                        logItemChange(orderMaterial, -1 * 10 * orderAmount);

                        files.balances.set(balanceKey, balances - cost);

                        player.sendMessage(ChatColor.GOLD + "You have bought " + order.getAmount() + " " + order.getI18NDisplayName() + " for £" + cost);

                    } else {
                        player.sendMessage(ChatColor.RED + "Not enough money to buy this item (Cost: £" + cost + ").");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Not enough inventory room for this item.");
                }

                return;

            default:
                player.sendMessage(ChatColor.RED + "Something went wrong: " + type + " please try again");
        }
    }

    private void openPrevious(Player player) {
        if (files.shop.contains(player.getUniqueId().toString())){
            String name = files.shop.getString(player.getUniqueId().toString());
            if (name.equals(player.getOpenInventory().getTitle())){
                return;
            }
            name = name.substring(12);
            if (name.equals("Shop menu")){
                name = "root";
            }
            new CommandShopMenu().doMenu(name, player, name);
            files.shop.set(player.getUniqueId().toString(), player.getOpenInventory().getTitle());
        } else {
            player.sendMessage(ChatColor.RED + "No menu to go to");
        }
    }

    private void logItemChange(Material material, int change) {

        ItemChange itemChange = new ItemChange();
        itemChange.name = material.name();
        itemChange.change = change;

        MarketCraft.logItemChange(itemChange);

        MarketCraft.server.getLogger().info(ChatColor.GREEN + (change > 0 ? "+" : "") + change + " " + material.name());
    }
}
