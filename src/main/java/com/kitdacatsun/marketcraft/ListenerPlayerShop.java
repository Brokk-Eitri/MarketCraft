package com.kitdacatsun.marketcraft;

import com.kitdacatsun.marketcraft.GUIBuilder.InvPos;
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
        if (!event.getView().getTitle().contains("Player Shop") || event.getCurrentItem() == null) {
            return;
        }

        if (event.getCurrentItem().getLore() == null) {
            return;
        }

        event.setCancelled(true);

        playerShopEvent(event);
    }

    private void playerShopEvent(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        assert clickedItem != null;

        Inventory inventory = event.getClickedInventory();
        assert inventory != null;

        Player player = (Player) event.getWhoClicked();

        int page;
        Scanner in = new Scanner(Objects.requireNonNull(inventory.getItem(InvPos.TOP_MID)).getItemMeta().getDisplayName()).useDelimiter("[^0-9]+");
        page = in.nextInt();

        switch (Objects.requireNonNull(clickedItem.getItemMeta().getLore()).get(0)) {
            case "Choose page":
                playerShopChooseEvent(clickedItem, player, page);
                break;

            case "Cancel":
                playerShopCancelEvent(inventory);
                break;
            case "Confirm":
                if (inventory.getItem(49) != null){
                    ArrayList<String> itemLore = (ArrayList<String>) Objects.requireNonNull(inventory.getItem(49)).getLore();
                    assert itemLore != null;
                    int position = Integer.parseInt(itemLore.get(0));
                    int price = Integer.parseInt(String.valueOf(MarketCraft.files.playerShop.get(position + ".price")));
                    String seller = String.valueOf(MarketCraft.files.playerShop.get(position + ".seller"));

                    UUID Uuid = player.getUniqueId();
                    UUID receiverUuid = UUID.fromString(String.valueOf(MarketCraft.files.playerShop.get(position + ".uid")));
                    String playerBalanceKey = "Players." + Uuid.toString() + ".balance";
                    String receiverBalanceKey = "Players." + receiverUuid.toString() + ".balance";

                    Player receiver = MarketCraft.server.getPlayer(seller);

                    int balance = (int) MarketCraft.files.balance.get(playerBalanceKey);
                    int receiverBalance = (int) MarketCraft.files.balance.get(receiverBalanceKey);
                    Inventory playersInv = player.getOpenInventory().getBottomInventory();

                    ItemStack selectedItem = playerShopItemEvent(inventory);

                    //checking for room in their inventory
                    if (!(player.getInventory().firstEmpty() == -1)){

                        //checking if the player has enough money
                        if (balance >= price) {

                            playerShopSellEvent(inventory, playersInv,selectedItem, balance, receiverBalance, price, playerBalanceKey, receiverBalanceKey, position);

                            assert receiver != null;
                            receiver.sendMessage(ChatColor.GOLD + "You have sold " + selectedItem.getI18NDisplayName() + " for: £" + price);
                            player.sendMessage(ChatColor.GOLD + "You have Bought " + selectedItem.getI18NDisplayName() + " for: £" + price);

                        } else {
                            player.sendMessage(ChatColor.RED + "Not enough money to buy this item.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Not enough inventory room for this item.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + " No items selected please select an item");
                }
                playerShopCancelEvent(inventory);
                break;
            default:
                if (!clickedItem.getLore().get(0).equals("Current page")){
                    playerShopSelectEvent(event, inventory, clickedItem, page);
                    break;
                }
        }
    }

    private void playerShopSellEvent(Inventory inventory, Inventory playersInv, ItemStack selectedItem, int balance, int receiverBalance, int price, String playerBalanceKey, String receiverBalanceKey, int position) {
        playersInv.addItem(selectedItem);

        balance -= price;
        receiverBalance += price;
        MarketCraft.files.balance.set(playerBalanceKey, balance);
        MarketCraft.files.balance.set(receiverBalanceKey, receiverBalance);
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
        if (Objects.equals(clickedItem.getI18NDisplayName(), "Next page")) {
            page += 1;
            CommandPlayerShop.openPlayerShop(player, page);
        } else if (Objects.equals(clickedItem.getI18NDisplayName(), "Previous page") && page > 0){
            page -= 1;
            CommandPlayerShop.openPlayerShop(player, page);
        }
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
