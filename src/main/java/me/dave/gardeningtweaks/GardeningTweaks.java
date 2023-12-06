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
    private static ConcurrentHashMap<String, Module> modules;
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

//        Listener[] listeners = new Listener[] {
//            new BonemealFlowers(),
//            new ComposterSpreader(),
//            new CustomComposterOutput(),
//            new CustomGrassDrops(),
//            new Decoarsify(),
//            new DynamicTrample(),
//            new FastLeafDecay(),
//            new GrowthDance(),
//            new InteractiveHarvest(),
//            new Lumberjack(),
//            new RejuvenatedBushes(),
//            new SaplingReplant(),
//            new TreeSpread()
//        };
//        registerEvents(listeners);

        // Version Specific Features
//        if (Bukkit.getVersion().contains("1.20")) {
//            getServer().getPluginManager().registerEvents(new SnifferDrops(), this);
//        }

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

    private void registerEvents(Listener[] listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public static Optional<Module> getModule(String id) {
        return Optional.ofNullable(modules.get(id));
    }

    public static void registerModule(Module module) {
        if (modules.containsKey(module.getId())) {
            GardeningTweaks.getInstance().getLogger().severe("Failed to register module with id '" + module.getId() + "', a module with this id is already running");
            return;
        }

        modules.put(module.getId(), module);
        module.enable();
        if (module instanceof Listener listener) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, getInstance());
        }
    }

    public static void unregisterModule(String moduleId) {
        Module module = modules.get(moduleId);
        if (module != null) {
            module.disable();

            if (module instanceof Listener listener) {
                HandlerList.unregisterAll(listener);
            }
        }
        modules.remove(moduleId);
    }
    public static void unregisterAllModules() {
        modules.values().forEach(Module::disable);
        modules.clear();
    }
}
