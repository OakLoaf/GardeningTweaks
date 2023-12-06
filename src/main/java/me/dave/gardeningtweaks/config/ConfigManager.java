package me.dave.gardeningtweaks.config;

import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();

    public ConfigManager() {
        GardeningTweaks.getInstance().saveDefaultConfig();

        reloadConfig();
    }

    public void reloadConfig() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
    }
}