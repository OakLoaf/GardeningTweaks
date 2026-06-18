package org.lushplugins.gardeningtweaks.config;

import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.module.*;
import org.lushplugins.gardeningtweaks.module.Module;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.concurrent.Callable;

public class ConfigManager {
    private boolean checkUpdates;

    public ConfigManager() {
        GardeningTweaks.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        reloadConfig(true);
    }

    public void reloadConfig(boolean checkVersion) {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        int configVersion = config.getInt("config-version", -1);
        if (checkVersion && configVersion == -1) {
            GardeningTweaks.getInstance().backupFile(new File(plugin.getDataFolder(), "config.yml"));
            plugin.saveResource("config.yml", true);
            reloadConfig(false);
            return;
        }

        this.checkUpdates = config.getBoolean("check-updates", true);

        ConfigurationSection modulesSection = config.getConfigurationSection("modules");
        if (modulesSection != null) {
            updateModule(modulesSection, ModuleId.BONE_MEAL_FLOWERS, BoneMealFlowers::new);
            updateModule(modulesSection, ModuleId.COMPOSTER_OUTPUT, ComposterOutput::new);
            updateModule(modulesSection, ModuleId.COMPOSTER_SPREADER, ComposterSpreader::new);
            updateModule(modulesSection, ModuleId.DECOARSIFY, Decoarsify::new);
            updateModule(modulesSection, ModuleId.DYNAMIC_TRAMPLE, DynamicTrample::new);
            updateModule(modulesSection, ModuleId.FANCY_TREES, FancyTrees::new);
            updateModule(modulesSection, ModuleId.FAST_LEAF_DECAY, FastLeafDecay::new);
            updateModule(modulesSection, ModuleId.GRASS_DROPS, GrassDrops::new);
            updateModule(modulesSection, ModuleId.GROWTH_DANCE, GrowthDance::new);
            updateModule(modulesSection, ModuleId.INTERACTIVE_HARVEST, InteractiveHarvest::new);
            updateModule(modulesSection, ModuleId.LUMBERJACK, Lumberjack::new);
            updateModule(modulesSection, ModuleId.REJUVENATED_BUSHES, RejuvenatedBushes::new);
            updateModule(modulesSection, ModuleId.SAPLING_REPLANT, SaplingReplant::new);
            updateModule(modulesSection, ModuleId.SNIFFER_DROPS, SnifferDrops::new);

            GardeningTweaks.getInstance().getModules().forEach(Module::reload);
        }
    }

    public void updateModule(ConfigurationSection modulesSection, String moduleId, Callable<Module> module) {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        if (modulesSection.getBoolean(moduleId.replace("_", "-"), false)) {
            if (plugin.getModule(moduleId) == null) {
                try {
                    plugin.registerModule(moduleId, module.call());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            plugin.unregisterModule(moduleId);
        }
    }

    public boolean shouldCheckUpdates() {
        return checkUpdates;
    }
}