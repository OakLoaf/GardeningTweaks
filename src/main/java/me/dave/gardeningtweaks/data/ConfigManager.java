package me.dave.gardeningtweaks.data;

import me.dave.gardeningtweaks.utils.GardeningMode;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();

    private final HashMap<String, TreeData> treeMap = new HashMap<>();
    private TreeData defaultTreeData;

    public ConfigManager() {
        plugin.saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        treeMap.clear();

        customSnifferDrops = new CustomSnifferDrops(config.getBoolean("custom-sniffer-drops.enabled", false), config.getStringList("custom-sniffer-drops.items").stream().map((string) -> {
            try {
                return Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                plugin.getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return null;
            }
        }).filter(Objects::nonNull).toList());


        defaultTreeData = new TreeData(List.of("GRASS_BLOCK"), List.of("DIRT", "COARSE_DIRT"), new HashMap<>());
        ConfigurationSection treesSection = config.getConfigurationSection("trees");
        if (treesSection != null) {
            ConfigurationSection treeTypeSection = treesSection.getConfigurationSection("DEFAULT");
            if (treeTypeSection != null) {
                ConfigurationSection flowerSection = treeTypeSection.getConfigurationSection("flowers");
                if (flowerSection != null) {
                    HashMap<String, Double> flowerMap = new HashMap<>();
                    for (String flowerStr : flowerSection.getKeys(false)) {
                        flowerMap.put(flowerStr, flowerSection.getDouble(flowerStr));
                    }
                    defaultTreeData = new TreeData(treeTypeSection.getStringList("spread-blocks"), treeTypeSection.getStringList("spread-blocks-on"), flowerMap);
                }
            }

            for (String treeType : treesSection.getKeys(false)) {
                if (treeType.equals("DEFAULT")) continue;
                treeTypeSection = treesSection.getConfigurationSection(treeType);
                if (treeTypeSection != null) {
                    ConfigurationSection flowerSection = treeTypeSection.getConfigurationSection("flowers");
                    if (flowerSection != null) {
                        HashMap<String, Double> flowerMap = new HashMap<>();
                        for (String flowerStr : flowerSection.getKeys(false)) {
                            flowerMap.put(flowerStr, flowerSection.getDouble(flowerStr));
                        }
                        TreeData treeData = new TreeData(treeTypeSection.getStringList("spread-blocks"), treeTypeSection.getStringList("spread-blocks-on"), flowerMap);
                        if (treeType.equals("OAK")) {
                            treeMap.put("TREE", treeData);
                            treeMap.put("BIG_TREE", treeData);
                        } else {
                            treeMap.put(treeType.toUpperCase(), treeData);
                        }
                    }
                }
            }
        }
    }

    public TreeData getTreeData(TreeType treeType) {
        return treeMap.getOrDefault(treeType.toString(), defaultTreeData);
    }

    private GardeningMode parseGardeningMode(String string) {
        GardeningMode mode = GardeningMode.DEFAULT;
        try {
            mode = GardeningMode.valueOf(string);
        } catch (IllegalArgumentException err) {
            plugin.getLogger().warning("Ignoring " + string + ", that is not a valid mode.");
        }
        return mode;
    }


    public record ComposterSpreader(boolean enabled, int timer, int chance, List<Material> blocks) {}
    public record CustomComposterOutput(boolean enabled, List<Material> items) {}
    public record CustomGrassDrops(boolean enabled, List<Material> items) {}
    public record CustomSnifferDrops(boolean enabled, List<Material> items) {}
    public record Decoarsify(boolean enabled) {}
    public record DynamicTrample(boolean enabled, boolean featherFalling, boolean creativeMode) {}
    public record FastLeafDecay(boolean enabled, boolean sounds, boolean particles, boolean ignorePersistence) {}
    public record GrowthDance(GardeningMode mode, int cooldownLength, List<Material> blocks) {}
    public record InteractiveHarvest(boolean enabled, List<Material> blocks) {}
    public record Lumberjack(GardeningMode mode, List<Material> blocks, boolean ignorePlaced) {}
    public record RejuvenatedBushes(boolean enabled) {}
    public record SaplingReplant(boolean enabled, boolean includePlayerDrops, boolean includeLeafDrops, int leafDelay) {}
}
