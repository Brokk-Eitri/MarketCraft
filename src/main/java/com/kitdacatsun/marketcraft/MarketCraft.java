package com.kitdacatsun.marketcraft;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
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
    public static SettingsFile changeBufferSave;
    public static SettingsFile playerBalances;
    public static SettingsFile shopMenus;

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
        playerBalances = new SettingsFile("playerBalances.yml");
        shopMenus = new SettingsFile("shopMenus.yml");

        for (Object key : itemCounts.getKeys(false)) {
            itemMap.put((String) key, itemCounts.getInt((String) key));
        }

        for (Object key : changeBufferSave.getKeys(false)) {
            ItemChange itemChange = new ItemChange();
            itemChange.name = (String) key;
            itemChange.change = (int) changeBufferSave.get((String) key);
            changeBuffer.add(itemChange);
        }

        BukkitScheduler scheduler = server.getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            if (world.getTime() % updateTimeTicks == 0) {
                logger.info("Updating prices");

                for (int i = 0; i < changeBuffer.size(); i++) {
                    ItemChange itemChange = changeBuffer.get(i);
                    logger.info("Item: " + itemChange.name);
                    itemMap.put(itemChange.name, itemMap.getOrDefault(itemChange.name, 0) + itemChange.change);
                    changeBuffer.remove(itemChange);
                }
            }
        }, 100L, 1L);

        server.getPluginManager().registerEvents(new ItemPickupListener(), this);
        server.getPluginManager().registerEvents(new GuiListener(), this);

        Objects.requireNonNull(getCommand("bank")).setExecutor(new CommandShop());
        Objects.requireNonNull(getCommand("balance")).setExecutor(new CommandBalance());
        Objects.requireNonNull(getCommand("shop")).setExecutor(new CommandShopMenu());
        Objects.requireNonNull(getCommand("pay")).setExecutor(new CommandPay());
    }

    @Override
    public void onDisable() {
        logger.info("Disabled");

        for (String item : itemMap.keySet()) {
            itemCounts.set(item, itemMap.get(item));
        }

        for (ItemChange itemChange : changeBuffer) {
            changeBufferSave.set(itemChange.name, itemChange.change);
        }
    }
}

class ItemChange {
    public String name;
    public int change;
}
