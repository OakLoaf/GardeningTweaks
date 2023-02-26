package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.datamanager.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;

public class FastLeafDecay implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private ConfigManager.FastLeafDecay fastLeafDecay;
    private final NamespacedKey ignoredKey = new NamespacedKey(GardeningTweaks.getInstance(), "FLD");
    private final FixedMetadataValue ignoredBlockMetaData = new FixedMetadataValue(plugin, "ignored");
    private final HashMap<Integer, Deque<Block>> blockScheduleMap = new HashMap<>();

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        fastLeafDecay = GardeningTweaks.getConfigManager().getFastLeafDecayConfig();
        if (!fastLeafDecay.enabled()) return;
        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Leaves leaves)) return;

        if (!fastLeafDecay.ignorePersistence() && leaves.isPersistent()) return;
        if (leaves.getDistance() >= 7) {
            int currTick = GardeningTweaks.getCurrentTick();
            if (blockScheduleMap.containsKey(currTick)) {
                Deque<Block> blockSchedule = blockScheduleMap.get(currTick);
                if (!blockSchedule.contains(block)) blockSchedule.add(block);
            } else {
                Deque<Block> blockSchedule = new ArrayDeque<>();
                blockSchedule.add(block);
                blockScheduleMap.put(currTick, blockSchedule);
                breakLeaves(currTick);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!GardeningTweaks.getConfigManager().getFastLeafDecayConfig().ignorePersistence()) return;
        Block block = event.getBlock();
        if (!Tag.LEAVES.isTagged(block.getType())) return;
        updateLeaf(block.getLocation(), true);
        block.setMetadata("GT_FLD", ignoredBlockMetaData);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!GardeningTweaks.getConfigManager().getFastLeafDecayConfig().ignorePersistence()) return;
        Block block = event.getBlock();
        if (!Tag.LEAVES.isTagged(block.getType())) return;
        updateLeaf(block.getLocation(), false);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!GardeningTweaks.getConfigManager().getFastLeafDecayConfig().ignorePersistence()) return;
        Chunk chunk = event.getChunk();
        byte[] bytes = chunk.getPersistentDataContainer().get(ignoredKey, PersistentDataType.BYTE_ARRAY);
        HashSet<ChunkLocation> chunkLocations = deserialize(bytes);
        chunkLocations.forEach((chunkLocation) -> chunk.getBlock(chunkLocation.chunkX(), chunkLocation.chunkY(), chunkLocation.chunkZ()).setMetadata("GT_FLD", ignoredBlockMetaData));
    }

    private void updateLeaf(Location location, boolean placed) {
        Chunk chunk = location.getChunk();
        Block block = location.getBlock();
        int chunkRelativeX = block.getX() & 0xF;
        int chunkRelativeY = block.getY() & 0xFF;
        int chunkRelativeZ = block.getZ() & 0xF;
        ChunkLocation chunkLocation = new ChunkLocation(chunkRelativeX, chunkRelativeY, chunkRelativeZ);

        PersistentDataContainer dataContainer = chunk.getPersistentDataContainer();

        byte[] byteArr = dataContainer.get(ignoredKey, PersistentDataType.BYTE_ARRAY);
        HashSet<ChunkLocation> chunkLocations = deserialize(byteArr);
        if (placed) chunkLocations.add(chunkLocation);
        else chunkLocations.remove(chunkLocation);

        dataContainer.set(ignoredKey, PersistentDataType.BYTE_ARRAY, serialize(chunkLocations));
    }

    private void breakLeaves(int tick) {
        Deque<Block> blockSchedule = blockScheduleMap.get(tick);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blockSchedule.isEmpty()) {
                    blockScheduleMap.remove(tick);
                    cancel();
                    return;
                }
                Block block = blockSchedule.pop();
                if (!Tag.LEAVES.isTagged(block.getType())) return;
                if (!(block.getBlockData() instanceof Leaves leaves)) return;
                if (leaves.getDistance() < 7) return;
                List<MetadataValue> metaList = block.getMetadata("GT_FLD");
                if (metaList.size() > 0) return;

                BlockData blockData = block.getType().createBlockData();
                Location location = block.getLocation();
                World world = block.getWorld();
                block.breakNaturally();
                if (fastLeafDecay.sounds()) world.playSound(location.clone().add(0.5, 0.5, 0.5), blockData.getSoundGroup().getBreakSound(), 1f, 1f);
                if (fastLeafDecay.particles()) world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, blockData);
            }
        }.runTaskTimer(plugin, 3,3);
    }

    private byte[] serialize(HashSet<ChunkLocation> chunkLocations) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(chunkLocations);
            out.close();

            return byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private HashSet<ChunkLocation> deserialize(byte[] bytes) {
        try {
            if (bytes == null) return new HashSet<>();
            ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(byteIn);

            HashSet<ChunkLocation> chunkLocations = (HashSet<ChunkLocation>) in.readObject();

            in.close();

            return chunkLocations;
        } catch (IOException | ClassNotFoundException err) {
            err.printStackTrace();
            return new HashSet<>();
        }
    }

    private record ChunkLocation(int chunkX, int chunkY, int chunkZ) implements Serializable {}
}
