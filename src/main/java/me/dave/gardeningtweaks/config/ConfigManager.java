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
                Module.get(BoneMealFlowers.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new BoneMealFlowers())
                );
            } else {
                Module.unregister(BoneMealFlowers.ID);
            }

            if (modulesSection.getBoolean("composter-output", false)) {
                Module.get(ComposterOutput.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new ComposterOutput())
                );
            } else {
                Module.unregister(ComposterOutput.ID);
            }

            if (modulesSection.getBoolean("composter-spreader", false)) {
                Module.get(ComposterSpreader.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new ComposterSpreader())
                );
            } else {
                Module.unregister(ComposterSpreader.ID);
            }

            if (modulesSection.getBoolean("decoarsify", false)) {
                Module.get(Decoarsify.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new Decoarsify())
                );
            } else {
                Module.unregister(Decoarsify.ID);
            }

            if (modulesSection.getBoolean("dynamic-trample", false)) {
                Module.get(DynamicTrample.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new DynamicTrample())
                );
            } else {
                Module.unregister(DynamicTrample.ID);
            }

            if (modulesSection.getBoolean("fast-leaf-decay", false)) {
                Module.get(FastLeafDecay.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new FastLeafDecay())
                );
            } else {
                Module.unregister(FastLeafDecay.ID);
            }

            if (modulesSection.getBoolean("grass-drops", false)) {
                Module.get(GrassDrops.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new GrassDrops())
                );
            } else {
                Module.unregister(GrassDrops.ID);
            }

            if (modulesSection.getBoolean("growth-dance", false)) {
                Module.get(GrowthDance.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new GrowthDance())
                );
            } else {
                Module.unregister(GrowthDance.ID);
            }

            if (modulesSection.getBoolean("interactive-harvest", false)) {
                Module.get(InteractiveHarvest.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new InteractiveHarvest())
                );
            } else {
                Module.unregister(InteractiveHarvest.ID);
            }

            if (modulesSection.getBoolean("lumberjack", false)) {
                Module.get(Lumberjack.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new Lumberjack())
                );
            } else {
                Module.unregister(Lumberjack.ID);
            }

            if (modulesSection.getBoolean("rejuvenated-bushes", false)) {
                Module.get(RejuvenatedBushes.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new RejuvenatedBushes())
                );
            } else {
                Module.unregister(RejuvenatedBushes.ID);
            }

            if (modulesSection.getBoolean("sapling-replant", false)) {
                Module.get(SaplingReplant.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new SaplingReplant())
                );
            } else {
                Module.unregister(SaplingReplant.ID);
            }

            if (modulesSection.getBoolean("sniffer-drops", false)) {
                String serverVersion = Bukkit.getVersion();
                if (!serverVersion.contains("1.20") && !serverVersion.contains("1.21")) {
                    GardeningTweaks.getInstance().getLogger().severe("The 'sniffer-drops' module requires versions 1.20 and above to function");
                }

                Module.get(SnifferDrops.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new SnifferDrops())
                );
            } else {
                Module.unregister(SnifferDrops.ID);
            }

            if (modulesSection.getBoolean("tree-spread", false)) {
                Module.get(TreeSpread.ID).ifPresentOrElse(
                        Module::reload,
                        () -> Module.register(new TreeSpread())
                );
            } else {
                Module.unregister(TreeSpread.ID);
            }
        }
    }
}