package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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
                    switchItem(event);
                }
            } catch (NullPointerException ignored) { }
        } else if (event.getView().getTitle().equals("Shop Menu")) {
            shopMenu(event);
        } else if (event.getView().getTitle().equals("Player Shop")){
            try {
                if (Objects.requireNonNull(event.getCurrentItem()).getLore() != null) {
                    playerShopEvent(event);
                } else {
                    switchItem(event);
                }
            } catch (NullPointerException ignored) { }
        }
    }

    private void  playerShopEvent(InventoryClickEvent event){
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        assert clickedItem != null;

        Player player = (Player) event.getWhoClicked();

        switch (Objects.requireNonNull(clickedItem.getItemMeta().getLore()).get(0)) {
            case "Decrease price":
            case "Increase price":
                Inventory inventory = event.getClickedInventory();
                assert inventory != null;

                GUIItem item;
                int price;

                if (Objects.requireNonNull(inventory.getItem(GUIBuilder.BOT_MID)).getItemMeta().getDisplayName().equals("Select an option")){
                    price = 0;
                } else {
                    Scanner in = new Scanner(Objects.requireNonNull(inventory.getItem(GUIBuilder.BOT_MID)).getItemMeta().getDisplayName()).useDelimiter("[^0-9]+");
                    price = in.nextInt();
                }

                if (clickedItem.getItemMeta().getDisplayName().contains("Increase")){
                    price +=  clickedItem.getAmount();
                } else {
                    price -=  clickedItem.getAmount();
                }


                item = new GUIItem();
                item.name = "Add to shop for £" + price ;
                item.lore = "Confirm";
                item.amount = 1;
                item.material = Material.LIME_DYE;
                inventory.setItem(GUIBuilder.BOT_MID, item.getItemStack());

                player.openInventory(inventory);

                return;
            case "Confirm":

                inventory = event.getClickedInventory();
                assert inventory != null;

                Scanner num = new Scanner(Objects.requireNonNull(inventory.getItem(GUIBuilder.BOT_MID)).getItemMeta().getDisplayName()).useDelimiter("[^0-9]+");
                price = num.nextInt();

                ItemStack selected = Objects.requireNonNull(inventory.getItem(GUIBuilder.MID));

                CommandPlayerShop.addItem(player, selected , price);



            case "Exit":
                player.closeInventory();
            default:
        }
    }

    private void shopEvent(InventoryClickEvent event) {
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

                ItemMeta clickedItemMeta = clickedItem.getItemMeta();

                Inventory shop = player.getOpenInventory().getTopInventory();
                Inventory playersInv = player.getOpenInventory().getBottomInventory();


                int cost = 10;


                if (clickedItemMeta.getLore().get(0).equals("Confirm") && !clickedItemMeta.getDisplayName().equals("Select an option") && shop.getItem(13) != null){

                    int saleAmount = Objects.requireNonNull(shop.getItem(13)).getAmount();

                    Material selectedItemMaterial = Objects.requireNonNull(shop.getItem(13)).getType();
                    ItemStack selectedItem = new ItemStack(selectedItemMaterial, saleAmount);

                    //Selling an item
                    if (clickedItemMeta.getDisplayName().contains("Sell")){

                        //checking if player has enough of the item type
                        if (playersInv.contains(selectedItem.getType(), saleAmount)){
                            playersInv.removeItemAnySlot(selectedItem);

                            UUID Uuid = player.getUniqueId();
                            String playerBalanceKey = "Players." + Uuid.toString() + ".balance";

                            int balance = (int) MarketCraft.balance.get(playerBalanceKey) + cost;
                            MarketCraft.balance.set(playerBalanceKey, balance);

                            player.sendMessage(ChatColor.GOLD + "You have sold " + saleAmount + " of " + selectedItem.getI18NDisplayName() + " for: £" + saleAmount * cost);
                        } else {
                            player.sendMessage(ChatColor.RED + "Not enough of that item type to sell");
                        }

                    //Buying an item
                    } else if (clickedItemMeta.getDisplayName().contains("Buy")){

                        //checking for room in their inventory
                        if (!(player.getInventory().firstEmpty() == -1)){

                            UUID Uuid = player.getUniqueId();
                            String playerBalanceKey = "Players." + Uuid.toString() + ".balance";
                            int balance = (int) MarketCraft.balance.get(playerBalanceKey);

                            //checking if the player has enough money
                            if (balance >= cost * saleAmount) {

                                playersInv.addItem(selectedItem);

                                balance = balance - cost * saleAmount;
                                MarketCraft.balance.set(playerBalanceKey, balance);

                                player.sendMessage(ChatColor.GOLD + "You have Bought " + saleAmount + " of " + selectedItem.getI18NDisplayName() + " for: £" + saleAmount * cost);

                            } else {
                                player.sendMessage(ChatColor.RED + "Not enough money to buy this item.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Not enough inventory room for this item.");
                        }
                    }
                }

                return;
            default:
        }

    }



    private void switchItem(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        Inventory shop = player.getOpenInventory().getTopInventory();

        shop.setItem(GUIBuilder.MID, event.getCurrentItem());
    }

    private void shopMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        CommandShopMenu commandShopMenu = new CommandShopMenu();
        commandShopMenu.doMenu(Objects.requireNonNull(event.getCurrentItem()).getType().name(), player);
    }


    //villager interface
    @EventHandler
    private void entityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(event.getRightClicked() instanceof Villager) {

            Villager villager = (Villager) event.getRightClicked();

            String name = Objects.requireNonNull(villager.getCustomName());

            if(name.equals("Bank") && villager.isInvulnerable()) {

                CommandShopMenu commandShopMenu = new CommandShopMenu();
                commandShopMenu.doMenu("",player);

            }
        }
    }

}