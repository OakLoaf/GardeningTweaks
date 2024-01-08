package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.commands.GardeningTweaksCmd;
import me.dave.gardeningtweaks.config.ConfigManager;
import me.dave.gardeningtweaks.hooks.*;
import me.dave.gardeningtweaks.hooks.claims.GriefPreventionHook;
import me.dave.gardeningtweaks.hooks.claims.HuskClaimsHook;
import me.dave.gardeningtweaks.hooks.claims.HuskTownsHook;
import me.dave.gardeningtweaks.listener.GardeningTweaksListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class GardeningTweaks extends JavaPlugin {
    private static final Random random = new Random();
    private static GardeningTweaks plugin;
    private static ConfigManager configManager;
    private static int currTick = 0;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigManager();

        PluginManager pluginManager = getServer().getPluginManager();

        addHook("CoreProtect", () -> Hook.register(new CoreProtectHook()));
        addHook("ProtocolLib", () -> Hook.register(new ProtocolLibHook()));
        addHook("RealisticBiomes", () -> Hook.register(new RealisticBiomesHook()));
        addHook("GriefPrevention", () -> {
            Hook.register(new GriefPreventionHook());
            getLogger().info("GardeningTweaks now respects GriefPrevention Claims.");
        });
        addHook("HuskClaims", () -> {
            Hook.register(new HuskClaimsHook());
            getLogger().info("GardeningTweaks now respects HuskClaims Claims.");
        });
        addHook("HuskTowns", () -> {
            Hook.register(new HuskTownsHook());
            getLogger().info("GardeningTweaks now respects HuskTowns Claims.");
        });

        pluginManager.registerEvents(new GardeningTweaksListener(), this);

        getCommand("gardeningtweaks").setExecutor(new GardeningTweaksCmd());

        Bukkit.getScheduler().runTaskTimer(this, () -> currTick += 1, 1, 1);
    }

    @Override
    public void onDisable() {
        Hook.unregisterAll();
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
            getLogger().info("Found plugin \"" + pluginName +"\". Enabling " + pluginName + " support.");
            runnable.run();
        }
    }
}
