package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Decoarsify implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ConfigManager.Decoarsify decoarsify = GardeningTweaks.configManager.getDecoarsifyConfig();
        if (!decoarsify.enabled()) return;
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType() != Material.COARSE_DIRT || player.getGameMode() == GameMode.CREATIVE) return;
        ItemMeta mainHandMeta = player.getInventory().getItemInMainHand().getItemMeta();
        if (mainHandMeta == null || mainHandMeta.hasEnchant(Enchantment.SILK_TOUCH)) return;
        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIRT));
    }
}
