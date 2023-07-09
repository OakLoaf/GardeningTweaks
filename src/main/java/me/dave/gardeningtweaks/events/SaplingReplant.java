package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.api.events.SaplingReplantEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SaplingReplant implements Listener {
    private final List<Material> plantableBlocks = List.of(new Material[]{Material.DIRT, Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.MOSS_BLOCK});
    private final int maximumAttempts = 10;

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // TODO: Add config and check if enabled
        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        Item itemEntity = event.getItemDrop();
        if (!Tag.SAPLINGS.isTagged(itemEntity.getItemStack().getType())) return;

        startPlantTimer(itemEntity, player);
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (!Tag.LEAVES.isTagged(event.getBlockState().getType())) return;

        event.getItems().forEach(itemEntity -> {
            if (!Tag.SAPLINGS.isTagged(itemEntity.getItemStack().getType())) return;

            Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> {
                if (itemEntity.isValid() && !itemEntity.isDead()) {
                    startPlantTimer(itemEntity, null);
                }
            },200 /* TODO: Make cooldown configurable */);
        });
    }

    private void startPlantTimer(Item itemEntity, Player player) {
        Material material = itemEntity.getItemStack().getType();

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
                    if (player != null && !GardeningTweaks.callEvent(new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), itemEntity.getItemStack(), player, true, EquipmentSlot.HAND))) {
                        cancel();
                        return;
                    }

                    if (!GardeningTweaks.callEvent(new SaplingReplantEvent(block, player, itemEntity, material))) {
                        cancel();
                        return;
                    }

                    ItemStack itemStack = itemEntity.getItemStack();
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    itemEntity.setItemStack(itemStack);

                    block.setType(material);
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GRASS_PLACE, 1f, 0.8f);
                    block.getWorld().spawnParticle(Particle.COMPOSTER, block.getLocation().add(0.5, 0.5, 0.5), 20, 0.4, 0, 0.4);

                    cancel();
                }
            }
        }.runTaskTimer(GardeningTweaks.getInstance(), 0, 20);
    }
}
