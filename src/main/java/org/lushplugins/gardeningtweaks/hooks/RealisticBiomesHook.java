package org.lushplugins.gardeningtweaks.hooks;

import org.lushplugins.lushlib.hook.Hook;
import me.maroon28.realisticbiomes.api.BlockAddEvent;
import me.maroon28.realisticbiomes.api.BlockRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;

public class RealisticBiomesHook extends Hook {

    public RealisticBiomesHook() {
        super(HookId.REALISTIC_BIOMES.toString());
    }

    public void setBlockType(Block block, Material material) {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.callEvent(new BlockRemoveEvent(block));
        block.setType(material);
        pluginManager.callEvent(new BlockAddEvent(block));
    }
}