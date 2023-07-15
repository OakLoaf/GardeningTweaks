package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.api.events.CropGrowEvent;
import me.dave.gardeningtweaks.api.events.PlayerGrowthDanceEvent;
import me.dave.gardeningtweaks.data.ConfigManager;
import me.dave.gardeningtweaks.utils.GardeningMode;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GrowthDance implements Listener {
    private static final Random random = new Random();
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final HashSet<UUID> cooldownList = new HashSet<>();

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.isCancelled()) return;
        ConfigManager.GrowthDance growthDance = GardeningTweaks.getConfigManager().getGrowthDanceConfig();
        if (growthDance.mode() == GardeningMode.DISABLED) return;
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (cooldownList.contains(player.getUniqueId())) return;
        if (!GardeningTweaks.callEvent(new PlayerGrowthDanceEvent(player))) return;
        cooldownList.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin, () -> cooldownList.remove(player.getUniqueId()), growthDance.cooldownLength());
        growCrops(player.getLocation(), 0.5, 2, growthDance.blocks());
    }

    public static boolean growCrops(Location location, double chance, int radius) {
        return growCrops(location, chance, radius, 2, null);
    }

    public static boolean growCrops(Location location, double chance, int radius, @Nullable List<Material> crops) {
        return growCrops(location, chance, radius, 2, crops);
    }

    public static boolean growCrops(Location location, double chance, int radius, int height, @Nullable List<Material> crops) {
        Location currLocation = location.clone();

        for (int indexY = 0; indexY < height; indexY++) {
            for (int indexX = -radius; indexX <= radius; indexX++) {
                for (int indexZ = -radius; indexZ <= radius; indexZ++) {
                    Block currBlock = currLocation.clone().add(indexX, indexY, indexZ).getBlock();

                    if (currBlock.getBlockData() instanceof Ageable crop && (crops == null || crops.contains(currBlock.getType()))) {
                        if (random.nextBoolean()) {
                            if (!GardeningTweaks.callEvent(new CropGrowEvent(currBlock))) continue;

                            int newAge = crop.getAge() + random.nextInt(3);
                            int maxAge = crop.getMaximumAge();
                            if (newAge > maxAge) newAge = maxAge;
                            crop.setAge(newAge);
                            currBlock.setBlockData(crop);
                        }
                    }
                }
            }
        }
        return true;
    }
}
