package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.commands.GardeningTweaksCmd;
import me.dave.gardeningtweaks.config.ConfigManager;
import me.dave.gardeningtweaks.hooks.CoreProtectHook;
import me.dave.gardeningtweaks.hooks.ProtocolLibHook;
import me.dave.gardeningtweaks.hooks.RealisticBiomesHook;
import me.dave.gardeningtweaks.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public final class GardeningTweaks extends JavaPlugin {
    private static final Random random = new Random();
    private static GardeningTweaks plugin;
    private static ConfigManager configManager;
    public static ProtocolLibHook protocolLibHook = null;
    public static RealisticBiomesHook realisticBiomesHook = null;
    public static CoreProtectHook coreProtectHook = null;
    private static int currTick = 0;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigManager();

        PluginManager pluginManager = getServer().getPluginManager();

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

        Bukkit.getScheduler().runTaskTimer(this, () -> currTick += 1, 1, 1);
    }

    public static Random getRandom() {
        return random;
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
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event instanceof Cancellable cancellable) {
            return !cancellable.isCancelled();
        } else {
            return true;
        }
    }

    private void addHook(String pluginName, Runnable runnable) {
        PluginManager pluginManager = getServer().getPluginManager();
        if (pluginManager.getPlugin(pluginName) != null && pluginManager.getPlugin(pluginName).isEnabled()) {
            runnable.run();
            getLogger().severe("Found plugin \"" + pluginName + "\". GardeningTweaks will now respect " + pluginName + " Claims.");
        }
    }
}
