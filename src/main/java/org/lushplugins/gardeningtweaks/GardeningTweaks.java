package org.lushplugins.gardeningtweaks;

import org.lushplugins.gardeningtweaks.command.GardeningTweaksCommand;
import org.lushplugins.gardeningtweaks.config.ConfigManager;
import org.lushplugins.gardeningtweaks.hook.Hook;
import org.lushplugins.gardeningtweaks.hook.HookId;
import org.lushplugins.gardeningtweaks.hook.packet.PacketEventsPacketHandler;
import org.lushplugins.gardeningtweaks.hook.packet.PacketHandler;
import org.lushplugins.gardeningtweaks.hook.packet.ProtocolLibPacketHandler;
import org.lushplugins.gardeningtweaks.listener.GardeningTweaksListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.lushplugins.gardeningtweaks.hook.RealisticBiomesHook;
import org.lushplugins.gardeningtweaks.hook.claim.*;
import org.lushplugins.gardeningtweaks.module.Module;
import org.lushplugins.gardeningtweaks.util.lamp.response.StringMessageResponseHandler;
import org.lushplugins.lushlib.utils.plugin.SpigotPlugin;
import org.lushplugins.pluginupdater.api.updater.Updater;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("Convert2MethodRef")
public final class GardeningTweaks extends SpigotPlugin {
    private static final Random RANDOM = new Random();
    private static GardeningTweaks plugin;

    private Map<String, Module> modules;
    private Map<String, Hook> hooks;
    private PacketHandler packetHandler;
    private ConfigManager configManager;
    private Updater updater;
    private int currTick = 0;

    @Override
    public void onLoad() {
        super.onLoad();
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.modules = new HashMap<>();
        this.hooks = new HashMap<>();

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

        ifPluginPresent("ProtocolLib", () -> packetHandler = new ProtocolLibPacketHandler());
        ifPluginPresent("packetevents", () -> packetHandler = new PacketEventsPacketHandler());

        registerHookIfPresent(HookId.REALISTIC_BIOMES, () -> new RealisticBiomesHook());

        log(Level.INFO, "Loading protection hooks");
        registerClaimHookIfPresent(HookId.GRIEF_PREVENTION, () -> new GriefPreventionHook());
        registerClaimHookIfPresent(HookId.HUSK_CLAIMS, () -> new HuskClaimsHook());
        registerClaimHookIfPresent(HookId.HUSK_TOWNS, () -> new HuskTownsHook());
        registerClaimHookIfPresent(HookId.LANDS, () -> new LandsHook());
        log(Level.INFO, "Finished loading protection hooks");

        registerListener(new GardeningTweaksListener());

        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this)
            .responseHandler(String.class, new StringMessageResponseHandler())
            .build();
        lamp.register(new GardeningTweaksCommand());

        Bukkit.getScheduler().runTaskTimer(this, () -> currTick += 1, 1, 1);

        Metrics metrics = new Metrics(this, 20745);
        metrics.addCustomChart(new AdvancedPie("gardeningtweaks_enabled_modules", () -> {
            return modules.keySet().stream()
                .collect(Collectors.toMap(
                    Function.identity(),
                    (ignored) -> 1
                ));
        }));
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    public Module getModule(String id) {
        return modules.get(id);
    }
    
    public void registerModule(String id, Module module) {
        modules.put(id, module);
    }

    public void unregisterModule(String id) {
        modules.remove(id);
    }

    public Hook getHook(String id) {
        return hooks.get(id);
    }

    public Optional<Hook> getOptionalHook(String id) {
        return Optional.ofNullable(getHook(id));
    }

    public void registerHookIfPresent(String pluginName, Callable<Hook> hook) {
        ifPluginPresent(pluginName, () -> {
            try {
                hooks.put(pluginName, hook.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void registerClaimHookIfPresent(String pluginName, Callable<Hook> hook) {
        registerHookIfPresent(pluginName, hook);
        log(Level.INFO, "GardeningTweaks now respects %s Claims"
            .formatted(pluginName));
    }

    public Optional<PacketHandler> getPacketHandler() {
        return Optional.ofNullable(packetHandler);
    }

    public boolean hasPrivateClaimAt(Location location) {
        for (Hook hook : hooks.values()) {
            if (hook instanceof ClaimHandler claimHook && claimHook.hasClaimAt(location)) {
                return true;
            }
        }

        return false;
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
}
