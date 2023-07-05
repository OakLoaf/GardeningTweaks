package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Replant implements Listener {
    private final List<Material> plantableBlocks = List.of(new Material[]{Material.DIRT, Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.MOSS_BLOCK});
    private final int maximumAttempts = 10;

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        // TODO: Add config and check if enabled
        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        Item itemEntity = event.getItemDrop();

        Material material = itemEntity.getItemStack().getType();
        if (!Tag.SAPLINGS.isTagged(material)) return;

        final int[] attempt = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!itemEntity.isValid() || itemEntity.isDead() || attempt[0]++ > maximumAttempts) {
                    cancel();
                    return;
                }

                Block block = itemEntity.getLocation().getBlock();
                if (block.getType() != Material.AIR) return;

                if (plantableBlocks.contains(block.getRelative(BlockFace.DOWN).getType())) {
                    if (!callEvent(new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), itemEntity.getItemStack(), player, true, EquipmentSlot.HAND))) {
                        cancel();
                        return;
                    }

                    ItemStack itemStack = itemEntity.getItemStack();
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    itemEntity.setItemStack(itemStack);

                    block.setType(material);
                }
            }
        }.runTaskTimer(GardeningTweaks.getInstance(), 0, 20);
    }

    private boolean callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event instanceof Cancellable) {
            return !((Cancellable) event).isCancelled();
        } else {
            return true;
        }
    }
}
