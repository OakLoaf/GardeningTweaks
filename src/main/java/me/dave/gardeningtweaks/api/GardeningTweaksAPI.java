package me.dave.gardeningtweaks.api;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.events.BonemealFlowers;
import me.dave.gardeningtweaks.events.GrowthDance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

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
     */
    public static void growCrops(Location location, double chance, int radius) {
        GrowthDance.growCrops(location, chance, radius);
    }

    /**
     * Grow crops in around a location
     * @param location Center location
     * @param chance Chance of growing per crop
     * @param radius Radius around the location to find crops
     * @param crops List of materials that will be affected
     */
    public static void growCrops(Location location, double chance, int radius, @Nullable List<Material> crops) {
        GrowthDance.growCrops(location, chance, radius, crops);
    }

    /**
     * Grow crops in around a location
     * @param location Center location
     * @param chance Chance of growing per crop
     * @param radius Radius around the location to find crops
     * @param height Height to check above and including the location
     * @param crops List of materials that will be affected
     */
    public static void growCrops(Location location, double chance, int radius, int height, @Nullable List<Material> crops) {
        GrowthDance.growCrops(location, chance, radius, height, crops);
    }

    /**
     * Grow a single block flower into a double tall flower
     * @param block The block to change
     * @param flowerType The double tall flower to change to
     */
    public static void bonemealFlower(Block block, Material flowerType) {
        BonemealFlowers.bonemealFlower(null, null, block, flowerType);
    }

    /**
     * Grow a single block flower into a double tall flower
     * @param player The player interacting with the flower
     * @param mainHand The item in the player's main hand
     * @param block The block to change
     * @param flowerType The double tall flower to change to
     */
    public static void bonemealFlower(Player player, ItemStack mainHand, Block block, Material flowerType) {
        BonemealFlowers.bonemealFlower(player, mainHand, block, flowerType);
    }
}
