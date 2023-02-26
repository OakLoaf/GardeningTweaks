package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.datamanager.ConfigManager;
import me.dave.gardeningtweaks.GardeningMode;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class GrowthDance implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final Random random = new Random();
    private final HashSet<UUID> cooldownList = new HashSet<>();

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        ConfigManager.GrowthDance growthDance = GardeningTweaks.getConfigManager().getGrowthDanceConfig();
        if (growthDance.mode() == GardeningMode.DISABLED) return;
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (cooldownList.contains(player.getUniqueId())) return;
        cooldownList.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin, () -> cooldownList.remove(player.getUniqueId()), growthDance.cooldownLength());
        growCrops(growthDance, player.getLocation());
    }

    private void growCrops(ConfigManager.GrowthDance growthDance, Location location) {
        Location currLocation = location.clone();

        for (int indexY = 0; indexY < 2; indexY++) {
            for (int indexX = -2; indexX < 3; indexX++) {
                for (int indexZ = -2; indexZ < 3; indexZ++) {
                    Block currBlock = currLocation.clone().add(indexX, indexY, indexZ).getBlock();

                    if (currBlock.getBlockData() instanceof Ageable crop && growthDance.blocks().contains(currBlock.getType())) {
                        if (random.nextBoolean()) {
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
    }
}
