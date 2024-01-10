package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.commands.GardeningTweaksCmd;
import me.dave.gardeningtweaks.config.ConfigManager;
import me.dave.gardeningtweaks.hooks.*;
import me.dave.gardeningtweaks.hooks.claims.*;
import me.dave.gardeningtweaks.listener.GardeningTweaksListener;
import me.dave.platyutils.PlatyUtils;
import me.dave.platyutils.hook.Hook;
import me.dave.platyutils.plugin.SpigotPlugin;
import me.dave.platyutils.utils.Updater;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import java.util.Random;
import java.util.logging.Level;

public final class GardeningTweaks extends SpigotPlugin {
    private static final Random random = new Random();
    private static GardeningTweaks plugin;
    private Updater updater;
    private static ConfigManager configManager;
    private static int currTick = 0;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        PlatyUtils.enable(this);
        updater = new Updater(this, "gardening-tweaks", "gardeningtweaks.update", "gardeningtweaks update");

        configManager = new ConfigManager();
        configManager.reloadConfig();

        addHook("CoreProtect", () -> registerHook(new CoreProtectHook()));
        addHook("ProtocolLib", () -> registerHook(new ProtocolLibHook()));
        addHook("RealisticBiomes", () -> registerHook(new RealisticBiomesHook()));
        addHook("GriefPrevention", () -> {
            registerHook(new GriefPreventionHook());
            log(Level.INFO, "GardeningTweaks now respects GriefPrevention Claims.");
        });
        addHook("HuskClaims", () -> {
            registerHook(new HuskClaimsHook());
            log(Level.INFO, "GardeningTweaks now respects HuskClaims Claims.");
        });
        addHook("HuskTowns", () -> {
            registerHook(new HuskTownsHook());
            log(Level.INFO, "GardeningTweaks now respects HuskTowns Claims.");
        });
        addHook("Lands", () -> {
            registerHook(new LandsHook());
            log(Level.INFO, "GardeningTweaks now respects Lands Claims.");
        });

        new GardeningTweaksListener().registerListeners();

        getCommand("gardeningtweaks").setExecutor(new GardeningTweaksCmd());

        Bukkit.getScheduler().runTaskTimer(this, () -> currTick += 1, 1, 1);
    }

    @Override
    public void onDisable() {
        unregisterAllHooks();
        unregisterAllModules();

        PlatyUtils.disable();
    }

    public Updater getUpdater() {
        return updater;
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

    public boolean hasPrivateClaimAt(Location location) {
        for (Hook hook : hooks.values()) {
            if (hook instanceof ClaimHook claimHook && claimHook.hasClaimAt(location)) {
                return true;
            }
        }

        return false;
    }
}
