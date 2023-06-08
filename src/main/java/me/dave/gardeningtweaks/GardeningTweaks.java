package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.datamanager.ConfigManager;
import me.dave.gardeningtweaks.dependencies.ProtocolLibHook;
import me.dave.gardeningtweaks.dependencies.RealisticBiomesHook;
import me.dave.gardeningtweaks.events.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GardeningTweaks extends JavaPlugin {
    private static GardeningTweaks plugin;
    private static ConfigManager configManager;
    public static ProtocolLibHook protocolLibHook = null;
    public static RealisticBiomesHook realisticBiomesHook = null;
    private static int currTick = 0;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigManager();

        PluginManager pluginManager = getServer().getPluginManager();

        if (pluginManager.getPlugin("ProtocolLib") != null) protocolLibHook = new ProtocolLibHook();
        else plugin.getLogger().info("ProtocolLib plugin not found. Continuing without ProtocolLib Support.");

        if (pluginManager.getPlugin("RealisticBiomes") != null) realisticBiomesHook = new RealisticBiomesHook();
        else plugin.getLogger().info("RealisticBiomes plugin not found. Continuing without RealisticBiomes Support.");

        getCommand("gardeningtweaks").setExecutor(new GardeningTweaksCmd());

        Listener[] listeners = new Listener[] {
            new BonemealFlowers(),
            new ComposterSpreader(),
            new CustomComposterOutput(),
            new CustomGrassDrops(),
            new Decoarsify(),
            new DynamicTrample(),
            new FastLeafDecay(),
            new GrowthDance(),
            new InteractiveHarvest(),
            new Lumberjack(),
            new RejuvenatedBushes(),
            new TreeEvents()
        };
        registerEvents(listeners);

        // Version Specific Features
        if (Bukkit.getVersion().contains("1.20")) {
            getServer().getPluginManager().registerEvents(new CustomSnifferDrops(), this);
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> currTick += 1, 1, 1);
    }

    @Override
    public void onDisable() {

    }

    public static GardeningTweaks getInstance() {
        return plugin;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    private void registerEvents(Listener[] listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public static int getCurrentTick() {
        return currTick;
    }
}
