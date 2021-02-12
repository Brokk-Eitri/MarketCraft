package com.kitdacatsun.marketcraft;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Item;
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

    private final static HashMap<Item, Integer> itemMap = new HashMap<>();
    public static ArrayList<ItemChange> itemChanges = new ArrayList<>();

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
        writeItemCounts();
    }

    private static void writeItemCounts() {
        for (Item item : itemMap.keySet()) {
            itemCounts.set(item.getName(), itemMap.get(item));
        }
    }

    private static void updateItemDict() {
        for (int i = 0; i < itemChanges.size(); i++) {
            ItemChange itemChange = itemChanges.get(i);
            logger.info("Item: " + itemChange.item.getName());
            itemMap.put(itemChange.item, itemMap.getOrDefault(itemChange.item, 0) + itemChange.change);
            itemChanges.remove(itemChange);
        }

        writeItemCounts();

        System.gc();
    }
}

class ItemChange {
    public Item item;
    public int change;

    public ItemChange(Item item, int change) {
        this.item = item;
        this.change = change;
    }
}
