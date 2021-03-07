package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;


public final class MarketCraft extends JavaPlugin {

    public static MarketCraft plugin;
    public static Server server;

    private final static Map<String, Integer> itemMap = new HashMap<>();
    private final static Map<String, Integer> priceMap = new HashMap<>();
    public static List<ItemChange> changeBuffer = new ArrayList<>();

    private static final long updateTimeTicks = 5 * 60 * 20;

    public static class files {
        public static YAMLFile itemCounts;
        public static YAMLFile balance;
        public static YAMLFile shopMenus;
        public static YAMLFile playerShop;
    }

    public static MarketCraft getPlugin() {
        return plugin;
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
        files.shopMenus = new YAMLFile("shop.yml");

        for (String key : files.itemCounts.getKeys(false)) {
            itemMap.put(key, files.itemCounts.getInt(key));
        }

        BukkitScheduler scheduler = server.getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, this::updatePrices, 0, updateTimeTicks);

        server.getPluginManager().registerEvents(new ItemChangeListener(), this);
        server.getPluginManager().registerEvents(new GuiListener(), this);

        Objects.requireNonNull(getCommand("villager")).setExecutor(new CommandVillager());
        Objects.requireNonNull(getCommand("balance")).setExecutor(new CommandBalance());
        Objects.requireNonNull(getCommand("shop")).setExecutor(new CommandShopMenu());
        Objects.requireNonNull(getCommand("pay")).setExecutor(new CommandPay());
    }

    @Override
    public void onDisable() {
        for (String item : itemMap.keySet()) {
            files.itemCounts.set(item, itemMap.get(item));
        }

        updatePrices();

        getLogger().info("Disabled");
    }

    private void updatePrices() {

        for (int i = 0; i < changeBuffer.size(); i++) {
            ItemChange itemChange = changeBuffer.get(i);
            itemMap.put(itemChange.name, itemMap.getOrDefault(itemChange.name, 0) + itemChange.change);
            changeBuffer.remove(itemChange);
        }

        if (itemMap.size() == 0) {
            return;
        }

        double lowest = Integer.MAX_VALUE;
        double highest = Integer.MIN_VALUE;

        getLogger().info(ChatColor.BLUE + "---------------< COUNTS >---------------");

        for (String key: itemMap.keySet()) {
            int value = itemMap.get(key);

            getLogger().info( ChatColor.BLUE + key + ": \t " + value);

            if (value < lowest) {
                lowest = value;
            } else if (value > highest) {
                highest = value;
            }
        }

        double priceConstant = files.shopMenus.getDouble("PRICE_CONSTANT");
        double min = files.shopMenus.getDouble("MIN_PRICE");
        double max = files.shopMenus.getDouble("MAX_PRICE");

        getLogger().info(ChatColor.BLUE + "---------------< PRICES >---------------");

        for (String key: itemMap.keySet()) {
            double rarity = (itemMap.get(key) - lowest) / (highest - lowest);
            int price = (int)(clamp(priceConstant / rarity, min, max));
            priceMap.put(key, price);

            getLogger().info( ChatColor.BLUE + key + ": \t£" + priceMap.get(key));
        }

        getLogger().info(ChatColor.BLUE + "----------------------------------------");
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

class ItemChange {
    public String name;
    public int change;
}
