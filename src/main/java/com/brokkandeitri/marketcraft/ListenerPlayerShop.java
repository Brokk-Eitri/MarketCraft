package com.brokkandeitri.marketcraft;

import com.brokkandeitri.marketcraft.GUIBuilder.InvPos;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ListenerPlayerShop implements Listener {
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Auction - Buy") || event.getCurrentItem() == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem().getLore() == null) {
            ItemStack item = event.getCurrentItem();
            String name = item.getI18NDisplayName();
            Player player = (Player) event.getWhoClicked();
            player.sendMessage(ChatColor.RED + "Cannot add " + name + " to the Auction via this menu.");
            return;
        }

        playerShopEvent(event);
    }

    private void playerShopEvent(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        assert clickedItem != null;

        Inventory inventory = event.getClickedInventory();
        assert inventory != null;

        Player player = (Player) event.getWhoClicked();

        int page;
        Scanner in = new Scanner(Objects.requireNonNull(inventory.getItem(InvPos.TOP_MID).getItemMeta().getDisplayName())).useDelimiter("[^0-9]+");
        page = in.nextInt();

        switch (Objects.requireNonNull(clickedItem.getItemMeta().getLore()).get(0)) {
            case "Choose page":
                playerShopChooseEvent(clickedItem, player, page);
                break;

            case "Cancel":
                playerShopCancelEvent(inventory);
                break;
            case "Confirm":
                if (inventory.getItem(49) == null){
                    player.sendMessage(ChatColor.RED + " No items selected please select an item");
                    return;
                }


                int price = Integer.parseInt(String.valueOf(MarketCraft.files.playerShop.get(getPosition(inventory) + ".price")));
                int cost = (int) Math.ceil(price * 1.05);
                Player receiver = MarketCraft.server.getPlayer(String.valueOf(MarketCraft.files.playerShop.get(getPosition(inventory) + ".seller")));

                String playerBalanceKey = "players." + player.getUniqueId().toString() + ".balances";
                String receiverBalanceKey = "players." + UUID.fromString(String.valueOf(MarketCraft.files.playerShop.get(getPosition(inventory) + ".uid"))).toString() + ".balances";

                if (!MarketCraft.files.balances.contains(playerBalanceKey)) {
                    MarketCraft.files.balances.set(playerBalanceKey, 0);
                }
                if (!MarketCraft.files.balances.contains(receiverBalanceKey)) {
                    MarketCraft.files.balances.set(playerBalanceKey, 0);
                }

                int balances = (int) MarketCraft.files.balances.get(playerBalanceKey);
                Inventory playersInv = player.getOpenInventory().getBottomInventory();

                ItemStack selectedItem = playerShopItemEvent(inventory);


                if (player.getInventory().firstEmpty() == -1){
                    player.sendMessage(ChatColor.RED + "Not enough inventory room for this item.");
                    return;
                }

                if (!(balances >= cost)){
                    player.sendMessage(ChatColor.RED + "Not enough money to buy this item (Cost: £" + cost + ").");
                    return;
                }

                playerShopSellEvent(inventory, playersInv,selectedItem, price, playerBalanceKey, receiverBalanceKey, getPosition(inventory), cost);
                assert receiver != null;
                playerPayEvent(receiver, player, selectedItem, price, cost);

                playerShopCancelEvent(inventory);
                break;
            default:
                if (!clickedItem.getLore().get(0).equals("Current page")){
                    playerShopSelectEvent(event, inventory, clickedItem, page);
                    break;
                }
        }
    }

    private int getPosition(Inventory inventory) {
        ArrayList<String> itemLore = (ArrayList<String>) Objects.requireNonNull(inventory.getItem(49)).getLore();
        assert itemLore != null;
        return Integer.parseInt(itemLore.get(0));
    }

    private void playerPayEvent(Player receiver, Player player, ItemStack selectedItem, int price, int cost) {
        receiver.sendMessage(ChatColor.GOLD + "You have sold " + selectedItem.getI18NDisplayName() + " for: £" + price);
        player.sendMessage(ChatColor.GOLD + "You have Bought " + selectedItem.getI18NDisplayName() + " for: £" + cost);
    }

    private void playerShopSellEvent(Inventory inventory, Inventory playersInv, ItemStack selectedItem, int price, String playerBalanceKey, String receiverBalanceKey, int position, int cost) {
        playersInv.addItem(selectedItem);

        int balances = (int) MarketCraft.files.balances.get(playerBalanceKey);
        balances -= cost;
        MarketCraft.files.balances.set(playerBalanceKey, balances);

        int receiverBalance = (int) MarketCraft.files.balances.get(receiverBalanceKey);
        receiverBalance += price;
        MarketCraft.files.balances.set(receiverBalanceKey, receiverBalance);

        inventory.clear(position + 9);

        List<String> uids = MarketCraft.files.playerShop.getStringList("uid");
        int size = uids.size() -1 ;
        uids.remove(size);
        int counter = 0;
        for (Object i : uids){
            if (counter == position){
                MarketCraft.files.playerShop.set(String.valueOf(i) , MarketCraft.files.playerShop.get(String.valueOf(size)));
            }
            counter += 1;
        }
        MarketCraft.files.playerShop.set(String.valueOf(size),null);
        MarketCraft.files.playerShop.set("uid",uids);
    }

    private ItemStack playerShopItemEvent(Inventory inventory) {
        ItemStack selectedItemInShop = Objects.requireNonNull(inventory.getItem(49));
        Material material = selectedItemInShop.getType();
        int amount = selectedItemInShop.getAmount();
        ItemStack selectedItem = new ItemStack(material , amount);
        selectedItem.addEnchantments(selectedItemInShop.getEnchantments());
        selectedItem.setDurability(selectedItemInShop.getDurability());
        return selectedItem;
    }

    private void playerShopSelectEvent(InventoryClickEvent event, Inventory inventory, ItemStack clickedItem, int page) {
        ArrayList<String> lore = new ArrayList<>();
        ArrayList<String> position = new ArrayList<>();
        position.add(String.valueOf(event.getRawSlot() - 9 + page));
        inventory.setItem(49, clickedItem);
        ItemStack item = Objects.requireNonNull(inventory.getItem(49));
        item.setLore(position);
        inventory.setItem(49, item);
        ItemStack limeDye = new ItemStack(Material.LIME_DYE, 1);
        lore.add("Confirm");
        limeDye.setLore(lore);
        inventory.setItem(50, limeDye);
    }

    private void playerShopChooseEvent(ItemStack clickedItem, Player player, int page) {
        if (Objects.equals(clickedItem.getItemMeta().getDisplayName(), "Next page")) {
            page += 1;
        } else if (Objects.equals(clickedItem.getItemMeta().getDisplayName(), "Previous page") && page > 0){
            page -= 1;
        }
        CommandPlayerShop.openPlayerShop(player, page);

    }

    private void playerShopCancelEvent(Inventory inventory) {
        inventory.clear(49);
        ItemStack grayDye = new ItemStack(Material.GRAY_DYE, 1);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Confirm");
        grayDye.setLore(lore);
        inventory.setItem(50,grayDye);
    }
}