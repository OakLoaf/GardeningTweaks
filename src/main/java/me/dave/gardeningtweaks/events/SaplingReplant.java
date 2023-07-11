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
        if (!GardeningTweaks.getConfigManager().getSaplingReplantConfig().enabled()) return;
        if (!GardeningTweaks.getConfigManager().getSaplingReplantConfig().includePlayerDrops()) return;

        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        Item itemEntity = event.getItemDrop();
        if (!Tag.SAPLINGS.isTagged(itemEntity.getItemStack().getType())) return;

        startPlantTimer(itemEntity, null, player);
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (!Tag.LEAVES.isTagged(event.getBlockState().getType())) return;
        if (!GardeningTweaks.getConfigManager().getSaplingReplantConfig().enabled()) return;
        if (!GardeningTweaks.getConfigManager().getSaplingReplantConfig().includeLeafDrops()) return;

        event.getItems().forEach(itemEntity -> {
            if (!Tag.SAPLINGS.isTagged(itemEntity.getItemStack().getType())) return;

            Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> {
                if (itemEntity.isValid() && !itemEntity.isDead()) {
                    startPlantTimer(itemEntity, event.getBlock(), null);
                }
            }, GardeningTweaks.getConfigManager().getSaplingReplantConfig().leafDelay());
        });
    }

    private void startPlantTimer(Item itemEntity, Block originBlock, Player player) {
        Material material = itemEntity.getItemStack().getType();

        final int[] attempt = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!itemEntity.isValid() || itemEntity.isDead() || attempt[0]++ > maximumAttempts) {
                    cancel();
                    return;
                }

                if (originBlock != null && !inRangeOfOrigin(originBlock.getLocation().clone().add(0.5, 0.5, 0.5), itemEntity.getLocation())) {
                    cancel();
                    return;
                }

                Block block = itemEntity.getLocation().getBlock();
                if (block.getType() != Material.AIR) return;
                if (Tag.SAPLINGS.isTagged(block.getType())) {
                    cancel();
                    return;
                }

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
                    if (itemStack.getAmount() > 1) {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        itemEntity.setItemStack(itemStack);
                    } else {
                        itemEntity.remove();
                    }

                    block.setType(material);
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GRASS_PLACE, 1f, 0.8f);
                    block.getWorld().spawnParticle(Particle.COMPOSTER, block.getLocation().add(0.5, 0.5, 0.5), 20, 0.4, 0, 0.4);

                    cancel();
                }
            }
        }.runTaskTimer(GardeningTweaks.getInstance(), 0, 20);
    }

    private boolean inRangeOfOrigin(Location origin, Location location) {
        return (location.getX() < (origin.getX() + 1.5)) // Upper X Bound
            && (location.getX() > (origin.getX() - 1.5)) // Lower X Bound
            && (location.getY() < (origin.getY() + 1.5)) // Upper Y Bound
            && (location.getZ() < (origin.getZ() + 1.5)) // Upper Z Bound
            && (location.getZ() > (origin.getZ() - 1.5)); // Lower Z Bound
    }
}
