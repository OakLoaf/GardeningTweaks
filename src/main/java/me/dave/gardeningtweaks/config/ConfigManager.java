package me.dave.gardeningtweaks.config;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.module.custom.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    public ConfigManager() {
        GardeningTweaks.getInstance().saveDefaultConfig();

        reloadConfig();
    }

    public void reloadConfig() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // TODO: Add check for already registered modules
        //  (Make sure to unregister old modules that are no longer enabled)
        ConfigurationSection modulesSection = config.getConfigurationSection("modules");
        if (modulesSection != null) {
            if (modulesSection.getBoolean("bonemeal-flowers", false)) {
                GardeningTweaks.registerModule(new BonemealFlowers());
            }
            if (modulesSection.getBoolean("composter-output", false)) {
                GardeningTweaks.registerModule(new ComposterOutput());
            }
            if (modulesSection.getBoolean("composter-spreader", false)) {
                GardeningTweaks.registerModule(new ComposterSpreader());
            }
            if (modulesSection.getBoolean("decoarsify", false)) {
                GardeningTweaks.registerModule(new Decoarsify());
            }
            if (modulesSection.getBoolean("dynamic-trample", false)) {
                GardeningTweaks.registerModule(new DynamicTrample());
            }
            if (modulesSection.getBoolean("fast-leaf-decay", false)) {
                GardeningTweaks.registerModule(new FastLeafDecay());
            }
            if (modulesSection.getBoolean("grass-drops", false)) {
                GardeningTweaks.registerModule(new GrassDrops());
            }
            if (modulesSection.getBoolean("growth-dance", false)) {
                GardeningTweaks.registerModule(new GrowthDance());
            }
            if (modulesSection.getBoolean("interactive-harvest", false)) {
                GardeningTweaks.registerModule(new InteractiveHarvest());
            }
            if (modulesSection.getBoolean("lumberjack", false)) {
                GardeningTweaks.registerModule(new Lumberjack());
            }
            if (modulesSection.getBoolean("rejuvenated-bushes", false)) {
                GardeningTweaks.registerModule(new RejuvenatedBushes());
            }
            if (modulesSection.getBoolean("sapling-replant", false)) {
                GardeningTweaks.registerModule(new SaplingReplant());
            }
            if (modulesSection.getBoolean("sniffer-drops", false)) {
                GardeningTweaks.registerModule(new SnifferDrops());
            }
            if (modulesSection.getBoolean("tree-spread", false)) {
                GardeningTweaks.registerModule(new TreeSpread());
            }
        }
    }
}