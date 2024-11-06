package org.lushplugins.gardeningtweaks.api;

import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.module.BoneMealFlowers;
import org.lushplugins.gardeningtweaks.module.GrowthDance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class GardeningTweaksAPI {

    /**
     * Gets the Plugin Instance for GardeningTweaks
     * @since 1.3
     * @return Plugin Instance
     */
    public static GardeningTweaks getInstance() {
        return GardeningTweaks.getInstance();
    }

    /**
     * Grow crops in around a location
     * @param location Center location
     * @param chance Chance of growing per crop
     * @param radius Radius around the location to find crops
     * @return List of grown blocks
     */
    public static List<Block> growCrops(Location location, double chance, int radius) {
        return GrowthDance.growCrops(location, chance, radius);
    }

    /**
     * Grow crops in around a location
     * @param location Center location
     * @param chance Chance of growing per crop
     * @param radius Radius around the location to find crops
     * @param crops List of materials that will be affected
     * @return List of grown blocks
     */
    public static List<Block> growCrops(Location location, double chance, int radius, @Nullable List<Material> crops) {
        return GrowthDance.growCrops(location, chance, radius, crops);
    }

    /**
     * Grow crops in around a location
     * @param location Center location
     * @param chance Chance of growing per crop
     * @param radius Radius around the location to find crops
     * @param height Height to check above and including the location
     * @param crops List of materials that will be affected
     * @return List of grown blocks
     */
    public static List<Block> growCrops(Location location, double chance, int radius, int height, @Nullable List<Material> crops) {
        return GrowthDance.growCrops(location, chance, radius, height, crops);
    }

    /**
     * Grow a single block flower into a double tall flower
     * @param block The block to change
     * @param flowerType The double tall flower to change to
     * @param chance The chance of success
     */
    public static void boneMealFlower(Block block, Material flowerType, int chance) {
        BoneMealFlowers.boneMealFlower(null, null, block, flowerType, chance);
    }

    /**
     * Grow a single block flower into a double tall flower
     * @param player The player interacting with the flower
     * @param mainHand The item in the player's main hand
     * @param block The block to change
     * @param flowerType The double tall flower to change to
     * @param chance The chance of success
     */
    public static void boneMealFlower(Player player, ItemStack mainHand, Block block, Material flowerType, int chance) {
        BoneMealFlowers.boneMealFlower(player, mainHand, block, flowerType, chance);
    }
}
