package org.lushplugins.gardeningtweaks;

import org.lushplugins.gardeningtweaks.commands.GardeningTweaksCmd;
import org.lushplugins.gardeningtweaks.config.ConfigManager;
import me.dave.gardeningtweaks.hooks.*;
import me.dave.gardeningtweaks.hooks.claims.*;
import org.lushplugins.gardeningtweaks.hooks.packets.PacketEventsHook;
import org.lushplugins.gardeningtweaks.hooks.packets.PacketHook;
import org.lushplugins.gardeningtweaks.hooks.packets.ProtocolLibHook;
import org.lushplugins.gardeningtweaks.listener.GardeningTweaksListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.lushplugins.gardeningtweaks.hooks.CoreProtectHook;
import org.lushplugins.gardeningtweaks.hooks.RealisticBiomesHook;
import org.lushplugins.gardeningtweaks.hooks.claims.*;
import org.lushplugins.lushlib.LushLib;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.lushlib.utils.Updater;

import java.util.HashMap;
import java.util.Optional;
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
        LushLib.getInstance().enable(this);
        updater = new Updater(this, "gardening-tweaks", "gardeningtweaks.update", "gardeningtweaks update");

        configManager = new ConfigManager();
        configManager.reloadConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("packetevents") != null) {
            log(Level.INFO, "Found plugin \"packetevents\". Enabling packetevents support.");
            registerHook(new PacketEventsHook());
        } else if (pluginManager.getPlugin("ProtocolLib") != null) {
            log(Level.INFO, "Found plugin \"ProtocolLib\". Enabling ProtocolLib support.");
            registerHook(new ProtocolLibHook());
        }

        addHook("CoreProtect", () -> registerHook(new CoreProtectHook()));
        addHook("RealisticBiomes", () -> registerHook(new RealisticBiomesHook()));

        log(Level.INFO, "Loading protection hooks");
        addHook("GriefPrevention", () -> {
            registerHook(new GriefPreventionHook());
            log(Level.INFO, "GardeningTweaks now respects GriefPrevention Claims");
        });
        addHook("HuskClaims", () -> {
            registerHook(new HuskClaimsHook());
            log(Level.INFO, "GardeningTweaks now respects HuskClaims Claims");
        });
        addHook("HuskTowns", () -> {
            registerHook(new HuskTownsHook());
            log(Level.INFO, "GardeningTweaks now respects HuskTowns Claims");
        });
        addHook("Lands", () -> {
            registerHook(new LandsHook());
            log(Level.INFO, "GardeningTweaks now respects Lands Claims");
        });
        log(Level.INFO, "Finished loading protection hooks");

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
        LushLib.getInstance().disable();
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

    public Optional<PacketHook> getPacketHook() {
        for (Hook hook : hooks.values()) {
            if (hook instanceof PacketHook packetHook) {
                return Optional.of(packetHook);
            }
        }

        return Optional.empty();
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
