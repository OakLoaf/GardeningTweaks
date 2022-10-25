package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.utilities.GrowthDance;
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

public class PlayerEvents implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final HashSet<UUID> cooldownList = new HashSet<>();
    private final Random random = new Random();

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking() && GardeningTweaks.configManager.getGrowthDanceMode() != GrowthDance.OFF) {
            Player player = event.getPlayer();
            if (cooldownList.contains(player.getUniqueId())) return;
            cooldownList.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(plugin, () -> cooldownList.remove(player.getUniqueId()), 10);
            growCrops(player.getLocation());
        }
    }

    public void growCrops(Location location) {
        Location currLocation = location.clone().add(-2, 0, -2);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    Block currBlock = currLocation.getBlock();

                    if (currBlock.getBlockData() instanceof Ageable crop) {
                        if (random.nextBoolean()) {
                            int newAge = crop.getAge() + random.nextInt(3);
                            int maxAge = crop.getMaximumAge();
                            if (newAge > maxAge) newAge = maxAge;
                            crop.setAge(newAge);
                            currBlock.setBlockData(crop);
                        }
                    }

                    currLocation.add(0, 0, 1);
                }
                currLocation.add(1, 0, -5);
            }
            currLocation.add(-5, 1, 0);
        }
    }
}
