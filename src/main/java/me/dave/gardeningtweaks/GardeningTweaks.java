package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.dependencies.ProtocolLibHook;
import me.dave.gardeningtweaks.dependencies.RealisticBiomesHook;
import me.dave.gardeningtweaks.events.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GardeningTweaks extends JavaPlugin {
    private static GardeningTweaks plugin;
    public static ConfigManager configManager;
    private static int currTick = 0;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigManager();

        PluginManager pluginManager = getServer().getPluginManager();

        RealisticBiomesHook realisticBiomesHook = null;
        if (pluginManager.getPlugin("RealisticBiomes") != null) realisticBiomesHook = new RealisticBiomesHook();
        else getLogger().info("RealisticBiomes plugin not found. Continuing without RealisticBiomes Support.");

        ProtocolLibHook protocolLibHook = null;
        if (pluginManager.getPlugin("ProtocolLib") != null) protocolLibHook = new ProtocolLibHook();
        else getLogger().info("ProtocolLib plugin not found. Continuing without ProtocolLib Support.");

        getCommand("gardeningtweaks").setExecutor(new GardeningTweaksCmd());

        Listener[] listeners = new Listener[] {
            new BonemealFlowers(protocolLibHook),
            new CustomGrassDrops(),
            new Decoarsify(),
            new DynamicTrample(),
            new FastLeafDecay(),
            new GrowthDance(),
            new InteractiveHarvest(protocolLibHook),
            new Lumberjack(),
            new RejuvenatedBushes(protocolLibHook),
            new TreeEvents(realisticBiomesHook)
        };
        registerEvents(listeners);

        Bukkit.getScheduler().runTaskTimer(this, () -> currTick += 1, 1, 1);
    }

    public static GardeningTweaks getInstance() { return plugin; }

    public void registerEvents(Listener[] listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public static int getCurrentTick() {
        return currTick;
    }
}
