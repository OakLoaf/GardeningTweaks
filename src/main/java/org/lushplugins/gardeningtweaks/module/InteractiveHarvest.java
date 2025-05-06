package org.lushplugins.gardeningtweaks.module;

import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.hooks.HookId;
import org.lushplugins.gardeningtweaks.hooks.claims.HuskClaimsHook;
import org.lushplugins.gardeningtweaks.util.ConfigUtils;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.module.Module;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.YamlConfiguration;
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

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class InteractiveHarvest extends Module implements Listener {
    public static final String ID = "INTERACTIVE_HARVEST";

    private final HashSet<UUID> harvestCooldownSet = new HashSet<>();
    private Collection<Material> blocks;

    public InteractiveHarvest() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/interactive-harvest.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/interactive-harvest.yml"));

        blocks = ConfigUtils.getRegistryValues(config, "blocks", Registry.MATERIAL);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND ||
                event.useInteractedBlock() == Event.Result.DENY ||
                event.useItemInHand() == Event.Result.DENY) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block != null && block.getBlockData() instanceof Ageable crop && blocks.contains(block.getType()) && crop.getAge() == crop.getMaximumAge()) {
            BlockState blockState = block.getState();
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();
            if (!harvestCooldownSet.contains(playerUUID)) {
                harvestCooldownSet.add(playerUUID);
                Bukkit.getScheduler().runTaskLater(GardeningTweaks.getInstance(), () -> harvestCooldownSet.remove(playerUUID), 2);
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();
                if (mainHand.getType() == Material.BONE_MEAL || offHand.getType() == Material.BONE_MEAL) {
                    event.setCancelled(true);
                }

                Optional<Hook> huskClaims = GardeningTweaks.getInstance().getHook(HookId.HUSK_CLAIMS.toString());
                if (huskClaims.isPresent()) {
                    if (((HuskClaimsHook) huskClaims.get()).isInteractiveHarvestCancelled(player, block.getLocation())) {
                        return;
                    }
                } else {
                    if (!GardeningTweaks.getInstance().callEvent(new BlockBreakEvent(block, player))) {
                        return;
                    }
                    if (!GardeningTweaks.getInstance().callEvent(new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(Material.AIR), player, true, EquipmentSlot.HAND))) {
                        return;
                    }
                }

                Material material = block.getType();
                Collection<ItemStack> items = block.getDrops(mainHand);
                block.setType(material);
                Location location = block.getLocation();
                World world = block.getWorld();
                List<Item> drops = items.stream()
                    .map(item -> {
                        if (item.getType().toString().contains("SEEDS")) {
                            item.setAmount(item.getAmount() - 1);
                        }

                        Item droppedItem = world.createEntity(location.clone().add(0.5, 0.5, 0.5), Item.class);
                        droppedItem.setItemStack(item);
                        return droppedItem;
                    })
                    .collect(Collectors.toList());

                if (!drops.isEmpty()) {
                    BlockDropItemEvent dropItemEvent = new BlockDropItemEvent(block, blockState, player, drops);
                    if (GardeningTweaks.getInstance().callEvent(dropItemEvent)) {
                        drops.forEach(world::addEntity);
                    } else {
                        drops.forEach(Item::remove);
                    }
                }

                GardeningTweaks.getInstance().getPacketHook().ifPresent(packetHook -> packetHook.armInteractAnimation(player));

                world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3, crop);
                world.playSound(location, crop.getSoundGroup().getBreakSound(), 1f, 1f);
            }

        }
    }
}
