package me.dave.gardeningtweaks.config;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.module.*;
import me.dave.platyutils.module.Module;
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
                GardeningTweaks.getInstance().getModule(BoneMealFlowers.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new BoneMealFlowers())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(BoneMealFlowers.ID);
            }

            if (modulesSection.getBoolean("composter-output", false)) {
                GardeningTweaks.getInstance().getModule(ComposterOutput.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new ComposterOutput())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(ComposterOutput.ID);
            }

            if (modulesSection.getBoolean("composter-spreader", false)) {
                GardeningTweaks.getInstance().getModule(ComposterSpreader.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new ComposterSpreader())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(ComposterSpreader.ID);
            }

            if (modulesSection.getBoolean("decoarsify", false)) {
                GardeningTweaks.getInstance().getModule(Decoarsify.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new Decoarsify())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(Decoarsify.ID);
            }

            if (modulesSection.getBoolean("dynamic-trample", false)) {
                GardeningTweaks.getInstance().getModule(DynamicTrample.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new DynamicTrample())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(DynamicTrample.ID);
            }

            if (modulesSection.getBoolean("fast-leaf-decay", false)) {
                GardeningTweaks.getInstance().getModule(FastLeafDecay.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new FastLeafDecay())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(FastLeafDecay.ID);
            }

            if (modulesSection.getBoolean("grass-drops", false)) {
                GardeningTweaks.getInstance().getModule(GrassDrops.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new GrassDrops())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(GrassDrops.ID);
            }

            if (modulesSection.getBoolean("growth-dance", false)) {
                GardeningTweaks.getInstance().getModule(GrowthDance.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new GrowthDance())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(GrowthDance.ID);
            }

            if (modulesSection.getBoolean("interactive-harvest", false)) {
                GardeningTweaks.getInstance().getModule(InteractiveHarvest.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new InteractiveHarvest())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(InteractiveHarvest.ID);
            }

            if (modulesSection.getBoolean("lumberjack", false)) {
                GardeningTweaks.getInstance().getModule(Lumberjack.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new Lumberjack())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(Lumberjack.ID);
            }

            if (modulesSection.getBoolean("rejuvenated-bushes", false)) {
                GardeningTweaks.getInstance().getModule(RejuvenatedBushes.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new RejuvenatedBushes())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(RejuvenatedBushes.ID);
            }

            if (modulesSection.getBoolean("sapling-replant", false)) {
                GardeningTweaks.getInstance().getModule(SaplingReplant.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new SaplingReplant())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(SaplingReplant.ID);
            }

            if (modulesSection.getBoolean("sniffer-drops", false)) {
                String serverVersion = Bukkit.getVersion();
                if (!serverVersion.contains("1.20") && !serverVersion.contains("1.21")) {
                    GardeningTweaks.getInstance().getLogger().severe("The 'sniffer-drops' module requires versions 1.20 and above to function");
                }

                GardeningTweaks.getInstance().getModule(SnifferDrops.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new SnifferDrops())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(SnifferDrops.ID);
            }

            if (modulesSection.getBoolean("tree-spread", false)) {
                GardeningTweaks.getInstance().getModule(TreeSpread.ID).ifPresentOrElse(
                        Module::reload,
                        () -> GardeningTweaks.getInstance().registerModule(new TreeSpread())
                );
            } else {
                GardeningTweaks.getInstance().unregisterModule(TreeSpread.ID);
            }
        }
    }
}