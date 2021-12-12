package com.brokkandeitri.marketcraft;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListenerPriceHistory implements Listener{
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Price history") || event.getCurrentItem() == null) {
            return;
        }
        PriceHistoryEvent(event);

        event.setCancelled(true);
    }

    private void PriceHistoryEvent(InventoryClickEvent event) {
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        assert clickedItem != null;

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem().getLore() != null) {
            switch (Objects.requireNonNull(clickedItem.getItemMeta().getLore()).get(0)) {
                case "Decrease time":
                case "Increase time":
                    priceHistoryTimeEvent(event, player, clickedItem);
                    return;
                case "Confirm":
                    PriceHistoryConfirmEvent(event, player);
                    return;
                case "Exit":
                    player.closeInventory();
                    return;
                default:
            }
        } else {
            Inventory priceHistoryInput = player.getOpenInventory().getTopInventory();
            priceHistoryInput.setItem(GUIBuilder.InvPos.MID, event.getCurrentItem());
        }
    }

    private void PriceHistoryConfirmEvent(InventoryClickEvent event, Player player) {
        Inventory inventory = event.getClickedInventory();
        assert inventory != null;

        int time = PriceHistoryPriceEvent(inventory);

        if (inventory.getItem(GUIBuilder.InvPos.MID) == null){
            return;
        }
        ItemStack selected = Objects.requireNonNull(inventory.getItem(GUIBuilder.InvPos.MID));

        inventory.setItem(GUIBuilder.InvPos.MID, null);

        List<Integer> fullSample = MarketCraft.files.priceHistory.getStringList(selected.getType().toString()).stream().map(Integer::parseInt).collect(Collectors.toList());
        time = PriceHistorySampleSize(fullSample, time, player);
        if (time == 0) { return; }
        List<Integer> sample = fullSample.subList(0, time);
        float mean = PriceHistoryMean(sample);
        float median = PriceHistoryMedian(sample);
        int mode = PriceHistoryMode(sample);
        int current = MarketCraft.getPrice(selected);
        int max = PriceHistoryMax(sample);
        int min = PriceHistoryMin(sample);
        CommandPrice.DisplayStats(player, mean, median, mode, current, max, min, selected);
    }

    private int PriceHistoryMin(List<Integer> sample) {
        int minPrice = sample.get(0);

        for (int point : sample) {
            minPrice = Math.min(minPrice, point);
        }
        return minPrice;
    }

    private int PriceHistoryMax(List<Integer> sample) {
        int maxPrice = 0;
        for (int point : sample) {
            maxPrice = Math.max(maxPrice, point);
        }
        return maxPrice;
    }

    private Integer PriceHistoryMode(List<Integer> sample) {
        return sample.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
    }

    private float PriceHistoryMedian(List<Integer> sample) {
        float index = ((float) sample.size() + 1) / 2 - 1;

        Collections.sort(sample);

        int indexHigh = sample.get((int) (index + 0.5));
        int indexLow = sample.get((int) (index - 0.5));

        return ((float) indexHigh + (float) indexLow) / (float) 2;
    }

    private float PriceHistoryMean(List<Integer> sample) {
        int count = 0;

        for (Integer integer : sample) {
            count += integer;
        }

        return (float) count / (float) sample.size();
    }

    private int PriceHistorySampleSize(List<Integer> sample, int time, Player player) {
        if (sample.size() < time){
            player.sendMessage(ChatColor.RED + "Sample size of " + time + " cannot be used as there isn't enough data using " + sample.size() + " instead");
            time = sample.size();
        }

        return  time;
    }

    private int PriceHistoryPriceEvent(Inventory inventory) {
        int time = 0;
        if (!Objects.requireNonNull(inventory.getItem(GUIBuilder.InvPos.BOT_MID)).getItemMeta().getDisplayName().equals("Click an item to select to view price history")) {
            time = Integer.parseInt(Objects.requireNonNull(inventory.getItem(GUIBuilder.InvPos.BOT_MID)).getItemMeta().getDisplayName().replaceAll("[^\\d.]", ""));
        }
        return time;
    }

    private void priceHistoryTimeEvent(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        Inventory inventory = event.getClickedInventory();
        assert inventory != null;

        int time = PriceHistoryPriceEvent(inventory);
        if (clickedItem.getItemMeta().getDisplayName().contains("Increase")) {
            time += clickedItem.getAmount();
        } else if (time > 0){
            time -= clickedItem.getAmount();
        }

        priceHistorySwapEvent(inventory, time);

        player.openInventory(inventory);
    }

    private void priceHistorySwapEvent(Inventory inventory, int time) {
        GUIItem item;
        item = new GUIItem();
        item.name = "See stats for " + time + " days";
        item.lore.add(Component.text("Confirm"));
        item.amount = 1;
        item.material = Material.LIME_DYE;
        inventory.setItem(GUIBuilder.InvPos.BOT_MID, item.getItemStack());
    }
}
