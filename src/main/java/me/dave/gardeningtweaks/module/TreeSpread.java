package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.api.events.TreeSpreadBlockEvent;
import me.dave.gardeningtweaks.hooks.RealisticBiomesHook;
import me.dave.platyutils.module.Module;
import me.dave.platyutils.utils.RandomCollection;
import me.dave.platyutils.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TreeSpread extends Module implements Listener {
    public static String ID = "TREE_SPREAD";

    private TreeData defaultTreeData;
    private final HashMap<String, TreeData> treeMap = new HashMap<>();

    public TreeSpread() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        File configFile = new File(plugin.getDataFolder(), "modules/tree-spread.yml");
        if (!configFile.exists()) {
            plugin.saveResource("modules/tree-spread.yml", false);
            plugin.getLogger().info("File Created: tree-spread.yml");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        treeMap.clear();

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

    @EventHandler
    public void onTreeGrow(StructureGrowEvent event) {
        if (event.isCancelled()){
            return;
        }

        TreeData treeData = treeMap.getOrDefault(event.getSpecies().toString(), defaultTreeData);
        Location location = event.getLocation();

        if (treeData.spreadsBlocks()) {
            spreadBlocks(treeData, location);
        }

        RandomCollection<Material> flowerCollection = treeData.getFlowerCollection();
        if (flowerCollection != null) {
            growFlowers(flowerCollection, location);
        }
    }

    public void spreadBlocks(TreeData treeData, Location saplingLoc) {
        Location currLocation = saplingLoc.clone().add(-2, -1, -2);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Block currBlock = currLocation.getBlock();

                if ((i >= 1 && i <= 3) && (j >= 1 && j <= 3)) {
                    if (treeData.isSpreadableMaterial(currBlock) && currBlock.getRelative(BlockFace.UP).isPassable()) {
                        List<Material> spreadMaterials = treeData.getSpreadMaterials();
                        Material spreadMaterial = spreadMaterials.get(GardeningTweaks.getRandom().nextInt(spreadMaterials.size()));
                        if (!GardeningTweaks.callEvent(new TreeSpreadBlockEvent(currBlock, spreadMaterial, saplingLoc.getBlock()))) {
                            continue;
                        }
                        setBlockMaterial(currBlock, spreadMaterial);
                    }
                } else if ((i != 0 && i != 4) || (j != 0 && j != 4)) {
                    if (GardeningTweaks.getRandom().nextBoolean()) {
                        if (treeData.isSpreadableMaterial(currBlock) && currBlock.getRelative(BlockFace.UP).isPassable()) {
                            List<Material> spreadMaterials = treeData.getSpreadMaterials();
                            Material spreadMaterial = spreadMaterials.get(GardeningTweaks.getRandom().nextInt(spreadMaterials.size()));
                            if (!GardeningTweaks.callEvent(new TreeSpreadBlockEvent(currBlock, spreadMaterial, saplingLoc.getBlock()))) {
                                continue;
                            }
                            setBlockMaterial(currBlock, spreadMaterial);
                        }
                    }
                }

                currLocation.add(0, 0, 1);
            }
            currLocation.add(1, 0, -5);
        }
    }

    public void growFlowers(RandomCollection<Material> flowerCollection, Location saplingLoc) {
        Location currLocation = saplingLoc.clone().add(-1, 0, -1);


        Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == 1 & j == 1) {
                        continue;
                    }

                    Block currBlock = currLocation.getBlock();

                    if (GardeningTweaks.getRandom().nextInt(2) < 1) {
                        Material flowerMaterial = flowerCollection.next();
                        if (currBlock.isEmpty() && flowerMaterial.createBlockData().isSupported(currBlock)) {
                            if (!GardeningTweaks.callEvent(new TreeSpreadBlockEvent(currBlock, flowerMaterial, saplingLoc.getBlock()))) {
                                continue;
                            }

                            setBlockMaterial(currBlock, flowerMaterial);
                        }
                    }

                    currLocation.add(0, 0, 1);
                }
                currLocation.add(1, 0, -3);
            }
        }, 1);
    }

    private void setBlockMaterial(Block block, Material material) {
        GardeningTweaks.getInstance().getHook(RealisticBiomesHook.ID).ifPresentOrElse(hook -> ((RealisticBiomesHook) hook).setBlockType(block, material), () -> block.setType(material));
    }

    private static class TreeData {
        private final List<Material> spreadBlocks = new ArrayList<>();
        private final List<Material> spreadBlocksOn = new ArrayList<>();
        private final RandomCollection<Material> flowerList = new RandomCollection<>();

        public TreeData(List<String> spreadBlocks, List<String> spreadBlocksOn, HashMap<String, Double> flowerList) {
            spreadBlocks.forEach(materialRaw -> {
                StringUtils.getEnum(materialRaw, Material.class).ifPresentOrElse(
                    this.spreadBlocks::add,
                    () -> GardeningTweaks.getInstance().getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material.")
                );
            });
            spreadBlocksOn.forEach(materialRaw -> {
                StringUtils.getEnum(materialRaw, Material.class).ifPresentOrElse(
                    this.spreadBlocksOn::add,
                    () -> GardeningTweaks.getInstance().getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material.")
                );
            });
            flowerList.forEach((materialRaw, weight) -> {
                StringUtils.getEnum(materialRaw, Material.class).ifPresentOrElse(
                    material -> this.flowerList.add(material, weight),
                    () -> GardeningTweaks.getInstance().getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material.")
                );
            });
        }

        public boolean spreadsBlocks() {
            return spreadBlocks.size() > 0;
        }

        public List<Material> getSpreadMaterials() {
            return spreadBlocks;
        }

        public boolean isSpreadableMaterial(Block block) {
            if (spreadBlocksOn.size() == 0) {
                Collection<BoundingBox> boundingBoxes = block.getCollisionShape().getBoundingBoxes();
                if (boundingBoxes.size() == 1) {
                    BoundingBox boundingBox = boundingBoxes.iterator().next();
                    return boundingBox.getWidthX() == 1.0 && boundingBox.getWidthZ() == 1.0 && boundingBox.getHeight() == 1.0;
                }
            }
            return spreadBlocksOn.contains(block.getType());
        }

        public RandomCollection<Material> getFlowerCollection() {
            return flowerList;
        }
    }
}
