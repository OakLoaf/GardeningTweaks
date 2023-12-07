package me.dave.gardeningtweaks.config;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.module.Module;
import me.dave.gardeningtweaks.module.custom.*;
import org.bukkit.Bukkit;
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

        ConfigurationSection modulesSection = config.getConfigurationSection("modules");
        if (modulesSection != null) {
            if (modulesSection.getBoolean("bone-meal-flowers", false)) {
                GardeningTweaks.getModule(BoneMealFlowers.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new BoneMealFlowers())
                );
            } else {
                GardeningTweaks.unregisterModule(BoneMealFlowers.ID);
            }

            if (modulesSection.getBoolean("composter-output", false)) {
                GardeningTweaks.getModule(ComposterOutput.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new ComposterOutput())
                );
            } else {
                GardeningTweaks.unregisterModule(ComposterOutput.ID);
            }

            if (modulesSection.getBoolean("composter-spreader", false)) {
                GardeningTweaks.getModule(ComposterSpreader.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new ComposterSpreader())
                );
            } else {
                GardeningTweaks.unregisterModule(ComposterSpreader.ID);
            }

            if (modulesSection.getBoolean("decoarsify", false)) {
                GardeningTweaks.getModule(Decoarsify.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new Decoarsify())
                );
            } else {
                GardeningTweaks.unregisterModule(Decoarsify.ID);
            }

            if (modulesSection.getBoolean("dynamic-trample", false)) {
                GardeningTweaks.getModule(DynamicTrample.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new DynamicTrample())
                );
            } else {
                GardeningTweaks.unregisterModule(DynamicTrample.ID);
            }

            if (modulesSection.getBoolean("fast-leaf-decay", false)) {
                GardeningTweaks.getModule(FastLeafDecay.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new FastLeafDecay())
                );
            } else {
                GardeningTweaks.unregisterModule(FastLeafDecay.ID);
            }

            if (modulesSection.getBoolean("grass-drops", false)) {
                GardeningTweaks.getModule(GrassDrops.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new GrassDrops())
                );
            } else {
                GardeningTweaks.unregisterModule(GrassDrops.ID);
            }

            if (modulesSection.getBoolean("growth-dance", false)) {
                GardeningTweaks.getModule(GrowthDance.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new GrowthDance())
                );
            } else {
                GardeningTweaks.unregisterModule(GrowthDance.ID);
            }

            if (modulesSection.getBoolean("interactive-harvest", false)) {
                GardeningTweaks.getModule(InteractiveHarvest.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new InteractiveHarvest())
                );
            } else {
                GardeningTweaks.unregisterModule(InteractiveHarvest.ID);
            }

            if (modulesSection.getBoolean("lumberjack", false)) {
                GardeningTweaks.getModule(Lumberjack.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new Lumberjack())
                );
            } else {
                GardeningTweaks.unregisterModule(Lumberjack.ID);
            }

            if (modulesSection.getBoolean("rejuvenated-bushes", false)) {
                GardeningTweaks.getModule(RejuvenatedBushes.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new RejuvenatedBushes())
                );
            } else {
                GardeningTweaks.unregisterModule(RejuvenatedBushes.ID);
            }

            if (modulesSection.getBoolean("sapling-replant", false)) {
                GardeningTweaks.getModule(SaplingReplant.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new SaplingReplant())
                );
            } else {
                GardeningTweaks.unregisterModule(SaplingReplant.ID);
            }

            if (modulesSection.getBoolean("sniffer-drops", false)) {
                String serverVersion = Bukkit.getVersion();
                if (!serverVersion.contains("1.20") && !serverVersion.contains("1.21")) {
                    GardeningTweaks.getInstance().getLogger().severe("The 'sniffer-drops' module requires versions 1.20 and above to function");
                }

                GardeningTweaks.getModule(SnifferDrops.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new SnifferDrops())
                );
            } else {
                GardeningTweaks.unregisterModule(SnifferDrops.ID);
            }

            if (modulesSection.getBoolean("tree-spread", false)) {
                GardeningTweaks.getModule(TreeSpread.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.registerModule(new TreeSpread())
                );
            } else {
                GardeningTweaks.unregisterModule(TreeSpread.ID);
            }
        }
    }
}