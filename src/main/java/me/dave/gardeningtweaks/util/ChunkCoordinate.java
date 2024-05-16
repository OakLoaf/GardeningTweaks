package me.dave.gardeningtweaks.util;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;

public record ChunkCoordinate(World world, int x, int z) {

    public Chunk getChunk() {
        return world.getChunkAt(x, z);
    }

    public static ChunkCoordinate from(Chunk chunk) {
        return new ChunkCoordinate(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkCoordinate that = (ChunkCoordinate) o;
        return x == that.x && z == that.z && Objects.equals(world, that.world);
    }

}
