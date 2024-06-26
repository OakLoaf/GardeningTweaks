package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.api.events.CropGrowEvent;
import me.dave.gardeningtweaks.api.events.PlayerGrowthDanceEvent;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushlib.module.Module;
import org.lushplugins.lushlib.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
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
    private List<Material> blocks;

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
        blocks = config.getStringList("blocks").stream().map((materialRaw) -> {
            Material material = StringUtils.getEnum(materialRaw, Material.class).orElse(null);
            if (material == null) {
                plugin.getLogger().warning("Ignoring " + materialRaw + ", that is not a valid material.");
            }

            return material;
        }).filter(Objects::nonNull).toList();
    }

    @Override
    public void onDisable() {
        if (cooldownList != null) {
            cooldownList.clear();
            cooldownList = null;
        }

        blocks = null;
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
        growCrops(player.getLocation(), 0.5, 2, blocks);
    }

    public static List<Block> growCrops(Location location, double chance, int radius) {
        return growCrops(location, chance, radius, 2, null);
    }

    public static List<Block> growCrops(Location location, double chance, int radius, @Nullable List<Material> crops) {
        return growCrops(location, chance, radius, 2, crops);
    }

    public static List<Block> growCrops(Location location, double chance, int radius, int height, @Nullable List<Material> crops) {
        List<Block> grownBlocks = new ArrayList<>();
        Location currLocation = location.clone();

        for (int indexY = 0; indexY < height; indexY++) {
            for (int indexX = -radius; indexX <= radius; indexX++) {
                for (int indexZ = -radius; indexZ <= radius; indexZ++) {
                    Block currBlock = currLocation.clone().add(indexX, indexY, indexZ).getBlock();
                    if (!(currBlock.getBlockData() instanceof Ageable crop) || crops == null || !crops.contains(currBlock.getType())) {
                        continue;
                    }

                    if (crop.getAge() == crop.getMaximumAge()) {
                        continue;
                    }

                    if (GardeningTweaks.getRandom().nextDouble(0, 1) > chance) {
                        continue;
                    }

                    if (!GardeningTweaks.getInstance().callEvent(new CropGrowEvent(currBlock))) {
                        continue;
                    }

                    int increment = GardeningTweaks.getRandom().nextInt(3);
                    if (increment == 0) {
                        continue;
                    }

                    int newAge = crop.getAge() + increment;
                    int maxAge = crop.getMaximumAge();
                    if (newAge > maxAge) {
                        newAge = maxAge;
                    }

                    crop.setAge(newAge);
                    currBlock.setBlockData(crop);
                    grownBlocks.add(currBlock);
                }
            }
        }

        return grownBlocks;
    }
}
