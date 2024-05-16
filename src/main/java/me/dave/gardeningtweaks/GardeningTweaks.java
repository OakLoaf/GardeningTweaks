package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.commands.GardeningTweaksCmd;
import me.dave.gardeningtweaks.config.ConfigManager;
import me.dave.gardeningtweaks.hooks.*;
import me.dave.gardeningtweaks.hooks.claims.*;
import me.dave.gardeningtweaks.listener.GardeningTweaksListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.lushlib.utils.Updater;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

public final class GardeningTweaks extends SpigotPlugin {
    private static final Random RANDOM = new Random();
    private static GardeningTweaks plugin;
    private ConfigManager configManager;
    private Updater updater;
    private int currTick = 0;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
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

        Metrics metrics = new Metrics(this, 20745);
        metrics.addCustomChart(new AdvancedPie("gardeningtweaks_enabled_modules", () -> {
            HashMap<String, Integer> enabledModules = new HashMap<>();
            modules.keySet().forEach(moduleId -> enabledModules.put(moduleId, 1));
            return enabledModules;
        }));
    }

    @Override
    public void onDisable() {
        unregisterAllHooks();
        unregisterAllModules();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Updater getUpdater() {
        return updater;
    }

    public int getCurrentTick() {
        return currTick;
    }

    public static Random getRandom() {
        return RANDOM;
    }

    public static GardeningTweaks getInstance() {
        return plugin;
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
