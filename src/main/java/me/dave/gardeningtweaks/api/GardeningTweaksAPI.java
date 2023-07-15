package me.dave.gardeningtweaks.api;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.events.GrowthDance;
import org.bukkit.Location;
import org.bukkit.Material;

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
     *
     * @param location Center location
     * @param chance Chance of growing per crop
     * @param radius Radius around the location to find crops
     * @return Whether the method was successfully ran (false, if GrowthDance
     */
    public static boolean growCrops(Location location, double chance, int radius) {
        return GrowthDance.growCrops(location, chance, radius);
    }

    /**
     *
     * @param location Center location
     * @param chance Chance of growing per crop
     * @param radius Radius around the location to find crops
     * @param crops List of materials that will be affected
     * @return Whether the method was successfully ran (false, if GrowthDance
     */
    public static boolean growCrops(Location location, double chance, int radius, @Nullable List<Material> crops) {
        return GrowthDance.growCrops(location, chance, radius, crops);
    }

    /**
     *
     * @param location Center location
     * @param chance Chance of growing per crop
     * @param radius Radius around the location to find crops
     * @param height Height to check above and including the location
     * @param crops List of materials that will be affected
     * @return Whether the method was successfully ran (false, if GrowthDance
     */
    public static boolean growCrops(Location location, double chance, int radius, int height, @Nullable List<Material> crops) {
        return GrowthDance.growCrops(location, chance, radius, height, crops);
    }
}
