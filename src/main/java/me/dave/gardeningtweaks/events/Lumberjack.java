package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningMode;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Lumberjack implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        Location location = block.getLocation();

        // Lumberjack
        ConfigManager.Lumberjack lumberjack = GardeningTweaks.configManager.getLumberjackConfig();
        if (lumberjack.mode() != GardeningMode.DISABLED && lumberjack.blocks().contains(blockType)) {
            Block blockAbove = block.getRelative(BlockFace.UP);
            if (blockAbove.getType() == blockType) breakVeinBlock(block);
        }
    }

    private void breakVeinBlock(Block block) {
        Material currType = block.getType();
        Location location = block.getLocation();
        World world = block.getWorld();
        block.breakNaturally();
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
                            breakVeinBlock(blockToBreak);
                        }
                    }
                }
            }
        }, 5);
    }

//    private static class LogLumber {
//        private final GardeningTweaks plugin;
//        private final Deque<Block> blockStack = new ArrayDeque<>();
//        private final List<Block> brokenBlocks = new ArrayList<>();

//        public LogLumber(GardeningTweaks plugin, Block startBlock) {
//            this.plugin = plugin;
//            blockStack.push(startBlock);
//            this.blockType = startBlock.getType();
//            this.blockData = blockType.createBlockData();
//            this.world = startBlock.getWorld();
//
//            breakConnectedLogs();
//        }
//
//        private void breakConnectedLogs() {
//
}
