package org.lushplugins.gardeningtweaks.module;

import org.jetbrains.annotations.Nullable;
import org.lushplugins.gardeningtweaks.api.events.BlockLumberEvent;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.util.ConfigUtils;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushlib.module.Module;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Lumberjack extends Module implements EventListener {
    public static final String ID = "LUMBERJACK";

    private Collection<Material> blocks;
    private String condition;
    private int breakLimit;

    public Lumberjack() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/lumberjack.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/lumberjack.yml"));

        this.blocks = ConfigUtils.getRegistryValues(config, "blocks", Registry.MATERIAL);
        this.condition = config.getString("condition", "crouching").toLowerCase();
        this.breakLimit = config.getInt("break-limit", 32);
    }

    @Override
    public void onDisable() {
        blocks = null;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())  {
            return;
        }

        Player player = event.getPlayer();
        if (condition.equals("standing") && player.isSneaking()) {
            return;
        } else if (condition.equals("crouching") && !player.isSneaking()) {
            return;
        }

        Block block = event.getBlock();
        Material blockType = block.getType();
        if (!blocks.contains(blockType)) {
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!Tag.ITEMS_AXES.isTagged(mainHand.getType())) {
            return;
        }

        Block blockAbove = block.getRelative(BlockFace.UP);
        if (blockAbove.getType() == blockType) {
            Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> new LumberTask(blockAbove, breakLimit, player), 5);
        }
    }

    public static class LumberTask {
        private final int breakLimit;
        private final Player player;
        private int blocksBroken = 0;

        public LumberTask(Block startBlock, int breakLimit, @Nullable Player player) {
            this.breakLimit = breakLimit;
            this.player = player;

            breakConnectedBlocks(startBlock);
        }

        private void breakConnectedBlocks(Block block) {
            Material breakType = block.getType();
            Location location = block.getLocation();
            World world = block.getWorld();

            if (blocksBroken >= breakLimit) {
                return;
            }

            if (!GardeningTweaks.getInstance().callEvent(new BlockLumberEvent(block, player))) {
                return;
            }

            if (player != null && !GardeningTweaks.getInstance().callEvent(new BlockBreakEvent(block, player))) {
                return;
            }

            // Handle block break
            blocksBroken += 1;
            block.breakNaturally();

            // Handle sounds/particles
            BlockData blockData = breakType.createBlockData();
            world.playSound(location.clone().add(0.5, 0.5, 0.5), blockData.getSoundGroup().getBreakSound(), 1f, 1f);
            world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, blockData);

            // Extra check ran prior to finding next block to slightly improve performance
            if (blocksBroken >= breakLimit) {
                return;
            }

            // Find the next block to break and schedule
            Block nextBlock = findNextBlock(block, breakType);
            if (nextBlock != null) {
                Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> {
                    breakConnectedBlocks(nextBlock);
                }, 5);
            }
        }

        public static Block findNextBlock(Block block, Material type) {
            return findNextBlock(block, Collections.singletonList(type));
        }

        public static Block findNextBlock(Block block, List<Material> types) {
            Location startLocation = block.getLocation();
            for (int indexY = 1; indexY >= 0; indexY--) {
                for (int indexX = -1; indexX <= 1; indexX++) {
                    for (int indexZ = -1; indexZ <= 1; indexZ++) {
                        if (indexX == 1 && indexY == 1 && indexZ == 1) {
                            continue;
                        }

                        Location currLoc = startLocation.clone().add(indexX, indexY, indexZ);
                        Block nextBlock = currLoc.getBlock();
                        if (nextBlock.getType() == type) {
                            return nextBlock;
                        }
                    }
                }
            }

            return null;
        }
    }
}
