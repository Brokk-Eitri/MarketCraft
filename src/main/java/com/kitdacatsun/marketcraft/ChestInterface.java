package me.jame.chestinterface;


import org.bukkit.plugin.java.JavaPlugin;


public final class ChestInterface extends JavaPlugin {



    @Override
    public void onLoad() {
        ChestInterface plugin = this;
        getLogger().info("Loaded");
    }


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getConfig().options().copyDefaults();

        Files.setup();
        Files.get().options().copyDefaults(true);
        Files.save();


        getLogger().info("Enabled");

        getCommand("bank").setExecutor(new CommandBank());
        getCommand("balance").setExecutor(new CommandBalance());
        getCommand("shop").setExecutor(new Commandshop());
        getCommand("pay").setExecutor(new Commandpay());
    }


    @Override
    public void onDisable() {
        getLogger().info("Disabled");
    }
}

