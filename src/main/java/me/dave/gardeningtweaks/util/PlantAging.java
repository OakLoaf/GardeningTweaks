package me.dave.gardeningtweaks.util;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.api.events.CropGrowEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlantAging {

    /**
     * @param plant The plant to age
     * @return The grown block, or null if no block was grown
     */
    public static @Nullable Block agePlantData(@NotNull Block plant) {
        BlockData blockData = plant.getBlockData();
        if (blockData instanceof Ageable crop) {
            Material cropType = crop.getMaterial();
            boolean verticalCrop = cropType == Material.SUGAR_CANE || cropType == Material.CACTUS;

            if (verticalCrop) {
                if (findPlantEnd(plant, BlockFace.UP).getBlockData() instanceof Ageable plantEnd) {
                    crop = plantEnd;
                }
            }

            if (crop.getAge() == crop.getMaximumAge()) {
                if (verticalCrop) {
                    Block growableBlock = getNextGrowableBlock(plant);
                    if (growableBlock == null) {
                        return null;
                    }

                    if (!GardeningTweaks.getInstance().callEvent(new CropGrowEvent(plant))) {
                        return null;
                    }

                    growableBlock.setType(cropType);
                    return growableBlock;
                }
            } else {
                int increment = GardeningTweaks.getRandom().nextInt(3);
                if (increment == 0) {
                    return null;
                }

                if (!GardeningTweaks.getInstance().callEvent(new CropGrowEvent(plant))) {
                    return null;
                }

                int newAge = Math.min((crop.getAge() + increment), crop.getMaximumAge());
                crop.setAge(newAge);
                plant.setBlockData(crop);
                return plant;
            }
        } else if (blockData instanceof Sapling sapling) {
            int increment = GardeningTweaks.getRandom().nextInt(3);
            if (increment == 0) {
                return null;
            }

            if (!GardeningTweaks.getInstance().callEvent(new CropGrowEvent(plant))) {
                return null;
            }

            int newStage = Math.min((sapling.getStage() + increment), sapling.getMaximumStage());
            sapling.setStage(newStage);
            plant.setBlockData(sapling);
            return plant;
        }

        return null;
    }

    /**
     * @param plant The plant to check
     * @return The next available block for the plant to grow into, or null if none is found
     */
    private static @Nullable Block getNextGrowableBlock(@NotNull Block plant) {
        Material plantType = plant.getType();
        return switch (plantType) {
            case SUGAR_CANE, CACTUS -> {
                Block plantTop = findPlantEnd(plant, BlockFace.UP);
                Block plantBottom = findPlantEnd(plant, BlockFace.DOWN);

                if ((plantTop.getY() - plantBottom.getY() + 1) < 4) {
                    Block growableBlock = plantTop.getRelative(BlockFace.UP);
                    if (growableBlock.getType() == Material.AIR) {
                        yield growableBlock;
                    }
                }

                yield null;
            }
            default -> null;
        };
    }

    /**
     * @param plant The plant to find the end of
     * @param direction The direction to search in
     * @return The block at the end of the plant
     */
    private static Block findPlantEnd(@NotNull Block plant, @NotNull BlockFace direction) {
        Block plantEnd = plant;
        Material plantType = plant.getType();

        boolean endFound = false;
        while (!endFound) {
            Block checkBlock = plantEnd.getRelative(direction);
            if (checkBlock.getType() == plantType) {
                plantEnd = checkBlock;
            } else {
                endFound = true;
            }
        }

        return plantEnd;
    }
}
