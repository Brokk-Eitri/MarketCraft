package com.brokkandeitri.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;


public final class MarketCraft extends JavaPlugin {

    public static MarketCraft plugin;
    public static Server server;

    public static class files {
        public static YAMLFile itemCounts = new YAMLFile("itemCounts.yml");
        public static YAMLFile balances = new YAMLFile("playerBalances.yml");
        public static YAMLFile shop = new YAMLFile("shop.yml");
        public static YAMLFile playerShop = new YAMLFile("playerShop.yml");
        public static YAMLFile priceHistory = new YAMLFile("priceHistory.yml");
        public static YAMLFile config = new YAMLFile("config.yml");

        public static FileWriter priceHistoryCSV;
    }

    public static int getPrice(ItemStack item) {
        return itemPriceMap.getOrDefault(item.getType().name(), files.config.getInt("MAX_PRICE"));
    }

    private final static Map<String, Integer> itemCountMap = new HashMap<>();
    private final static Map<String, Integer> itemPriceMap = new HashMap<>();

    private static int priceHistoryUpdateTime;

    @Override
    public void onLoad() {
        plugin = this;
        server = plugin.getServer();
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabled");

        if (files.config.getBool("LOGGING")) {
            server.getLogger().setLevel(Level.INFO);
        } else {
            server.getLogger().setLevel(Level.WARNING);
        }

        SpawnVillagers();

        for (String key : files.itemCounts.getKeys(false)) {
            int count = files.itemCounts.getInt(key);
            if (count > 0) {
                itemCountMap.put(key, count);
            }
        }

        priceHistoryUpdateTime = files.config.getInt("PRICE_HISTORY_UPDATE_TIME");

        try {
            File file = new File(MarketCraft.plugin.getDataFolder(), "priceHistory.csv");

            if (file.createNewFile()) {
                files.priceHistoryCSV.write("time,item_name,price,count\n");
                server.getLogger().info("Created priceHistory.csv");
            }

            files.priceHistoryCSV = new FileWriter(file, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        displayPrices();

        server.getPluginManager().registerEvents(new ListenerItemChange(), this);
        server.getPluginManager().registerEvents(new ListenerPlayerShop(), this);
        server.getPluginManager().registerEvents(new ListenerShop(), this);
        server.getPluginManager().registerEvents(new ListenerShopMenu(), this);
        server.getPluginManager().registerEvents(new ListenerPlayerShopAdd(), this);
        server.getPluginManager().registerEvents(new ListenerVillagers(), this);
        server.getPluginManager().registerEvents(new ListenerPriceHistory(), this);

        Objects.requireNonNull(getCommand("villager")).setExecutor(new CommandVillager());
        Objects.requireNonNull(getCommand("balance")).setExecutor(new CommandBalance());
        Objects.requireNonNull(getCommand("shop")).setExecutor(new CommandShopMenu());
        Objects.requireNonNull(getCommand("pay")).setExecutor(new CommandPay());
        Objects.requireNonNull(getCommand("price")).setExecutor(new CommandPrice());
        Objects.requireNonNull(getCommand("playershop")).setExecutor(new CommandPlayerShop());
        Objects.requireNonNull(getCommand("ranking")).setExecutor(new CommandRanking());
    }


    @Override
    public void onDisable() {
        for (String item : itemCountMap.keySet()) {
            files.itemCounts.set(item, itemCountMap.get(item));
        }

        displayPrices();

        updatePrices();
        updatePriceHistory();

        try {
            files.priceHistoryCSV.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getLogger().info("Disabled");
    }

    public static void updatePriceHistory() {
        for (String key: itemPriceMap.keySet()){
            if (!files.priceHistory.contains(key)){
                files.priceHistory.set(key, "");
            }
        }

        for (String key: files.priceHistory.getKeys(true)){
            @NotNull List<String> prices = files.priceHistory.getStringList(key);
            int price = getPrice(new ItemStack(Material.valueOf(key)));
            prices.add(String.valueOf(price));
            files.priceHistory.set(key, prices);

            try {
                files.priceHistoryCSV.write(System.currentTimeMillis() / 1000L + "," + key + "," + price + "," + itemCountMap.get(key) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        server.getLogger().info(ChatColor.BLUE + "Price History Updated");
    }

    public static void updatePrices() {
        if (itemCountMap.size() == 0) {
            return;
        }

        double lowest = Integer.MAX_VALUE;
        double highest = Integer.MIN_VALUE;

        for (String key : itemCountMap.keySet()) {
            int value = itemCountMap.get(key);
            if (value < lowest) {
                lowest = value - 1;
            } else if (value > highest) {
                highest = value;
            }
        }

        double min = files.config.getDouble("MIN_PRICE");
        double max = files.config.getDouble("MAX_PRICE");

        for (String key: itemCountMap.keySet()) {
            double rarity = 1 - ((itemCountMap.get(key) - lowest) / (highest - lowest));
            int price = (int)(min + ((max - min) * rarity));
            itemPriceMap.put(key, price);
        }

        server.getLogger().info(ChatColor.BLUE + "Prices Updated");
    }

    public static void displayPrices() {
        server.getLogger().info(ChatColor.BLUE + "---------------< PRICES >---------------");

        for (String key : itemCountMap.keySet()) {
            server.getLogger().info(ChatColor.BLUE + key + ": " + " ".repeat(40 - key.length()) +
                    " £" + getPrice(new ItemStack(Material.valueOf(key))) + "\t(" + itemCountMap.get(key) + ")");
        }

        server.getLogger().info(ChatColor.BLUE + "----------------------------------------");
    }

    static int changesSinceLastUpdate = 0;
    public static void logItemChange(ItemChange itemChange) {
        itemCountMap.put(itemChange.name, itemChange.change + itemCountMap.getOrDefault(itemChange.name, 0));

        updatePrices();
        changesSinceLastUpdate++;

        if (changesSinceLastUpdate > priceHistoryUpdateTime) {
            updatePriceHistory();
        }
    }

    public void SpawnVillagers(){
        if (files.config.contains("VILLAGERS")) {
            List<String> villagers = files.config.getStringList("VILLAGERS");
            for (String item: villagers) {
                UUID uid = UUID.fromString(files.shop.getString(item));
                if (server.getEntity(uid) != null) {
                    Entity villager = server.getEntity(uid);
                    assert villager != null;
                    villager.getWorld();
                    villager.remove();
                    new CommandVillager().SummonVillager(villager.getLocation(), villager.getWorld(), villager.getCustomName());
                }
            }
        }
    }
}

class ItemChange {
    public String name;
    public int change;
}
