package com.brokkandeitri.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;


public final class MarketCraft extends JavaPlugin {

    public static MarketCraft plugin;
    public static Server server;

    private final static Map<String, Integer> itemCountMap = new HashMap<>();
    private final static Map<String, Integer> itemPriceMap = new HashMap<>();
    public static List<ItemChange> changeBuffer = new ArrayList<>();

    private static final long priceHistorySaveDelay = 20 * 60 * 60 * 12;

    public static class files {
        public static YAMLFile itemCounts = new YAMLFile("itemCounts.yml");
        public static YAMLFile changeBuffer = new YAMLFile("changeBuffer.yml");
        public static YAMLFile balances = new YAMLFile("playerBalances.yml");
        public static YAMLFile shop = new YAMLFile("shop.yml");
        public static YAMLFile playerShop = new YAMLFile("playerShop.yml");
        public static YAMLFile priceHistory = new YAMLFile("priceHistory.yml");
    }

    public static int getPrice(ItemStack item) {
        return itemPriceMap.getOrDefault(item.getType().name(), files.shop.getInt("MAX_PRICE"));
    }

    @Override
    public void onLoad() {
        plugin = this;
        server = plugin.getServer();
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabled");

        SpawnVillagers();

        for (String key : files.itemCounts.getKeys(false)) {
            int count = files.itemCounts.getInt(key);
            if (count > 0) {
                itemCountMap.put(key, count);
            }
        }

        for (String key : files.changeBuffer.getKeys(false)) {
            ItemChange itemChange = new ItemChange();
            itemChange.name = key;
            itemChange.change = files.changeBuffer.getInt(key);
            changeBuffer.add(itemChange);
        }

        displayPrices();

        BukkitScheduler scheduler = server.getScheduler();
        int delayStart = 61 - LocalDateTime.now().getSecond();
        scheduler.scheduleSyncRepeatingTask(plugin, MarketCraft::updatePrices, delayStart * 20, 20 * 60);
        scheduler.scheduleSyncRepeatingTask(plugin, MarketCraft::updatePriceHistory, 0, priceHistorySaveDelay);

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
    }


    @Override
    public void onDisable() {
        for (String item : itemCountMap.keySet()) {
            files.itemCounts.set(item, itemCountMap.get(item));
        }

        for (ItemChange itemChange : changeBuffer.toArray(new ItemChange[0])) {
            files.changeBuffer.set(itemChange.name, itemChange.change);
        }

        displayPrices();
        updatePriceHistory();

        getLogger().info("Disabled");
    }

    public static void updatePriceHistory() {
        for (String key: itemPriceMap.keySet()){
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
            itemCountMap.put(itemChange.name, Math.max(itemCountMap.getOrDefault(itemChange.name, 0) + itemChange.change, 0));
        }

        changeBuffer.clear();

        if (itemCountMap.size() == 0) {
            return;
        }

        double lowest = Integer.MAX_VALUE;
        double highest = Integer.MIN_VALUE;

        for (String key : itemCountMap.keySet()) {
            int value = itemCountMap.get(key);
            if (value < lowest) {
                lowest = value;
            } else if (value > highest) {
                highest = value;
            }
        }

        double min = files.shop.getDouble("MIN_PRICE");
        double max = files.shop.getDouble("MAX_PRICE");

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
            server.getLogger().info(ChatColor.BLUE + key + ": " + " ".repeat(20 - key.length()) +
                    " £" + getPrice(new ItemStack(Material.valueOf(key))) + "\t(" + itemCountMap.get(key) + ")");
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
