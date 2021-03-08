package com.kitdacatsun.marketcraft;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class YAMLFile {
    private final File file;
    private final FileConfiguration customFile;

    public YAMLFile(String fileName) {
        file = new File(MarketCraft.plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Bukkit.getLogger().info("Created new YAML file: " + fileName);
                }
            } catch (IOException ignored) { }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Could not save settings file " + customFile.getName());
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

    public double getDouble(String key) {
        return customFile.getDouble(key);
    }

    public String getString(String key) {
        return customFile.getString(key);
    }

    public @NotNull List<String> getStringList(String key) {
        return customFile.getStringList(key);
    }

    public void setStringList(String key, List<String> list) {
        String value = "[" + String.join(", ", list) + "]";
        customFile.set(key, value);
    }

    public boolean contains(String key) {
        return customFile.contains(key);
    }

    public FileConfigurationOptions options() {
        return customFile.options();
    }

    public @NotNull Set<String> getKeys(boolean deep) {
        return customFile.getKeys(deep);
    }
}
