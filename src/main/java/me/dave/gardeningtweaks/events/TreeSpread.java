package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.api.events.TreeSpreadBlockEvent;
import me.dave.gardeningtweaks.data.TreeData;
import me.dave.gardeningtweaks.utils.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;

public class TreeSpread implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();

    @EventHandler
    public void onTreeGrow(StructureGrowEvent event) {
        if (event.isCancelled()) return;
        TreeData treeData = GardeningTweaks.getConfigManager().getTreeData(event.getSpecies());
        Location location = event.getLocation();

        if (treeData.spreadsBlocks()) spreadBlocks(treeData, location);

        RandomCollection<Material> flowerCollection = treeData.getFlowerCollection();
        if (flowerCollection != null) growFlowers(flowerCollection, location);
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
                        if (!GardeningTweaks.callEvent(new TreeSpreadBlockEvent(currBlock, spreadMaterial, saplingLoc.getBlock()))) continue;
                        setBlockMaterial(currBlock, spreadMaterial);
                    }
                } else if ((i != 0 && i != 4) || (j != 0 && j != 4)) {
                    if (GardeningTweaks.getRandom().nextBoolean()) {
                        if (treeData.isSpreadableMaterial(currBlock) && currBlock.getRelative(BlockFace.UP).isPassable()) {
                            List<Material> spreadMaterials = treeData.getSpreadMaterials();
                            Material spreadMaterial = spreadMaterials.get(GardeningTweaks.getRandom().nextInt(spreadMaterials.size()));
                            if (!GardeningTweaks.callEvent(new TreeSpreadBlockEvent(currBlock, spreadMaterial, saplingLoc.getBlock()))) continue;
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


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == 1 & j == 1) continue;
                    Block currBlock = currLocation.getBlock();

                    if (GardeningTweaks.getRandom().nextInt(2) < 1) {
                        Material flowerMaterial = flowerCollection.next();
                        if (currBlock.isEmpty() && flowerMaterial.createBlockData().isSupported(currBlock)) {
                            if (!GardeningTweaks.callEvent(new TreeSpreadBlockEvent(currBlock, flowerMaterial, saplingLoc.getBlock()))) continue;
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
        if (GardeningTweaks.realisticBiomesHook != null) GardeningTweaks.realisticBiomesHook.setBlockType(block, material);
        else block.setType(material);
    }
}
