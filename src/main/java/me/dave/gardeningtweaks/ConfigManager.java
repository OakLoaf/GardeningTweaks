package me.dave.gardeningtweaks;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public class ConfigManager {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();

    private CustomGrassDrops customGrassDrops;
    private Decoarsify decoarsify;
    private DynamicTrample dynamicTrample;
    private FastLeafDecay fastLeafDecay;
    private GrowthDance growthDance;
    private InteractiveHarvest interactiveHarvest;
    private Lumberjack lumberjack;
    private RejuvenatedBushes rejuvenatedBushes;

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

        interactiveHarvest = new InteractiveHarvest(config.getBoolean("interactive-harvest.enabled", false), config.getStringList("interactive-harvest.blocks").stream().map(Material::valueOf).toList());
        dynamicTrample = new DynamicTrample(config.getBoolean("dynamic-trample.enabled", false), config.getBoolean("dynamic-trample.feather-falling", false), config.getBoolean("dynamic-trample.creative-mode", false));
        lumberjack = new Lumberjack(GardeningMode.valueOf(config.getString("lumberjack.mode", "DEFAULT").toUpperCase()), config.getStringList("lumberjack.blocks").stream().map(Material::valueOf).toList());
        fastLeafDecay = new FastLeafDecay(config.getBoolean("fast-leaf-decay.enabled", false));
        decoarsify = new Decoarsify(config.getBoolean("decoarsify.enabled", false));
        growthDance = new GrowthDance(GardeningMode.valueOf(config.getString("growth-dance.enabled", "DEFAULT")), config.getStringList("growth-dance.blocks").stream().map(Material::valueOf).toList());
        rejuvenatedBushes = new RejuvenatedBushes(config.getBoolean("rejuvenated-bushes.enabled", false));
        customGrassDrops = new CustomGrassDrops(config.getBoolean("custom-grass-drops.enabled", false), config.getStringList("custom-grass-drops.blocks").stream().map(Material::valueOf).toList());

        defaultTreeData = new TreeData("DEFAULT", List.of("GRASS_BLOCK"), List.of("DIRT", "COARSE_DIRT"), new HashMap<>());
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
                    defaultTreeData = new TreeData("DEFAULT", treeTypeSection.getStringList("spread-blocks"), treeTypeSection.getStringList("spread-blocks-on"), flowerMap);
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
                        TreeData treeData = new TreeData(treeType, treeTypeSection.getStringList("spread-blocks"), treeTypeSection.getStringList("spread-blocks-on"), flowerMap);
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

    public boolean isInteractiveHarvestEnabled() {
        return interactiveHarvest.enabled;
    }

    public InteractiveHarvest getInteractiveHarvestConfig() {
        return interactiveHarvest;
    }

    public boolean isDynamicTrampleEnabled() {
        return dynamicTrample.enabled;
    }

    public DynamicTrample getDynamicTrampleConfig() {
        return dynamicTrample;
    }

    public GardeningMode getLumberjackMode() {
        return lumberjack.mode;
    }

    public Lumberjack getLumberjackConfig() {
        return lumberjack;
    }

    public boolean isFastLeafDecayEnabled() {
        return fastLeafDecay.enabled;
    }

    public boolean isDecoarsifyEnabled() {
        return decoarsify.enabled;
    }

    public GardeningMode getGrowthDanceMode() {
        return growthDance.mode;
    }

    public GrowthDance getGrowthDanceConfig() {
        return growthDance;
    }

    public boolean isRejuvenatedBushesEnabled() {
        return rejuvenatedBushes.enabled;
    }

    public boolean isCustomGrassDropsEnabled() {
        return customGrassDrops.enabled;
    }

    public CustomGrassDrops getCustomGrassDropsConfig() {
        return customGrassDrops;
    }

    public TreeData getTreeData(TreeType treeType) {
        return treeMap.getOrDefault(treeType.toString(), defaultTreeData);
    }


    public record CustomGrassDrops(boolean enabled, List<Material> blocks) {}
    public record Decoarsify(boolean enabled) {}
    public record DynamicTrample(boolean enabled, boolean featherFalling, boolean creativeMode) {}
    public record FastLeafDecay(boolean enabled) {}
    public record GrowthDance(GardeningMode mode, List<Material> blocks) {}
    public record InteractiveHarvest(boolean enabled, List<Material> blocks) {}
    public record Lumberjack(GardeningMode mode, List<Material> blocks) {}
    public record RejuvenatedBushes(boolean enabled) {}
}
