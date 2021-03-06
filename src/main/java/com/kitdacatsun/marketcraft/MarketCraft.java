package com.kitdacatsun.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.logging.Logger;


public final class MarketCraft extends JavaPlugin {

    public static MarketCraft plugin;
    public static Server server;
    public static World world;
    public static Logger logger;

    private final static Map<String, Integer> itemMap = new HashMap<>();
    private final static Map<String, Integer> priceMap = new HashMap<>();
    public static List<ItemChange> changeBuffer = new ArrayList<>();

    private static final long updateTimeTicks = 60 * 20;

    public static SettingsFile itemCounts;
    public static SettingsFile changeBufferSave;
    public static SettingsFile balance;
    public static SettingsFile shopMenus;
    public static SettingsFile playerShop;


    @Override
    public void onLoad() {
        logger = getLogger();

        plugin = this;
        server = plugin.getServer();
    }

    @Override
    public void onEnable() {
        logger.info("Enabled");

        world = server.getWorld("world");

        itemCounts = new SettingsFile("itemCounts.yml");
        changeBufferSave = new SettingsFile("changeBufferSave.yml");
        balance = new SettingsFile("playerBalances.yml");
        playerShop = new SettingsFile("playerShop.yml");
        shopMenus = new SettingsFile("shop.yml");

        for (String key : itemCounts.getKeys(false)) {
            itemMap.put(key, itemCounts.getInt(key));
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
            itemCounts.set(item, itemMap.get(item));
        }

        updatePrices();

        logger.info("Disabled");
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

        logger.info(ChatColor.BLUE + "---------------< COUNTS >---------------");

        for (String key: itemMap.keySet()) {
            int value = itemMap.get(key);

            logger.info( ChatColor.BLUE + key + ": \t " + value);

            if (value < lowest) {
                lowest = value;
            } else if (value > highest) {
                highest = value;
            }
        }

        double priceConstant = shopMenus.getDouble("PRICE_CONSTANT");
        double min = shopMenus.getDouble("MIN_PRICE");
        double max = shopMenus.getDouble("MAX_PRICE");

        logger.info(ChatColor.BLUE + "---------------< PRICES >---------------");

        for (String key: itemMap.keySet()) {
            double rarity = (itemMap.get(key) - lowest) / (highest - lowest);
            int price = (int)(clamp(priceConstant / rarity, min, max));
            priceMap.put(key, price);

            logger.info( ChatColor.BLUE + key + ": \t£" + priceMap.get(key));
        }

        logger.info(ChatColor.BLUE + "----------------------------------------");
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

class ItemChange {
    public String name;
    public int change;
}
