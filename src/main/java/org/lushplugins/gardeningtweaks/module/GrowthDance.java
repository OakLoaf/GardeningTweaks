package org.lushplugins.gardeningtweaks.module;

import org.lushplugins.gardeningtweaks.api.events.PlayerGrowthDanceEvent;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.util.ConfigUtils;
import org.lushplugins.gardeningtweaks.util.PlantAging;
import org.bukkit.Registry;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushlib.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class GrowthDance extends Module implements EventListener {
    public static final String ID = "GROWTH_DANCE";

    private HashSet<UUID> cooldownList;
    private Integer cooldownLength;
    private Collection<Material> blockTypes;

    public GrowthDance() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/growth-dance.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/growth-dance.yml"));

        cooldownList = new HashSet<>();

        cooldownLength = config.getInt("growth-rate", 2);
        blockTypes = ConfigUtils.getRegistryValues(config, "blocks", Registry.MATERIAL);
    }

    @Override
    public void onDisable() {
        if (cooldownList != null) {
            cooldownList.clear();
            cooldownList = null;
        }

        blockTypes = null;
        cooldownLength = null;
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.isCancelled() || !event.isSneaking()) {
            return;
        }

        Player player = event.getPlayer();
        if (cooldownList.contains(player.getUniqueId()) || !GardeningTweaks.getInstance().callEvent(new PlayerGrowthDanceEvent(player))) {
            return;
        }

        cooldownList.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> cooldownList.remove(player.getUniqueId()), cooldownLength);
        growCrops(player.getLocation(), 0.5, 2, blockTypes);
    }

    public static List<Block> growCrops(Location location, double chance, int radius) {
        return growCrops(location, chance, radius, 2, null);
    }

    public static List<Block> growCrops(Location location, double chance, int radius, @Nullable Collection<Material> crops) {
        return growCrops(location, chance, radius, 2, crops);
    }

    public static List<Block> growCrops(Location location, double chance, int radius, int height, @Nullable Collection<Material> crops) {
        List<Block> grownBlocks = new ArrayList<>();
        Location currLocation = location.clone();

        for (int indexY = 0; indexY < height; indexY++) {
            for (int indexX = -radius; indexX <= radius; indexX++) {
                for (int indexZ = -radius; indexZ <= radius; indexZ++) {
                    Block block = currLocation.clone().add(indexX, indexY, indexZ).getBlock();
                    if (crops != null && !crops.contains(block.getType())) {
                        continue;
                    }

                    if (GardeningTweaks.getRandom().nextDouble(0, 1) > chance) {
                        continue;
                    }

                    Block grownBlock = PlantAging.agePlantData(block);
                    if (grownBlock != null) {
                        grownBlocks.add(grownBlock);
                    }
                }
            }
        }

        return grownBlocks;
    }
}
