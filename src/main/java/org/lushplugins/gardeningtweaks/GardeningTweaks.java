package org.lushplugins.gardeningtweaks;

import org.lushplugins.gardeningtweaks.commands.GardeningTweaksCommand;
import org.lushplugins.gardeningtweaks.config.ConfigManager;
import org.lushplugins.gardeningtweaks.hooks.packets.PacketEventsHook;
import org.lushplugins.gardeningtweaks.hooks.packets.PacketHook;
import org.lushplugins.gardeningtweaks.hooks.packets.ProtocolLibHook;
import org.lushplugins.gardeningtweaks.listener.GardeningTweaksListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.lushplugins.gardeningtweaks.hooks.RealisticBiomesHook;
import org.lushplugins.gardeningtweaks.hooks.claims.*;
import org.lushplugins.gardeningtweaks.util.lamp.response.StringMessageResponseHandler;
import org.lushplugins.lushlib.LushLib;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.pluginupdater.api.updater.Updater;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

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
        this.configManager = new ConfigManager();
        this.configManager.reloadConfig();

        if (this.configManager.shouldCheckUpdates()) {
            this.updater = new Updater.Builder(this)
                .modrinth("ilX5LKrx", true)
                .checkSchedule(900)
                .notify(true)
                .notificationPermission("gardeningtweaks.update")
                .notificationMessage("&#e0c01b%plugin% &#ffe27ahas an update! Type &#e0c01b/gardeningtweaks update &#ffe27ato update! (&#e0c01b%current_version% &#ffe27a-> &#e0c01b%latest_version%&#ffe27a)")
                .build();
        } else {
            this.updater = null;
        }

        ifPluginPresent("packetevents", () -> registerHook(new PacketEventsHook()));
        ifPluginPresent("ProtocolLib", () -> registerHook(new ProtocolLibHook()));
        ifPluginEnabled("RealisticBiomes", () -> registerHook(new RealisticBiomesHook()));

        log(Level.INFO, "Loading protection hooks");
        ifPluginEnabled("GriefPrevention", () -> {
            registerHook(new GriefPreventionHook());
            log(Level.INFO, "GardeningTweaks now respects GriefPrevention Claims");
        });
        ifPluginEnabled("HuskClaims", () -> {
            registerHook(new HuskClaimsHook());
            log(Level.INFO, "GardeningTweaks now respects HuskClaims Claims");
        });
        ifPluginEnabled("HuskTowns", () -> {
            registerHook(new HuskTownsHook());
            log(Level.INFO, "GardeningTweaks now respects HuskTowns Claims");
        });
        ifPluginEnabled("Lands", () -> {
            registerHook(new LandsHook());
            log(Level.INFO, "GardeningTweaks now respects Lands Claims");
        });
        log(Level.INFO, "Finished loading protection hooks");

        registerListener(new GardeningTweaksListener());

        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this)
            .responseHandler(String.class, new StringMessageResponseHandler())
            .build();
        lamp.register(new GardeningTweaksCommand());

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
