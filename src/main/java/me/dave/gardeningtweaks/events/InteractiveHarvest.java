package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.data.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InteractiveHarvest implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final HashSet<UUID> harvestCooldownSet = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;
        ConfigManager.InteractiveHarvest interactiveHarvest = GardeningTweaks.getConfigManager().getInteractiveHarvestConfig();
        if (!interactiveHarvest.enabled()) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block != null && block.getBlockData() instanceof Ageable crop && interactiveHarvest.blocks().contains(block.getType()) && crop.getAge() == crop.getMaximumAge()) {
            BlockState blockState = block.getState();
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();
            if (!harvestCooldownSet.contains(playerUUID)) {
                harvestCooldownSet.add(playerUUID);
                Bukkit.getScheduler().runTaskLater(plugin, () -> harvestCooldownSet.remove(playerUUID), 2);
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();
                if (mainHand.getType() == Material.BONE_MEAL || offHand.getType() == Material.BONE_MEAL) event.setCancelled(true);

                if (!GardeningTweaks.callEvent(new BlockBreakEvent(block, player))) return;
                if (!GardeningTweaks.callEvent(new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(Material.AIR), player, true, EquipmentSlot.HAND))) return;

                Material material = block.getType();
                Collection<ItemStack> drops = block.getDrops(mainHand);
                block.setType(material);
                Location location = block.getLocation();
                World world = block.getWorld();
                List<Item> entities = new ArrayList<>();
                for (ItemStack drop : drops) {
                    if (drop.getType().toString().contains("SEEDS")) drop.setAmount(drop.getAmount() - 1);
                    entities.add(world.dropItemNaturally(location.clone().add(0.5, 0.5, 0.5), drop));
                }

                if (!entities.isEmpty()) {
                    if (!GardeningTweaks.callEvent(new BlockDropItemEvent(block, blockState, player, entities))) {
                        entities.forEach(Entity::remove);
                    }
                }


                if (GardeningTweaks.protocolLibHook != null) GardeningTweaks.protocolLibHook.armInteractAnimation(player);
                world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, crop);
                world.playSound(location, crop.getSoundGroup().getBreakSound(), 1f, 1f);
            }

        }
    }
}
