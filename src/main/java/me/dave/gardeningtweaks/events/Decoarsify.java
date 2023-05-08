package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.datamanager.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Decoarsify implements Listener {

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (event.isCancelled()) return;
        ConfigManager.Decoarsify decoarsify = GardeningTweaks.getConfigManager().getDecoarsifyConfig();
        if (!decoarsify.enabled()) return;
        BlockState blockState = event.getBlockState();
        Player player = event.getPlayer();
        if (blockState.getType() != Material.COARSE_DIRT || player.getGameMode() == GameMode.CREATIVE) return;
        ItemMeta mainHandMeta = player.getInventory().getItemInMainHand().getItemMeta();
        if (mainHandMeta == null || mainHandMeta.hasEnchant(Enchantment.SILK_TOUCH)) return;
        event.setCancelled(true);
        blockState.getWorld().dropItemNaturally(blockState.getLocation(), new ItemStack(Material.DIRT));
    }
}
