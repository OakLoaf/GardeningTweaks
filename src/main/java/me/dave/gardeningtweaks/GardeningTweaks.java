package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.data.ConfigManager;
import me.dave.gardeningtweaks.hooks.CoreProtectHook;
import me.dave.gardeningtweaks.hooks.ProtocolLibHook;
import me.dave.gardeningtweaks.hooks.RealisticBiomesHook;
import me.dave.gardeningtweaks.events.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GardeningTweaks extends JavaPlugin {
    private static GardeningTweaks plugin;
    private static ConfigManager configManager;
    public static ProtocolLibHook protocolLibHook = null;
    public static RealisticBiomesHook realisticBiomesHook = null;
    public static CoreProtectHook coreProtectHook = null;
    private static PluginManager pluginManager;
    private static int currTick = 0;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigManager();

        pluginManager = getServer().getPluginManager();

        if (pluginManager.getPlugin("CoreProtect") != null) {
            coreProtectHook = new CoreProtectHook();
            plugin.getLogger().info("Found plugin \"CoreProtect\". CoreProtect support enabled.");
        }

        if (pluginManager.getPlugin("ProtocolLib") != null) {
            protocolLibHook = new ProtocolLibHook();
            plugin.getLogger().info("Found plugin \"ProtocolLib\". ProtocolLib support enabled.");
        }

        if (pluginManager.getPlugin("RealisticBiomes") != null) {
            realisticBiomesHook = new RealisticBiomesHook();
            plugin.getLogger().info("Found plugin \"RealisticBiomes\". RealisticBiomes support enabled.");
        }

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
            new TreeSpread()
        };
        registerEvents(listeners);

        // Version Specific Features
        if (Bukkit.getVersion().contains("1.20")) {
            getServer().getPluginManager().registerEvents(new CustomSnifferDrops(), this);
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> currTick += 1, 1, 1);
    }

    public static GardeningTweaks getInstance() {
        return plugin;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static int getCurrentTick() {
        return currTick;
    }

    public static boolean callEvent(Event event) {
        pluginManager.callEvent(event);
        if (event instanceof Cancellable) {
            return !((Cancellable) event).isCancelled();
        } else {
            return true;
        }
    }

    private void registerEvents(Listener[] listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
