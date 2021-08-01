package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
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
        public static YAMLFile shop;
        public static YAMLFile playerShop;
        public static YAMLFile itemChange;
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

        for (String key : files.itemCounts.getKeys(false)) {
            itemMap.put(key, files.itemCounts.getInt(key));
        }

        BukkitScheduler scheduler = server.getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, () -> updatePrices(false), 0, updateTimeTicks);

        // Register Listeners
        server.getPluginManager().registerEvents(new ListenerItemChange(), this);
        server.getPluginManager().registerEvents(new ListenerPlayerShop(), this);
        server.getPluginManager().registerEvents(new ListenerShop(), this);
        server.getPluginManager().registerEvents(new ListenerShopMenu(), this);
        server.getPluginManager().registerEvents(new ListenerPlayerShopAdd(), this);
        server.getPluginManager().registerEvents(new ListenerVillagers(), this);

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

        MarketCraft.updatePrices(true);

        getLogger().info("Disabled");
    }

    public static void updatePrices(boolean display) {
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
        if (display) {
            server.getLogger().info(ChatColor.BLUE + "---------------< COUNTS >---------------");
        }

        for (String key: itemMap.keySet()) {
            int value = itemMap.get(key);
            if (display) {
                server.getLogger().info(ChatColor.BLUE + key + ": \t " + value);
            }
            if (value < lowest) {
                lowest = value;
            } else if (value > highest) {
                highest = value;
            }
        }

        double min = files.shop.getDouble("MIN_PRICE");
        double max = files.shop.getDouble("MAX_PRICE");

        if (display) {
            server.getLogger().info(ChatColor.BLUE + "---------------< PRICES >---------------");
        }

        for (String key: itemMap.keySet()) {
            double rarity = (1 - ((itemMap.get(key) - lowest) / (highest - lowest))) * 10;
            int price = (int)(min + (((max - min) / 10) * rarity));
            priceMap.put(key, price);

            if (display) {
                server.getLogger().info(ChatColor.BLUE + key + ": \t£" + priceMap.get(key));
            }
        }

        if (display) {
            server.getLogger().info(ChatColor.BLUE + "----------------------------------------");
        }
    }
}

class ItemChange {
    public String name;
    public int change;
}
