package me.dave.gardeningtweaks.util;

import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkCoordinate {
    private final World world;
    private final int x;
    private final int z;
    
    public ChunkCoordinate(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
    
    public Chunk getChunk() {
        return world.getChunkAt(x, z);
    }

    public static ChunkCoordinate from(Chunk chunk) {
        return new ChunkCoordinate(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }
}
