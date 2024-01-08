package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.platyutils.module.Module;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.configuration.file.YamlConfiguration;
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

public class FastLeafDecay extends Module implements Listener {
    public static String ID = "FAST_LEAF_DECAY";

    private final NamespacedKey ignoredKey = new NamespacedKey(GardeningTweaks.getInstance(), "FLD");
    private final FixedMetadataValue ignoredBlockMetaData = new FixedMetadataValue(GardeningTweaks.getInstance(), "ignored");
    private HashMap<Integer, Deque<Block>> blockScheduleMap;
    private Boolean sounds;
    private Boolean particles;
    private Boolean ignorePersistence;

    public FastLeafDecay() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        File configFile = new File(plugin.getDataFolder(), "modules/fast-leaf-decay.yml");
        if (!configFile.exists()) {
            plugin.saveResource("modules/fast-leaf-decay.yml", false);
            plugin.getLogger().info("File Created: fast-leaf-decay.yml");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        blockScheduleMap = new HashMap<>();

        sounds = config.getBoolean("sounds", false);
        particles = config.getBoolean("particles", false);
        ignorePersistence = config.getBoolean("ignore-persistence", false);
    }

    @Override
    public void onDisable() {
        if (blockScheduleMap != null) {
            blockScheduleMap.values().forEach(Collection::clear);
            blockScheduleMap.clear();
            blockScheduleMap = null;
        }

        sounds = null;
        particles = null;
        ignorePersistence = null;
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Leaves leaves)) {
            return;
        }

        if (!ignorePersistence && leaves.isPersistent()) {
            return;
        }
        if (leaves.getDistance() >= 7) {
            int currTick = GardeningTweaks.getCurrentTick();
            if (blockScheduleMap.containsKey(currTick)) {
                Deque<Block> blockSchedule = blockScheduleMap.get(currTick);
                if (!blockSchedule.contains(block)) {
                    blockSchedule.add(block);
                }
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
        if (!ignorePersistence) return;
        Block block = event.getBlock();
        if (!Tag.LEAVES.isTagged(block.getType())) return;
        updateLeaf(block.getLocation(), true);
        block.setMetadata("GT_FLD", ignoredBlockMetaData);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!ignorePersistence) {
            return;
        }

        Block block = event.getBlock();
        if (!Tag.LEAVES.isTagged(block.getType())) {
            return;
        }
        updateLeaf(block.getLocation(), false);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!ignorePersistence) {
            return;
        }

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
        if (placed) {
            chunkLocations.add(chunkLocation);
        } else {
            chunkLocations.remove(chunkLocation);
        }

        dataContainer.set(ignoredKey, PersistentDataType.BYTE_ARRAY, serialize(chunkLocations));
    }

    private void breakLeaves(int tick) {
        Deque<Block> blockSchedule = blockScheduleMap.get(tick);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blockScheduleMap == null || blockScheduleMap.isEmpty()) {
                    cancel();
                    return;
                }

                if (blockSchedule.isEmpty()) {
                    blockScheduleMap.remove(tick);
                    cancel();
                    return;
                }

                Block block = blockSchedule.pop();
                if (!Tag.LEAVES.isTagged(block.getType()) || !(block.getBlockData() instanceof Leaves leaves) || leaves.getDistance() < 7) {
                    return;
                }

                List<MetadataValue> metaList = block.getMetadata("GT_FLD");
                if (metaList.size() > 0) {
                    return;
                }

                BlockData blockData = block.getType().createBlockData();
                Location location = block.getLocation();
                World world = block.getWorld();
                block.breakNaturally();
                if (sounds) {
                    world.playSound(location.clone().add(0.5, 0.5, 0.5), blockData.getSoundGroup().getBreakSound(), 1f, 1f);
                }
                if (particles) {
                    world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, blockData);
                }
            }
        }.runTaskTimer(GardeningTweaks.getInstance(), 3,3);
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

            @SuppressWarnings("unchecked")
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
