package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.dependencies.ProtocolLibHook;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class InteractiveHarvest implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final ProtocolLibHook protocolLibHook;
    private final HashSet<UUID> harvestCooldownSet = new HashSet<>();

    public InteractiveHarvest(ProtocolLibHook protocolLibHook) {
        this.protocolLibHook = protocolLibHook;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ConfigManager.InteractiveHarvest interactiveHarvest = GardeningTweaks.configManager.getInteractiveHarvestConfig();
        if (!interactiveHarvest.enabled()) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block != null && block.getBlockData() instanceof Ageable crop && interactiveHarvest.blocks().contains(block.getType()) && crop.getAge() == crop.getMaximumAge()) {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();
            if (!harvestCooldownSet.contains(playerUUID)) {
                harvestCooldownSet.add(playerUUID);
                Bukkit.getScheduler().runTaskLater(plugin, () -> harvestCooldownSet.remove(playerUUID), 2);
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();
                if (mainHand.getType() == Material.BONE_MEAL || offHand.getType() == Material.BONE_MEAL) event.setCancelled(true);
                Material material = block.getType();
                Collection<ItemStack> drops = block.getDrops(mainHand);
                block.setType(material);
                Location location = block.getLocation();
                World world = block.getWorld();
                for (ItemStack drop : drops) {
                    if (drop.getType().toString().contains("SEEDS")) drop.setAmount(drop.getAmount() - 1);
                    world.dropItemNaturally(location.clone().add(0.5, 0.5, 0.5), drop);
                }

                protocolLibHook.armInteractAnimation(player);
                world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, crop);
                world.playSound(location, crop.getSoundGroup().getBreakSound(), 1f, 1f);
            }
        }
    }
}