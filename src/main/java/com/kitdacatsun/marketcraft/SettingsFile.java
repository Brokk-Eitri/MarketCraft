package com.kitdacatsun.marketcraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SettingsFile {
    private final File file;
    private final FileConfiguration customFile;

    public SettingsFile(String fileName) {
        file = new File(MarketCraft.plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    MarketCraft.logger.info("Created new file " + fileName);
                }
            } catch (IOException ignored) {
            }
        } else {
            MarketCraft.logger.info("Found file");
        }

        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            MarketCraft.logger.warning("Could not save settings file " + customFile.getName());
        }
    }

    public void set(String key, Object value) {
        customFile.set(key, value);
        save();
    }

    public Object get(String key) {
        return customFile.get(key);
    }

    public int getInt(String key) {
        return customFile.getInt(key);
    }

    public boolean contains(String key) {
        return customFile.contains(key);
    }

    public FileConfigurationOptions options() {
        return customFile.options();
    }

    public Object[] getKeys(boolean deep) {
        return customFile.getKeys(deep).toArray();
    }

    public FileConfiguration getCustomFile() {
        return customFile;
    }
}
