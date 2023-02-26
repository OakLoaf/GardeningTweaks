package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.datamanager.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Random;

public class ComposterSpreader implements Listener {
    private final Random random = new Random();
    private final HashSet<Location> composterLocationList = new HashSet<>();

    public ComposterSpreader() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            ConfigManager.ComposterSpreader composterSpreader = GardeningTweaks.getConfigManager().getComposterSpreader();
            composterLocationList.forEach(location -> {
                Block block = location.getBlock();

                if (block.getType() != Material.COMPOSTER) {
                    composterLocationList.remove(location);
                    return;
                }

                if (!(block.getBlockData() instanceof Levelled composterData)) return;
                if (composterData.getLevel() == 0) return;

                if (random.nextInt(100) < composterSpreader.chance()) {
                    if (growCrops(composterSpreader, location)) {
                        if (composterData.getLevel() == composterData.getMaximumLevel()) composterData.setLevel(composterData.getLevel() - 2);
                        else composterData.setLevel(composterData.getLevel() - 1);
                        block.setBlockData(composterData);

                        Location blockCenter = location.clone().add(0.5, 1, 0.5);
                        World world = blockCenter.getWorld();
                        if (world != null) {
                            world.spawnParticle(Particle.COMPOSTER, blockCenter, 20, 0.4, 0, 0.4);
                            world.playSound(blockCenter, Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1f, 1f);
                        }
                    }
                }
            });
        }, 600, GardeningTweaks.getConfigManager().getComposterSpreader().timer());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() == Material.COMPOSTER) composterLocationList.add(block.getLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.COMPOSTER) composterLocationList.remove(block.getLocation());
    }

    @EventHandler
    public void onBlockPhysicsUpdate(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.COMPOSTER) composterLocationList.add(block.getLocation());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.COMPOSTER) composterLocationList.add(block.getLocation());
    }

    private boolean growCrops(ConfigManager.ComposterSpreader composterSpreader, Location location) {
        Location currLocation = location.clone();
        boolean grownCrops = false;

        for (int indexX = -2; indexX < 3; indexX++) {
            for (int indexZ = -2; indexZ < 3; indexZ++) {
                Block currBlock = currLocation.clone().add(indexX, 0, indexZ).getBlock();

                if (currBlock.getBlockData() instanceof Ageable crop && composterSpreader.blocks().contains(currBlock.getType())) {
                    if (random.nextBoolean()) {
                        if (crop.getAge() == crop.getMaximumAge()) continue;
                        int newAge = crop.getAge() + 1 + random.nextInt(3);
                        int maxAge = crop.getMaximumAge();
                        if (newAge > maxAge) newAge = maxAge;
                        crop.setAge(newAge);
                        currBlock.setBlockData(crop);
                        grownCrops = true;
                    }
                }
            }
        }

        return grownCrops;
    }
}
