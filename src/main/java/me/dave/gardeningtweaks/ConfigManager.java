package me.dave.gardeningtweaks;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();

    private BonemealFlowers bonemealFlowers;
    private ComposterSpreader composterSpreader;
    private CustomComposterOutput customComposterOutput;
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

        bonemealFlowers = new BonemealFlowers(config.getBoolean("bonemeal-flowers.enabled", false));
        composterSpreader = new ComposterSpreader(config.getBoolean("composter-spreader.enabled", false), config.getInt("composter-spreader.timer", 10) * 20, (int) Math.round(config.getDouble("composter-spreader.chance", 50)), config.getStringList("composter-spreader.blocks").stream().map((string) -> {
            try {
                return Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                plugin.getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return null;
            }
        }).filter(Objects::nonNull).toList());
        customComposterOutput = new CustomComposterOutput(config.getBoolean("custom-composter-output.enabled", false), config.getStringList("custom-composter-output.items").stream().map((string) -> {
            try {
                return Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                plugin.getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return null;
            }
        }).filter(Objects::nonNull).toList());
        customGrassDrops = new CustomGrassDrops(config.getBoolean("custom-grass-drops.enabled", false), config.getStringList("custom-grass-drops.items").stream().map((string) -> {
            try {
                return Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                plugin.getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return null;
            }
        }).filter(Objects::nonNull).toList());
        decoarsify = new Decoarsify(config.getBoolean("decoarsify.enabled", false));
        dynamicTrample = new DynamicTrample(config.getBoolean("dynamic-trample.enabled", false), config.getBoolean("dynamic-trample.feather-falling", false), config.getBoolean("dynamic-trample.creative-mode", false));
        fastLeafDecay = new FastLeafDecay(config.getBoolean("fast-leaf-decay.enabled", false), config.getBoolean("fast-leaf-decay.sounds", false), config.getBoolean("fast-leaf-decay.particles", false));
        growthDance = new GrowthDance(parseGardeningMode(config.getString("growth-dance.enabled", "DEFAULT")), (int) Math.round(20 / (double) config.getInt("growth-dance.growth-rate")), config.getStringList("growth-dance.blocks").stream().map((string) -> {
            try {
                return Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                plugin.getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return null;
            }
        }).filter(Objects::nonNull).toList());
        interactiveHarvest = new InteractiveHarvest(config.getBoolean("interactive-harvest.enabled", false), config.getStringList("interactive-harvest.blocks").stream().map((string) -> {
            try {
                return Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                plugin.getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return null;
            }
        }).filter(Objects::nonNull).toList());
        lumberjack = new Lumberjack(parseGardeningMode(config.getString("lumberjack.mode", "DEFAULT").toUpperCase()), config.getStringList("lumberjack.blocks").stream().map((string) -> {
            try {
                return Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                plugin.getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return null;
            }
        }).filter(Objects::nonNull).toList());
        rejuvenatedBushes = new RejuvenatedBushes(config.getBoolean("rejuvenated-bushes.enabled", false));

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

    public BonemealFlowers getBonemealFlowersConfig() {
        return bonemealFlowers;
    }

    public ComposterSpreader getComposterSpreader() {
        return composterSpreader;
    }

    public CustomComposterOutput getCustomComposterOutput() {
        return customComposterOutput;
    }

    public CustomGrassDrops getCustomGrassDropsConfig() {
        return customGrassDrops;
    }
    public Decoarsify getDecoarsifyConfig() {
        return decoarsify;
    }
    public DynamicTrample getDynamicTrampleConfig() {
        return dynamicTrample;
    }
    public FastLeafDecay getFastLeafDecayConfig() {
        return fastLeafDecay;
    }
    public GrowthDance getGrowthDanceConfig() {
        return growthDance;
    }
    public InteractiveHarvest getInteractiveHarvestConfig() {
        return interactiveHarvest;
    }
    public Lumberjack getLumberjackConfig() {
        return lumberjack;
    }
    public RejuvenatedBushes getRejuvenatedBushesConfig() {
        return rejuvenatedBushes;
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


    public record BonemealFlowers(boolean enabled) {}
    public record ComposterSpreader(boolean enabled, int timer, int chance, List<Material> blocks) {}
    public record CustomComposterOutput(boolean enabled, List<Material> items) {}
    public record CustomGrassDrops(boolean enabled, List<Material> items) {}
    public record Decoarsify(boolean enabled) {}
    public record DynamicTrample(boolean enabled, boolean featherFalling, boolean creativeMode) {}
    public record FastLeafDecay(boolean enabled, boolean sounds, boolean particles) {}
    public record GrowthDance(GardeningMode mode, int cooldownLength, List<Material> blocks) {}
    public record InteractiveHarvest(boolean enabled, List<Material> blocks) {}
    public record Lumberjack(GardeningMode mode, List<Material> blocks) {}
    public record RejuvenatedBushes(boolean enabled) {}
}
