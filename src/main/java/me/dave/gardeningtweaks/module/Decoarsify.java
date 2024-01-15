package me.dave.gardeningtweaks.module;

import me.dave.platyutils.listener.EventListener;
import me.dave.platyutils.module.Module;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Decoarsify extends Module implements EventListener {
    public static final String ID = "DECOARSIFY";

    public Decoarsify() {
        super(ID);
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        BlockState blockState = event.getBlockState();
        Player player = event.getPlayer();
        if (blockState.getType() != Material.COARSE_DIRT || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        ItemMeta mainHandMeta = player.getInventory().getItemInMainHand().getItemMeta();
        if (mainHandMeta != null && mainHandMeta.hasEnchant(Enchantment.SILK_TOUCH)) {
            return;
        }

        event.getItems().forEach(item -> {
            if (item.getItemStack().getType().equals(Material.COARSE_DIRT)) {
                item.setItemStack(new ItemStack(Material.DIRT));
            }
        });
    }
}
