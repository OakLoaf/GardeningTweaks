package me.dave.gardeningtweaks.dependencies;

import me.maroon28.realisticbiomes.api.BlockAddEvent;
import me.maroon28.realisticbiomes.api.BlockRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;

public class RealisticBiomesHook {
    private final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

    public void setBlockType(Block block, Material material) {
        pluginManager.callEvent(new BlockRemoveEvent(block));
        block.setType(material);
        pluginManager.callEvent(new BlockAddEvent(block));
    }
}