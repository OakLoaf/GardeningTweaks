package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.api.events.TreeSpreadBlockEvent;
import me.dave.gardeningtweaks.hooks.HookId;
import me.dave.gardeningtweaks.hooks.RealisticBiomesHook;
import me.dave.platyutils.listener.EventListener;
import me.dave.platyutils.module.Module;
import me.dave.platyutils.utils.RandomCollection;
import me.dave.platyutils.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.util.*;

public class FancyTrees extends Module implements EventListener {
    public static final String ID = "FANCY_TREES";
    private static final EnumSet<BlockFace> HORIZONTAL_BLOCK_FACES = EnumSet.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    private HashMap<String, TreeData> treeMap;

    public FancyTrees() {
        super(ID);
    }

    @Override
    public void onEnable() {
        treeMap = new HashMap<>();
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/fancy-trees.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/fancy-trees.yml"));;

        ConfigurationSection treesSection = config.getConfigurationSection("trees");
        if (treesSection != null) {
            treesSection.getKeys(false).forEach(treeType -> {
                ConfigurationSection treeTypeSection = treesSection.getConfigurationSection(treeType);
                if (treeTypeSection != null) {
                    ConfigurationSection flowerSection = treeTypeSection.getConfigurationSection("flowers");
                    if (flowerSection != null) {
                        List<Material> spreadBlocks = treeTypeSection.getStringList("spread-blocks").stream()
                            .map(materialRaw -> {
                                Optional<Material> optionalMaterial = StringUtils.getEnum(materialRaw, Material.class);
                                if (optionalMaterial.isPresent()) {
                                    return optionalMaterial.get();
                                } else {
                                    GardeningTweaks.getInstance().getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material.");
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .toList();

                        List<Material> spreadBlocksOn = treeTypeSection.getStringList("spread-blocks-on").stream()
                            .map(materialRaw -> {
                                Optional<Material> optionalMaterial = StringUtils.getEnum(materialRaw, Material.class);
                                if (optionalMaterial.isPresent()) {
                                    return optionalMaterial.get();
                                } else {
                                    GardeningTweaks.getInstance().getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material.");
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .toList();

                        RandomCollection<Material> flowerCollection = new RandomCollection<>();
                        flowerSection.getKeys(false).forEach(materialRaw -> StringUtils.getEnum(materialRaw, Material.class).ifPresentOrElse(
                            material -> flowerCollection.add(material, flowerSection.getDouble(materialRaw)),
                            () -> GardeningTweaks.getInstance().getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material."))
                        );

                        TreeData treeData = new TreeData(spreadBlocks,spreadBlocksOn, flowerCollection);
                        if (treeType.equalsIgnoreCase("OAK")) {
                            treeMap.put("TREE", treeData);
                        } else if (treeType.equalsIgnoreCase("BIG_OAK")) {
                            treeMap.put("BIG_TREE", treeData);
                        } else {
                            treeMap.put(treeType.toUpperCase(), treeData);
                        }
                    }
                }
            });
        } else {
            GardeningTweaks.getInstance().getLogger().warning("There are no valid trees configured, automatically disabling the '" + ID  + "' module");
            disable();
        }
    }

    @Override
    protected void onDisable() {
        if (treeMap != null) {
            treeMap.clear();
            treeMap = null;
        }
    }

    @EventHandler
    public void onTreeGrow(StructureGrowEvent event) {
        if (event.isCancelled()){
            return;
        }

        TreeData treeData = treeMap.getOrDefault(event.getSpecies().toString(), treeMap.get("DEFAULT"));
        if (treeData != null) {
            Location location = event.getLocation();
            if (treeData.spreadsBlocks()) {
                spreadBlocks(treeData, location);
            }

            RandomCollection<Material> flowerCollection = treeData.getFlowerCollection();
            if (flowerCollection != null) {
                growFlowers(flowerCollection, location);
            }
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
                        if (!GardeningTweaks.getInstance().callEvent(new TreeSpreadBlockEvent(currBlock, spreadMaterial, saplingLoc.getBlock()))) {
                            continue;
                        }
                        setBlockMaterial(currBlock, spreadMaterial);
                    }
                } else if ((i != 0 && i != 4) || (j != 0 && j != 4)) {
                    if (GardeningTweaks.getRandom().nextBoolean()) {
                        if (treeData.isSpreadableMaterial(currBlock) && currBlock.getRelative(BlockFace.UP).isPassable()) {
                            List<Material> spreadMaterials = treeData.getSpreadMaterials();
                            Material spreadMaterial = spreadMaterials.get(GardeningTweaks.getRandom().nextInt(spreadMaterials.size()));
                            if (!GardeningTweaks.getInstance().callEvent(new TreeSpreadBlockEvent(currBlock, spreadMaterial, saplingLoc.getBlock()))) {
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
                            if (!GardeningTweaks.getInstance().callEvent(new TreeSpreadBlockEvent(currBlock, flowerMaterial, saplingLoc.getBlock()))) {
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
        GardeningTweaks.getInstance().getHook(HookId.REALISTIC_BIOMES.toString()).ifPresentOrElse(hook -> ((RealisticBiomesHook) hook).setBlockType(block, material), () -> block.setType(material));
        if (block.getBlockData() instanceof Directional directional) {
            BlockFace[] blockFaces = HORIZONTAL_BLOCK_FACES.toArray(new BlockFace[0]);
            directional.setFacing(blockFaces[GardeningTweaks.getRandom().nextInt(blockFaces.length)]);
            block.setBlockData(directional);
        }
    }

    private static class TreeData {
        private final List<Material> spreadBlocks;
        private final List<Material> spreadBlocksOn;
        private final RandomCollection<Material> flowerList;

        public TreeData(List<Material> spreadBlocks, List<Material> spreadBlocksOn, RandomCollection<Material> flowerList) {
            this.spreadBlocks = spreadBlocks;
            this.spreadBlocksOn = spreadBlocksOn;
            this.flowerList = flowerList;
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
