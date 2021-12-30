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

import java.io.FileNotFoundException;
import java.util.*;

public class ListenerPriceHistory implements Listener{
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Price History") || event.getCurrentItem() == null) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        assert clickedItem != null;

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem().lore() != null) {
            switch (Objects.requireNonNull(clickedItem.getItemMeta().getLore()).get(0)) {
                case "Decrease time":
                case "Increase time":
                    changeTimeFrame(event, player, clickedItem);
                    return;
                case "Confirm":
                    priceHistoryConfirmEvent(event, player);
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

    private void priceHistoryConfirmEvent(InventoryClickEvent event, Player player) {
        Inventory inventory = event.getClickedInventory();
        assert inventory != null;

        if (inventory.getItem(GUIBuilder.InvPos.MID) == null){
            return;
        }

        ItemStack item = Objects.requireNonNull(inventory.getItem(GUIBuilder.InvPos.MID));
        String itemName = item.getType().name();

        inventory.setItem(GUIBuilder.InvPos.MID, null);

        List<DataPoint> data = new ArrayList<>();

        int timeFrame = getTimeFrame(inventory);
        if (timeFrame == 0) { return; }

        try {
            Scanner scanner = new Scanner(MarketCraft.files.priceHistory);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split(",");


                if (split[1].equals(itemName)) {
                    DataPoint dataPoint = new DataPoint(line);
                    data.add(dataPoint);

                    if (dataPoint.time > (System.currentTimeMillis() / 1000L) + (timeFrame * 86400L)) {
                        break;
                    }
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            MarketCraft.server.getLogger().warning(e.getMessage());
        }

        if (data.size() == 0) {
            player.sendMessage(ChatColor.RED + "No data for that item");
            return;
        }

        // TODO: Validate History Ranges

        HistoryStats stats = new HistoryStats(data);
        player.sendMessage(ChatColor.GOLD + "Stats For: " + itemName + " (" + data.size() + " data points)");
        player.sendMessage(ChatColor.GOLD + "Current Price: £" + stats.current);
        player.sendMessage(ChatColor.GOLD + "Mean Price: £" + stats.colour(stats.mean) + stats.mean);
        player.sendMessage(ChatColor.GOLD + "Median Price: £" + stats.colour(stats.median) + stats.median);
        player.sendMessage(ChatColor.GOLD + "Modal Price: £" + stats.colour(stats.mode) + stats.mode);
        player.sendMessage(ChatColor.GOLD + "Range: £" + (stats.max - stats.min) + " (£" + stats.min + " to £" + stats.max + ")");
        player.sendMessage(ChatColor.GOLD + "(" + ChatColor.RED + "lower " + ChatColor.YELLOW + "equal " + ChatColor.GREEN + "higher" + ChatColor.GOLD + " than current)");

        player.getOpenInventory().close();
    }

    private int getTimeFrame(Inventory inventory) {
        int time = 0;

        String itemName = Objects.requireNonNull(inventory.getItem(GUIBuilder.InvPos.BOT_MID)).getItemMeta().getDisplayName();

        if (!itemName.equals("Click an item to select to view price history")) {
            time = Integer.parseInt(itemName.replaceAll("[^\\d]", ""));
        }

        return time;
    }

    private void changeTimeFrame(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        Inventory inventory = event.getClickedInventory();
        assert inventory != null;

        int time = getTimeFrame(inventory);
        if (clickedItem.getItemMeta().getDisplayName().contains("Increase")) {
            time += clickedItem.getAmount();
        } else if (time > 0){
            time -= clickedItem.getAmount();
        }

        GUIItem item = new GUIItem();
        item.name = "See stats for " + time + " days";
        item.lore.add(Component.text("Confirm"));
        item.amount = 1;
        item.material = Material.LIME_DYE;

        inventory.setItem(GUIBuilder.InvPos.BOT_MID, item.getItemStack());

        player.openInventory(inventory);
    }
}

class HistoryStats {
    float mean;
    float median;
    int mode;
    int current;
    int max;
    int min;

    HistoryStats(List<DataPoint> dataPoints) {
        current = dataPoints.get(dataPoints.size() - 1).price;
        min = dataPoints.get(0).price;

        float total = 0;

        List<Integer> prices = new ArrayList<>();

        for (DataPoint point : dataPoints) {
            if (point.price > max) {
                max = point.price;
            } else if (point.price < min) {
                min = point.price;
            }

            total += point.price;

            prices.add(point.price);
        }

        mean = total / dataPoints.size();

        Collections.sort(prices);
        mode = mode(prices);
        median = median(prices);
    }

    ChatColor colour(float v) {
        if (v > current) {
            return ChatColor.GREEN;
        } else if (v < current) {
            return ChatColor.RED;
        }

        return ChatColor.YELLOW;
    }

    private Integer mode(List<Integer> prices) {
        Set<Integer> unique = new LinkedHashSet<>(prices);
        int mode = prices.get(0);

        for (int u : unique) {
            int f = Collections.frequency(prices, u);
            if (f > mode) {
                mode = u;
            }
        }

        return mode;
    }

    private float median(List<Integer> prices) {
        float midpoint = prices.size() / 2f;

        int floored = (int) Math.floor(midpoint);
        if (midpoint % 1 == 0) {
            return (prices.get(floored) + prices.get(floored)) / 2f;
        } else {
            return prices.get((int) Math.ceil(midpoint));
        }
    }
}

class DataPoint {
    int time;
    int price;
    int count;

    DataPoint(String csv) {
        String[] split = csv.split(",");
        time = Integer.parseInt(split[0]);
        price = Integer.parseInt(split[2]);
        count = Integer.parseInt(split[3]);
    }
}
