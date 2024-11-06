package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.api.events.ComposterCropGrowEvent;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.util.ConfigUtils;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushlib.module.Module;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class ComposterSpreader extends Module implements EventListener {
    public static final String ID = "COMPOSTER_SPREADER";

    private BukkitTask task;
    private HashSet<Location> composterLocations;
    private int chance;
    private int radius;
    private Collection<Material> blocks;
    private Collection<Material> cropBlocks;

    public ComposterSpreader() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/composter-spreader.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/composter-spreader.yml"));

        int timer = config.getInt("timer", 10) * 20;
        chance = (int) Math.round(config.getDouble("chance", 50));
        radius = config.getInt("radius", 2);
        blocks = ConfigUtils.getRegistryValues(config, "blocks", Registry.MATERIAL);
        cropBlocks = ConfigUtils.getRegistryValues(config, "crop-blocks", Registry.MATERIAL);

        composterLocations = new HashSet<>();
        task = Bukkit.getScheduler().runTaskTimer(plugin, () ->  {
            composterLocations.forEach(location -> {
                if (!location.getChunk().isLoaded()) {
                    return;
                }

                Block block = location.getBlock();
                if (block.getType() != Material.COMPOSTER) {
                    composterLocations.remove(location);
                    return;
                }

                if (!(block.getBlockData() instanceof Levelled composterData) || composterData.getLevel() == 0) {
                    return;
                }

                if (GardeningTweaks.getRandom().nextInt(100) < chance) {
                    if (growCrops(location)) {
                        int decrement = composterData.getMaximumLevel() == composterData.getLevel() ? 2 : 1;
                        composterData.setLevel(composterData.getLevel() - decrement);
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
        }, 600, timer);
    }

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (composterLocations != null) {
            composterLocations.clear();
            composterLocations = null;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlockPlaced();
        if (!composterLocations.contains(block.getLocation()) && isValidComposter(block)) {
            composterLocations.add(block.getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getType() == Material.COMPOSTER) {
            composterLocations.remove(block.getLocation());
        }
    }

    @EventHandler
    public void onBlockPhysicsUpdate(BlockPhysicsEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (!composterLocations.contains(block.getLocation()) && isValidComposter(block)) {
            composterLocations.add(block.getLocation());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;
        Block block = event.getClickedBlock();
        if (block != null && !composterLocations.contains(block.getLocation()) && isValidComposter(block)) {
            composterLocations.add(block.getLocation());
        }
    }

    private boolean isValidComposter(Block block) {
        if (!block.getType().equals(Material.COMPOSTER)) {
            return false;
        }

        if (cropBlocks.isEmpty()) {
            return true;
        } else {
            Location composterLocation = block.getLocation();
            for (int indexX = -2; indexX < 3; indexX++) {
                for (int indexZ = -2; indexZ < 3; indexZ++) {
                    Block currBlock = composterLocation.clone().add(indexX, -1, indexZ).getBlock();
                    if (cropBlocks.contains(currBlock.getType())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean growCrops(Location location) {
        Location currLocation = location.clone();
        boolean grownCrops = false;

        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                Block currBlock = currLocation.clone().add(offsetX, 0, offsetZ).getBlock();

                if (currBlock.getBlockData() instanceof Ageable crop && blocks.contains(currBlock.getType())) {
                    if (GardeningTweaks.getRandom().nextBoolean()) {
                        if (crop.getAge() == crop.getMaximumAge()) continue;
                        if (!GardeningTweaks.getInstance().callEvent(new ComposterCropGrowEvent(currBlock))) continue;
                        int newAge = crop.getAge() + 1 + GardeningTweaks.getRandom().nextInt(3);
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
