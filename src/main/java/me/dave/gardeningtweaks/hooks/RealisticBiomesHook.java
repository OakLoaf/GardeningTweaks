package me.dave.gardeningtweaks.hooks;

import me.maroon28.realisticbiomes.api.BlockAddEvent;
import me.maroon28.realisticbiomes.api.BlockRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;

public class RealisticBiomesHook implements Hook {
    public static String ID = "realistic-biomes";

    @Override
    public String getId() {
        return ID;
    }

    public void setBlockType(Block block, Material material) {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.callEvent(new BlockRemoveEvent(block));
        block.setType(material);
        pluginManager.callEvent(new BlockAddEvent(block));
    }
}