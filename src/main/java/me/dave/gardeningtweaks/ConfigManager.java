package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.utilities.RandomCollection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private String prefix;
    private boolean interactiveHarvest;
    private boolean dynamicTrampleFeatherFall;
    private boolean dynamicTrampleCreative;
    private boolean fastLeafDecay;
    private boolean decoarsify;
    private boolean growthDance;
    private final List<Material> grassDrops = new ArrayList<>();
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
        defaultTreeData = new TreeData("DEFAULT", List.of("GRASS_BLOCK"), List.of("DIRT", "COARSE_DIRT"), new HashMap<>());

        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", ""));
        interactiveHarvest = config.getBoolean("interactive-harvest", false);
        dynamicTrampleFeatherFall = config.getBoolean("dynamic-trample.feather-falling", false);
        dynamicTrampleCreative = config.getBoolean("dynamic-trample.creative-mode", false);
        fastLeafDecay = config.getBoolean("fast-leaf-decay", false);
        config.getStringList("custom-grass-drops").forEach(string -> grassDrops.add(Material.valueOf(string.toUpperCase())));
        decoarsify = config.getBoolean("decoarsify", false);
        growthDance = config.getBoolean("growth-dance", false);

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

    public String getPrefix() {
        return prefix;
    }

    public boolean hasInteractiveHarvest() {
        return interactiveHarvest;
    }

    public boolean hasDynamicTrampleFeatherFall() {
        return dynamicTrampleFeatherFall;
    }

    public boolean hasDynamicTrampleCreative() {
        return dynamicTrampleCreative;
    }

    public boolean hasFastLeafDecay() {
        return fastLeafDecay;
    }

    public boolean hasDecoarsify() {
        return decoarsify;
    }

    public boolean getGrowthDanceMode() {
        return growthDance;
    }

    public List<Material> getGrassDrops() {
        return grassDrops;
    }

    public List<Material> getTreeSpreadBlock(TreeType treeType) {
        return treeMap.getOrDefault(treeType.toString(), defaultTreeData).getSpreadBlocks();
    }

    public boolean doesSpreadBlocks(TreeType treeType) {
        return treeMap.getOrDefault(treeType.toString(), defaultTreeData).getSpreadBlocks().size() > 0;
    }

    public boolean isSpreadableMaterial(TreeType treeType, Block block) {
        List<Material> spreadOnList = treeMap.getOrDefault(treeType.toString(), defaultTreeData).getSpreadBlocksOn();
        if (spreadOnList.size() == 0) {
            Collection<BoundingBox> boundingBoxes = block.getCollisionShape().getBoundingBoxes();
            if (boundingBoxes.size() == 1) {
                BoundingBox boundingBox = boundingBoxes.iterator().next();
                return boundingBox.getWidthX() == 1.0 && boundingBox.getWidthZ() == 1.0 && boundingBox.getHeight() == 1.0;
            }
        }
        return spreadOnList.contains(block.getType());
    }

    public RandomCollection<Material> getTreeFlowers(TreeType treeType) {
        TreeData treeData = treeMap.getOrDefault(treeType.toString(), defaultTreeData);
        return treeData.getFlowerCollection();
    }
}
