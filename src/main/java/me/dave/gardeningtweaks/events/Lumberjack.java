package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningMode;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Lumberjack implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();

        // Lumberjack
        ConfigManager.Lumberjack lumberjack = GardeningTweaks.configManager.getLumberjackConfig();
        if (lumberjack.mode() != GardeningMode.DISABLED && lumberjack.blocks().contains(block.getType())) {
            breakVeinBlock(block);
        }
    }

    private void breakVeinBlock(Block block) {
        Material currType = block.getType();
        Location location = block.getLocation();
        World world = block.getWorld();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location checkLoc = location.clone().add(-1, -1, -1);
            for (int indexY = 1; indexY >= 0; indexY--) {
                for (int indexX = 1; indexX >= -1; indexX--) {
                    for (int indexZ = 1; indexZ >= -1; indexZ--) {
                        if (indexX == 1 && indexY == 1 && indexZ == 1) continue;
                        Block blockToBreak = checkLoc.clone().add(indexX, indexY, indexZ).getBlock();
                        if (blockToBreak.getType() == currType) breakVeinBlock(blockToBreak);
                        blockToBreak.breakNaturally();
                        world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, currType.createBlockData());
                    }
                }
            }
        }, 5);
    }

//    private static class LogLumber {
//        private final GardeningTweaks plugin;
//        private final Deque<Block> blockStack = new ArrayDeque<>();
//        private final List<Block> brokenBlocks = new ArrayList<>();
//        private final Material blockType;
//        private final BlockData blockData;
//        private final World world;
//
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
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    for (int indexY = 1; indexY >= 0; indexY--) {
//                        for (int indexX = 1; indexX >= -1; indexX--) {
//                            for (int indexZ = 1; indexZ >= -1; indexZ--) {
//                                Block nextBlock = world.getBlockAt(blockStack.peek().getLocation().add(indexX, indexY, indexZ));
//                                if (nextBlock.getType().equals(blockType)) {
//                                    if (!brokenBlocks.contains(nextBlock)) {
//                                        world.spawnParticle(Particle.BLOCK_DUST, nextBlock.getLocation().clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, blockData);
//                                        world.playSound(nextBlock.getLocation().clone().add(0.5, 0.5, 0.5), blockData.getSoundGroup().getBreakSound(), 1f, 1f);
//                                        nextBlock.breakNaturally();
//                                        brokenBlocks.add(nextBlock);
//                                        if (brokenBlocks.size() >= 31) {
//                                            cancel();
//                                            return;
//                                        }
//                                    }
//                                    blockStack.push(nextBlock);
//                                    return;
//                                }
//                            }
//                        }
//                    }
//                    blockStack.pop();
//                    if (blockStack.isEmpty()) {
//                        cancel();
//                    }
//                }
//            }.runTaskTimer(plugin, 5, 5);
//        }
//    }
}
