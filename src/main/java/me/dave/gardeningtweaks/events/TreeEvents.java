package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.dependencies.RealisticBiomesHook;
import me.dave.gardeningtweaks.utilities.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;
import java.util.Random;

public class TreeEvents implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final RealisticBiomesHook realisticBiomesHook;
    private final Random random = new Random();

    public TreeEvents(RealisticBiomesHook realisticBiomesHook) {
        this.realisticBiomesHook = realisticBiomesHook;
    }

    @EventHandler
    public void onTreeGrow(StructureGrowEvent event) {
        TreeType treeType = event.getSpecies();
        Location location = event.getLocation();

        if (GardeningTweaks.configManager.doesSpreadBlocks(treeType)) spreadBlocks(treeType, location);

        RandomCollection<Material> flowerCollection = GardeningTweaks.configManager.getTreeFlowers(treeType);
        if (flowerCollection != null) growFlowers(location, flowerCollection);
    }

    public void spreadBlocks(TreeType treeType, Location saplingLocation) {
        Location currLocation = saplingLocation.clone().add(-2, -1, -2);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Block currBlock = currLocation.getBlock();

                if ((i >= 1 && i <= 3) && (j >= 1 && j <= 3)) {
                    if (GardeningTweaks.configManager.isSpreadableMaterial(treeType, currBlock) && currBlock.getRelative(BlockFace.UP).isPassable()) {
                        List<Material> spreadBlocks = GardeningTweaks.configManager.getTreeSpreadBlock(treeType);
                        setBlockMaterial(currBlock, spreadBlocks.get(random.nextInt(spreadBlocks.size())));
                    }
                } else if ((i != 0 && i != 4) || (j != 0 && j != 4)) {
                    if (random.nextBoolean()) {
                        if (GardeningTweaks.configManager.isSpreadableMaterial(treeType, currBlock) && currBlock.getRelative(BlockFace.UP).isPassable()) {
                            List<Material> spreadBlocks = GardeningTweaks.configManager.getTreeSpreadBlock(treeType);
                            setBlockMaterial(currBlock, spreadBlocks.get(random.nextInt(spreadBlocks.size())));
                        }
                    }
                }

                currLocation.add(0, 0, 1);
            }
            currLocation.add(1, 0, -5);
        }
    }

    public void growFlowers(Location saplingLocation, RandomCollection<Material> flowerCollection) {
        Random random = new Random();
        Location currLocation = saplingLocation.clone().add(-1, 0, -1);


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == 1 & j == 1) continue;
                    Block currBlock = currLocation.getBlock();

                    if (random.nextInt(2) < 1) {
                        Material flowerMaterial = flowerCollection.next();
                        if (currBlock.isEmpty() && flowerMaterial.createBlockData().isSupported(currBlock)) {
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
        if (realisticBiomesHook != null) realisticBiomesHook.setBlockType(block, material);
        else block.setType(material);
    }
}
