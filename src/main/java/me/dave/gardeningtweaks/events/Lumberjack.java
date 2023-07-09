package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.api.events.BlockLumberEvent;
import me.dave.gardeningtweaks.data.ConfigManager;
import me.dave.gardeningtweaks.GardeningMode;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;

public class Lumberjack implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final EnumSet<Material> axes = EnumSet.of(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        Material blockType = block.getType();

        ConfigManager.Lumberjack lumberjack = GardeningTweaks.getConfigManager().getLumberjackConfig();
        GardeningMode lumberjackMode = lumberjack.mode();
        if (lumberjackMode != GardeningMode.DISABLED && lumberjack.blocks().contains(blockType)) {
            Player player = event.getPlayer();
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (!axes.contains(mainHand.getType()) || player.isSneaking()) return;
            Block blockAbove = block.getRelative(BlockFace.UP);
            if (blockAbove.getType() == blockType) Bukkit.getScheduler().runTaskLater(plugin, () -> new LogLumber(plugin, blockAbove, player), 5);
        }
    }

    private static class LogLumber {
        private final GardeningTweaks plugin;
        private final Player player;
        private int blocksBroken = 0;

        public LogLumber(GardeningTweaks plugin, Block startBlock, Player player) {
            this.plugin = plugin;
            this.player = player;

            breakConnectedLogs(startBlock);
        }

        private void breakConnectedLogs(Block block) {
            Material currType = block.getType();
            Location location = block.getLocation();
            World world = block.getWorld();

            if (blocksBroken >= 32) return;
            if (GardeningTweaks.getConfigManager().getLumberjackConfig().ignorePlaced() && GardeningTweaks.coreProtectHook != null && !GardeningTweaks.coreProtectHook.isBlockNatural(block.getLocation())) return;
            if (!GardeningTweaks.callEvent(new BlockBreakEvent(block, player))) return;
            if (!GardeningTweaks.callEvent(new BlockLumberEvent(block, player))) return;

            block.breakNaturally();
            blocksBroken += 1;
            BlockData blockData = currType.createBlockData();
            world.playSound(location.clone().add(0.5, 0.5, 0.5), blockData.getSoundGroup().getBreakSound(), 1f, 1f);
            world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, blockData);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
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
