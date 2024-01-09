package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.api.events.ComposterCropGrowEvent;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.platyutils.module.Module;
import me.dave.platyutils.utils.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ComposterSpreader extends Module implements Listener {
    public static String ID = "COMPOSTER_SPREADER";

    private BukkitTask task;
    private HashSet<Location> composterLocationList;
    private int chance;
    private List<Material> blocks;

    public ComposterSpreader() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        File configFile = new File(plugin.getDataFolder(), "modules/composter-spreader.yml");
        if (!configFile.exists()) {
            plugin.saveResource("modules/composter-spreader.yml", false);
            plugin.getLogger().info("File Created: composter-spreader.yml");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        int timer = config.getInt("timer", 10) * 20;
        chance = (int) Math.round(config.getDouble("chance", 50));
        blocks = config.getStringList("blocks").stream().map((materialRaw) -> {
            Material material = StringUtils.getEnum(materialRaw, Material.class).orElse(null);
            if (material == null) {
                plugin.getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material.");
            }

            return material;
        }).filter(Objects::nonNull).toList();

        composterLocationList = new HashSet<>();
        task = Bukkit.getScheduler().runTaskTimer(plugin, () ->  {
            composterLocationList.forEach(location -> {
                Block block = location.getBlock();

                if (block.getType() != Material.COMPOSTER) {
                    composterLocationList.remove(location);
                    return;
                }

                if (!(block.getBlockData() instanceof Levelled composterData)) return;
                if (composterData.getLevel() == 0) return;

                if (GardeningTweaks.getRandom().nextInt(100) < chance) {
                    if (growCrops(location)) {
                        composterData.setLevel(composterData.getLevel() - composterData.getLevel() == composterData.getMaximumLevel() ? -2 : 1);
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

        if (composterLocationList != null) {
            composterLocationList.clear();
            composterLocationList = null;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlockPlaced();
        if (block.getType() == Material.COMPOSTER) composterLocationList.add(block.getLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getType() == Material.COMPOSTER) composterLocationList.remove(block.getLocation());
    }

    @EventHandler
    public void onBlockPhysicsUpdate(BlockPhysicsEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getType() == Material.COMPOSTER) composterLocationList.add(block.getLocation());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.COMPOSTER) composterLocationList.add(block.getLocation());
    }

    private boolean growCrops(Location location) {
        Location currLocation = location.clone();
        boolean grownCrops = false;

        for (int indexX = -2; indexX < 3; indexX++) {
            for (int indexZ = -2; indexZ < 3; indexZ++) {
                Block currBlock = currLocation.clone().add(indexX, 0, indexZ).getBlock();

                if (currBlock.getBlockData() instanceof Ageable crop && blocks.contains(currBlock.getType())) {
                    if (GardeningTweaks.getRandom().nextBoolean()) {
                        if (crop.getAge() == crop.getMaximumAge()) continue;
                        if (!GardeningTweaks.callEvent(new ComposterCropGrowEvent(currBlock))) continue;
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
