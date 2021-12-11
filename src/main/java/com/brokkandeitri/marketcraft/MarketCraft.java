package com.brokkandeitri.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public final class MarketCraft extends JavaPlugin {

    public static MarketCraft plugin;
    public static Server server;

    private final static Map<String, Integer> itemMap = new HashMap<>();
    private final static Map<String, Integer> priceMap = new HashMap<>();
    public static List<ItemChange> changeBuffer = new ArrayList<>();

    private static final long updateTimeTicks = 20 * 60;
    private static final long priceHistorySaveTicks = 20 * 60 * 60 * 12;

    public static class files {
        public static YAMLFile itemCounts;
        public static YAMLFile balance;
        public static YAMLFile shop;
        public static YAMLFile playerShop;
        public static YAMLFile priceHistory;
    }

    public static int getPrice(ItemStack item) {
        return priceMap.getOrDefault(item.getType().name(), files.shop.getInt("MAX_PRICE"));
    }

    @Override
    public void onLoad() {
        plugin = this;
        server = plugin.getServer();
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabled");

        files.itemCounts = new YAMLFile("itemCounts.yml");
        files.balance = new YAMLFile("playerBalances.yml");
        files.playerShop = new YAMLFile("playerShop.yml");
        files.shop = new YAMLFile("shop.yml");
        files.priceHistory = new YAMLFile("priceHistory.yml");

        SpawnVillagers();

        for (String key : files.itemCounts.getKeys(false)) {
            itemMap.put(key, files.itemCounts.getInt(key));
        }

        updatePrices();
        displayPrices();
        updatePriceHistory();

        BukkitScheduler scheduler = server.getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, MarketCraft::updatePrices, 0, updateTimeTicks);
        scheduler.scheduleSyncRepeatingTask(plugin, MarketCraft::updatePriceHistory, 0, priceHistorySaveTicks);

        // Register Listeners
        server.getPluginManager().registerEvents(new ListenerItemChange(), this);
        server.getPluginManager().registerEvents(new ListenerPlayerShop(), this);
        server.getPluginManager().registerEvents(new ListenerShop(), this);
        server.getPluginManager().registerEvents(new ListenerShopMenu(), this);
        server.getPluginManager().registerEvents(new ListenerPlayerShopAdd(), this);
        server.getPluginManager().registerEvents(new ListenerVillagers(), this);
        server.getPluginManager().registerEvents(new ListenerPriceHistory(), this);

        // Register Commands
        Objects.requireNonNull(getCommand("villager")).setExecutor(new CommandVillager());
        Objects.requireNonNull(getCommand("balance")).setExecutor(new CommandBalance());
        Objects.requireNonNull(getCommand("shop")).setExecutor(new CommandShopMenu());
        Objects.requireNonNull(getCommand("pay")).setExecutor(new CommandPay());
        Objects.requireNonNull(getCommand("price")).setExecutor(new CommandPrice());
    }


    @Override
    public void onDisable() {
        for (String item : itemMap.keySet()) {
            files.itemCounts.set(item, itemMap.get(item));
        }

        updatePrices();
        displayPrices();
        updatePriceHistory();

        getLogger().info("Disabled");
    }

    public static void updatePriceHistory() {
        for (String key: itemMap.keySet()){
            if (!files.priceHistory.contains(key)){
                files.priceHistory.set(key, "");
            }
        }

        Set<String> keys = files.priceHistory.getKeys(true);

        for (String key: keys){
            @NotNull List<String> prices = files.priceHistory.getStringList(key);
            prices.add(String.valueOf(getPrice(new ItemStack(Material.valueOf(key)))));
            files.priceHistory.set(key, prices);
        }

        server.getLogger().info(ChatColor.BLUE + "Price History Updated");
    }

    public static void updatePrices() {
        for (ItemChange itemChange : changeBuffer) {
            itemMap.put(itemChange.name, Math.max(itemMap.getOrDefault(itemChange.name, 0) + itemChange.change, 0));
        }

        changeBuffer.clear();

        if (itemMap.size() == 0) {
            return;
        }

        double lowest = Integer.MAX_VALUE;
        double highest = Integer.MIN_VALUE;

        for (String key : itemMap.keySet()) {
            int value = itemMap.get(key);
            if (value < lowest) {
                lowest = value;
            } else if (value > highest) {
                highest = value;
            }
        }

        double min = files.shop.getDouble("MIN_PRICE");
        double max = files.shop.getDouble("MAX_PRICE");

        for (String key: itemMap.keySet()) {
            double rarity = 1 - ((itemMap.get(key) - lowest) / (highest - lowest));
            int price = (int)(min + ((max - min) * rarity));
            priceMap.put(key, price);
        }

        server.getLogger().info(ChatColor.BLUE + "Prices Updated");
    }

    public static void displayPrices() {
        server.getLogger().info(ChatColor.BLUE + "---------------< PRICES >---------------");

        for (String key : itemMap.keySet()) {
            server.getLogger().info(ChatColor.BLUE + key + ": " + " ".repeat(20 - key.length()) +
                    " £" + getPrice(new ItemStack(Material.valueOf(key))) + "\t(" + itemMap.get(key) + ")");
        }

        server.getLogger().info(ChatColor.BLUE + "----------------------------------------");
    }

    public void SpawnVillagers(){
        if (files.shop.contains("Villagers")) {
            List<String> villagers = files.shop.getStringList("Villagers");
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
