package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.api.events.BlockLumberEvent;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.platyutils.module.Module;
import me.dave.platyutils.utils.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Lumberjack extends Module implements Listener {
    public static final String ID = "LUMBERJACK";

    private final EnumSet<Material> axes = EnumSet.of(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);
    private List<Material> blocks;

    public Lumberjack() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/lumberjack.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/lumberjack.yml"));

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
        blocks = null;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (blocks.contains(blockType)) {
            Player player = event.getPlayer();
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (!axes.contains(mainHand.getType()) || player.isSneaking()) return;
            Block blockAbove = block.getRelative(BlockFace.UP);
            if (blockAbove.getType() == blockType) Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> new LogLumber(blockAbove, player), 5);
        }
    }

    private static class LogLumber {
        private final Player player;
        private int blocksBroken = 0;

        public LogLumber(Block startBlock, Player player) {
            this.player = player;

            breakConnectedLogs(startBlock);
        }

        private void breakConnectedLogs(Block block) {
            Material currType = block.getType();
            Location location = block.getLocation();
            World world = block.getWorld();

            if (blocksBroken >= 32) {
                return;
            }

            if (!GardeningTweaks.callEvent(new BlockLumberEvent(block, player)) || !GardeningTweaks.callEvent(new BlockBreakEvent(block, player))) {
                return;
            }

            block.breakNaturally();
            blocksBroken += 1;
            BlockData blockData = currType.createBlockData();
            world.playSound(location.clone().add(0.5, 0.5, 0.5), blockData.getSoundGroup().getBreakSound(), 1f, 1f);
            world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, blockData);

            Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> {
                for (int indexY = 1; indexY >= 0; indexY--) {
                    for (int indexX = -1; indexX <= 1; indexX++) {
                        for (int indexZ = -1; indexZ <= 1; indexZ++) {
                            if (indexX == 1 && indexY == 1 && indexZ == 1) continue;
                            Location currLoc = location.clone().add(indexX, indexY, indexZ);
                            Block blockToBreak = currLoc.getBlock();
                            if (blockToBreak.getType() == currType) {
                                breakConnectedLogs(blockToBreak);
                            }
                        }
                    }
                }
            }, 5);
        }
    }
}
