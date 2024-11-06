package org.lushplugins.gardeningtweaks.module;

import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.api.events.TreeSpreadBlockEvent;
import org.lushplugins.gardeningtweaks.hooks.HookId;
import org.lushplugins.gardeningtweaks.hooks.RealisticBiomesHook;
import org.lushplugins.gardeningtweaks.util.ConfigUtils;
import org.bukkit.Registry;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushlib.module.Module;
import org.lushplugins.lushlib.registry.RegistryUtils;
import org.lushplugins.lushlib.utils.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
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
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/fancy-trees.yml"));
        ;

        ConfigurationSection treesSection = config.getConfigurationSection("trees");
        if (treesSection != null) {
            treesSection.getValues(false).forEach((treeType, value) -> {
                if (value instanceof ConfigurationSection treeTypeSection) {
                    ConfigurationSection flowerSection = treeTypeSection.getConfigurationSection("flowers");
                    if (flowerSection != null) {
                        List<Material> spreadBlocks = List.copyOf(ConfigUtils.getRegistryValues(treeTypeSection, "spread-blocks", Registry.MATERIAL));
                        List<Material> spreadBlocksOn = List.copyOf(ConfigUtils.getRegistryValues(treeTypeSection, "spread-blocks-on", Registry.MATERIAL));

                        RandomCollection<Material> flowerCollection = new RandomCollection<>();
                        flowerSection.getValues(false).forEach((materialRaw, chance) -> {
                            Material material = RegistryUtils.parseString(materialRaw, Registry.MATERIAL);
                            if (material != null) {
                                flowerCollection.add(material, flowerSection.getDouble(materialRaw));
                            } else {
                                GardeningTweaks.getInstance().getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material.");
                            }
                        });

                        TreeData treeData = new TreeData(spreadBlocks, spreadBlocksOn, flowerCollection);
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
            GardeningTweaks.getInstance().getLogger().warning("There are no valid trees configured, automatically disabling the '" + ID + "' module");
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
        if (event.isCancelled()) {
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

    private void spreadBlocks(TreeData treeData, Location saplingLoc) {
        Location currLocation = saplingLoc.clone().add(-2, -1, -2);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Block currBlock = currLocation.getBlock();

                boolean inSpreadRange = (i >= 1 && i <= 3) && (j >= 1 && j <= 3);
                boolean edgeSpread = (i != 0 && i != 4) || (j != 0 && j != 4);

                if (inSpreadRange || (edgeSpread && GardeningTweaks.getRandom().nextBoolean())) {
                    if (treeData.isSpreadableMaterial(currBlock) && currBlock.getRelative(BlockFace.UP).isPassable()) {
                        List<Material> spreadMaterials = treeData.spreadBlocks();
                        Material spreadMaterial = spreadMaterials.get(GardeningTweaks.getRandom().nextInt(spreadMaterials.size()));

                        if (GardeningTweaks.getInstance().callEvent(new TreeSpreadBlockEvent(currBlock, spreadMaterial, saplingLoc.getBlock()))) {
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
                        if (currBlock.isEmpty() && isSupported(flowerMaterial.createBlockData(), currBlock)) {
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

    private boolean isSupported(BlockData blockData, Block block) {
        String serverVersion = Bukkit.getVersion();
        if (serverVersion.contains("1.16") || serverVersion.contains("1.17") || serverVersion.contains("1.18")) {
            return block.getRelative(BlockFace.DOWN).getType().isSolid();
        } else {
            return blockData.isSupported(block);
        }
    }

    private record TreeData(List<Material> spreadBlocks, List<Material> spreadBlocksOn, RandomCollection<Material> flowerList) {

        public boolean spreadsBlocks() {
            return !spreadBlocks.isEmpty();
        }

        public boolean isSpreadableMaterial(Block block) {
            if (spreadBlocksOn.isEmpty()) {
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
