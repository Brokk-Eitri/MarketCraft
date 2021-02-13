package com.kitdacatsun.marketcraft;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


public final class MarketCraft extends JavaPlugin {

    public static MarketCraft plugin;
    public static Server server;
    public static World world;
    public static Logger logger;

    private final static HashMap<String, Integer> itemMap = new HashMap<>();
    public static ArrayList<ItemChange> changeBuffer = new ArrayList<>();

    private static final long updateTimeTicks = 24000;

    public static SettingsFile itemCounts;

    @Override
    public void onLoad() {
        logger = getLogger();

        plugin = this;
        server = plugin.getServer();
    }

    @Override
    public void onEnable() {
        world = server.getWorld("world");
        itemCounts = new SettingsFile("itemCounts.yml");

        BukkitScheduler scheduler = server.getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            if (world.getTime() % updateTimeTicks == 0) {
               logger.info("Updating prices");
               updateItemDict();
            }
        }, 100L, 1L);

        server.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        saveItemCounts();
    }

    private static void saveItemCounts() {
        for (String item : itemMap.keySet()) {
            itemCounts.set(item, itemMap.get(item));
        }
    }

    private static void updateItemDict() {
        for (int i = 0; i < changeBuffer.size(); i++) {
            ItemChange itemChange = changeBuffer.get(i);
            logger.info("Item: " + itemChange.name);
            itemMap.put(itemChange.name, itemMap.getOrDefault(itemChange.name, 0) + itemChange.change);
            changeBuffer.remove(itemChange);
        }

        saveItemCounts();

        System.gc();
    }
}

class ItemChange {
    public String name;
    public int change;

    public ItemChange(String name, int change) {
        this.name = name;
        this.change = change;
    }
}
