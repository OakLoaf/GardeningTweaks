package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Deque;

public class FastLeafDecay implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private ConfigManager.FastLeafDecay fastLeafDecay;
    private final Deque<Block> blockSchedule = new ArrayDeque<>();
    private boolean scheduleRunning;

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        fastLeafDecay = GardeningTweaks.configManager.getFastLeafDecayConfig();
        if (!fastLeafDecay.enabled()) return;
        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Leaves leaves)) return;

        if (leaves.isPersistent()) return;
        if (leaves.getDistance() >= 7) {
            if (!blockSchedule.contains(block)) blockSchedule.add(block);
            if (!scheduleRunning) {
                scheduleRunning = true;
                breakLeaves();
            }
        }
    }

    private void breakLeaves() {
        scheduleRunning = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blockSchedule.isEmpty()) {
                    scheduleRunning = false;
                    cancel();
                    return;
                }
                Block block = blockSchedule.pop();
                if (!Tag.LEAVES.isTagged(block.getType())) return;

                BlockData blockData = block.getType().createBlockData();
                Location location = block.getLocation();
                World world = block.getWorld();
                block.breakNaturally();
                if (fastLeafDecay.sounds()) world.playSound(location.clone().add(0.5, 0.5, 0.5), blockData.getSoundGroup().getBreakSound(), 1f, 1f);
                if (fastLeafDecay.particles()) world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, blockData);
            }
        }.runTaskTimer(plugin, 0,3);
    }
}
