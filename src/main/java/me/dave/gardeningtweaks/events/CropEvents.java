package me.dave.gardeningtweaks.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class CropEvents implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final ProtocolManager protocolManager;
    private final HashSet<UUID> harvestCooldownSet = new HashSet<>();

    public CropEvents(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        // Interactive Harvest
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (GardeningTweaks.configManager.hasInteractiveHarvest()) {
                Block block = event.getClickedBlock();
                if (block == null || !(block.getBlockData() instanceof Ageable crop) || crop.getAge() != crop.getMaximumAge())
                    return;
                Player player = event.getPlayer();
                UUID playerUUID = player.getUniqueId();
                if (harvestCooldownSet.contains(playerUUID)) return;
                harvestCooldownSet.add(playerUUID);
                Bukkit.getScheduler().runTaskLater(plugin, () -> harvestCooldownSet.remove(playerUUID), 2);
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();
                if (mainHand.getType() == Material.BONE_MEAL || offHand.getType() == Material.BONE_MEAL)
                    event.setCancelled(true);
                Material material = block.getType();
                Collection<ItemStack> drops = block.getDrops(mainHand);
                block.setType(material);
                Location location = block.getLocation();
                World world = location.getWorld();
                if (world != null) {
                    for (ItemStack drop : drops) {
                        if (drop.getType().toString().contains("SEEDS")) drop.setAmount(drop.getAmount() - 1);
                        world.dropItemNaturally(location.clone().add(0.5, 0.5, 0.5), drop);
                    }
                    if (protocolManager != null) {
                        PacketContainer armAnimation = new PacketContainer(PacketType.Play.Server.ANIMATION);
                        armAnimation.getIntegers()
                            .write(0, player.getEntityId())
                            .write(1, 0);

                        try {
                            protocolManager.sendServerPacket(player, armAnimation);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, crop);
                    world.playSound(location, Sound.BLOCK_CROP_BREAK, 1f, 1f);
                }
            }
        }

        // Dynamic Trample
        else if (action == Action.PHYSICAL) {
            boolean dynamicTrampleFeatherFall = GardeningTweaks.configManager.hasDynamicTrampleFeatherFall();
            boolean dynamicTrampleCreative = GardeningTweaks.configManager.hasDynamicTrampleCreative();
            if (dynamicTrampleFeatherFall || dynamicTrampleCreative) {
                Block block = event.getClickedBlock();
                if (block == null) return;
                Material material = block.getType();
                if (material != Material.FARMLAND) return;
                Player player = event.getPlayer();

                if (dynamicTrampleFeatherFall) {
                    ItemStack boots = player.getInventory().getBoots();
                    if (boots != null) {
                        ItemMeta bootsMeta = boots.getItemMeta();
                        if (bootsMeta != null && bootsMeta.hasEnchant(Enchantment.PROTECTION_FALL)) event.setCancelled(true);
                    }
                }
                if (dynamicTrampleCreative && player.getGameMode() == GameMode.CREATIVE) {
                    event.setCancelled(true);
                }
            }
        }
    }
}