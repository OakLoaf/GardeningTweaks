package org.lushplugins.gardeningtweaks.config;

import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.module.*;
import org.lushplugins.lushlib.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigManager {

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

        ConfigurationSection modulesSection = config.getConfigurationSection("modules");
        if (modulesSection != null) {
            if (modulesSection.getBoolean("bone-meal-flowers", false)) {
                if (plugin.getModule(BoneMealFlowers.ID).isEmpty()) {
                    plugin.registerModule(new BoneMealFlowers());
                }
            } else {
                plugin.unregisterModule(BoneMealFlowers.ID);
            }

            if (modulesSection.getBoolean("composter-output", false)) {
                if (plugin.getModule(ComposterOutput.ID).isEmpty()) {
                    plugin.registerModule(new ComposterOutput());
                }
            } else {
                plugin.unregisterModule(ComposterOutput.ID);
            }

            if (modulesSection.getBoolean("composter-spreader", false)) {
                if (plugin.getModule(ComposterSpreader.ID).isEmpty()) {
                    plugin.registerModule(new ComposterSpreader());
                }
            } else {
                plugin.unregisterModule(ComposterSpreader.ID);
            }

            if (modulesSection.getBoolean("decoarsify", false)) {
                if (plugin.getModule(Decoarsify.ID).isEmpty()) {
                    plugin.registerModule(new Decoarsify());
                }
            } else {
                plugin.unregisterModule(Decoarsify.ID);
            }

            if (modulesSection.getBoolean("dynamic-trample", false)) {
                if (plugin.getModule(DynamicTrample.ID).isEmpty()) {
                    plugin.registerModule(new DynamicTrample());
                }
            } else {
                plugin.unregisterModule(DynamicTrample.ID);
            }

            if (modulesSection.getBoolean("fancy-trees", false)) {
                if (plugin.getModule(FancyTrees.ID).isEmpty()) {
                    plugin.registerModule(new FancyTrees());
                }
            } else {
                plugin.unregisterModule(FancyTrees.ID);
            }

            if (modulesSection.getBoolean("fast-leaf-decay", false)) {
                if (plugin.getModule(FastLeafDecay.ID).isEmpty()) {
                    plugin.registerModule(new FastLeafDecay());
                }
            } else {
                plugin.unregisterModule(FastLeafDecay.ID);
            }

            if (modulesSection.getBoolean("grass-drops", false)) {
                if (plugin.getModule(GrassDrops.ID).isEmpty()) {
                    plugin.registerModule(new GrassDrops());
                }
            } else {
                plugin.unregisterModule(GrassDrops.ID);
            }

            if (modulesSection.getBoolean("growth-dance", false)) {
                if (plugin.getModule(GrowthDance.ID).isEmpty()) {
                    plugin.registerModule(new GrowthDance());
                }
            } else {
                plugin.unregisterModule(GrowthDance.ID);
            }

            if (modulesSection.getBoolean("interactive-harvest", false)) {
                if (plugin.getModule(InteractiveHarvest.ID).isEmpty()) {
                    plugin.registerModule(new InteractiveHarvest());
                }
            } else {
                plugin.unregisterModule(InteractiveHarvest.ID);
            }

            if (modulesSection.getBoolean("lumberjack", false)) {
                if (plugin.getModule(Lumberjack.ID).isEmpty()) {
                    plugin.registerModule(new Lumberjack());
                }
            } else {
                plugin.unregisterModule(Lumberjack.ID);
            }

            if (modulesSection.getBoolean("rejuvenated-bushes", false)) {
                if (plugin.getModule(RejuvenatedBushes.ID).isEmpty()) {
                    plugin.registerModule(new RejuvenatedBushes());
                }
            } else {
                plugin.unregisterModule(RejuvenatedBushes.ID);
            }

            if (modulesSection.getBoolean("sapling-replant", false)) {
                if (plugin.getModule(SaplingReplant.ID).isEmpty()) {
                    plugin.registerModule(new SaplingReplant());
                }
            } else {
                plugin.unregisterModule(SaplingReplant.ID);
            }

            if (modulesSection.getBoolean("sniffer-drops", false)) {
                String serverVersion = Bukkit.getVersion();
                if (!serverVersion.contains("1.20") && !serverVersion.contains("1.21")) {
                    plugin.getLogger().severe("The 'sniffer-drops' module requires versions 1.20 and above to function");
                }

                if (plugin.getModule(SnifferDrops.ID).isEmpty()) {
                    plugin.registerModule(new SnifferDrops());
                }
            } else {
                plugin.unregisterModule(SnifferDrops.ID);
            }

            GardeningTweaks.getInstance().getModules().forEach(Module::reload);
        }
    }
}