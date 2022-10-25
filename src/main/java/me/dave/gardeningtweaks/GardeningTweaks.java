package me.dave.gardeningtweaks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.dave.gardeningtweaks.dependencies.RealisticBiomesHook;
import me.dave.gardeningtweaks.events.BlockEvents;
import me.dave.gardeningtweaks.events.CropEvents;
import me.dave.gardeningtweaks.events.PlayerEvents;
import me.dave.gardeningtweaks.events.TreeEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GardeningTweaks extends JavaPlugin {
    private static GardeningTweaks plugin;
    public static ConfigManager configManager;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigManager();

        RealisticBiomesHook realisticBiomesHook = null;
        PluginManager pluginManager = getServer().getPluginManager();
        if (pluginManager.getPlugin("RealisticBiomes") != null) realisticBiomesHook = new RealisticBiomesHook();
        else getLogger().info("RealisticBiomes plugin not found. Continuing without RealisticBiomes Support.");

        ProtocolManager protocolManager = null;
        if (pluginManager.getPlugin("ProtocolLib") != null) protocolManager = ProtocolLibrary.getProtocolManager();
        else getLogger().info("ProtocolLib plugin not found. Continuing without ProtocolLib Support.");

        getCommand("gardeningtweaks").setExecutor(new GardeningTweaksCmd());

        Listener[] listeners = new Listener[] {
            new BlockEvents(),
            new CropEvents(protocolManager),
            new PlayerEvents(),
            new TreeEvents(realisticBiomesHook)
        };
        registerEvents(listeners);
    }

    public static GardeningTweaks getInstance() { return plugin; }

    public void registerEvents(Listener[] listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
