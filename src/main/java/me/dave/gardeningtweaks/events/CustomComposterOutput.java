package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.datamanager.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class CustomComposterOutput implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        ConfigManager.CustomComposterOutput customComposterOutput = GardeningTweaks.getConfigManager().getCustomComposterOutput();
        if (!customComposterOutput.enabled() || customComposterOutput.items().size() == 0) return;
        Block block = event.getBlock();
        if (block.getType() != Material.COMPOSTER) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setDropItems(false);
        Collection<ItemStack> drops = block.getDrops();
        Location location = block.getLocation();
        World world = block.getWorld();
        for (ItemStack item : drops) {
            if (item.getType() == Material.BONE_MEAL) {
                List<Material> newDrops = customComposterOutput.items();
                item.setType(newDrops.get(random.nextInt(newDrops.size())));
            }
            world.dropItemNaturally(location, item);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.isCancelled()) return;
        ConfigManager.CustomComposterOutput customComposterOutput = GardeningTweaks.getConfigManager().getCustomComposterOutput();
        if (!customComposterOutput.enabled() || customComposterOutput.items().size() == 0) return;
        Inventory source = event.getSource();
        if (source.getType() != InventoryType.COMPOSTER) return;
        ItemStack item = event.getItem();
        if (item.getType() == Material.BONE_MEAL) {
            List<Material> newDrops = customComposterOutput.items();
            item.setType(newDrops.get(random.nextInt(newDrops.size())));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.COMPOSTER) return;
        if (!(block.getBlockData() instanceof Levelled composterData)) return;
        if (composterData.getLevel() != composterData.getMaximumLevel()) return;

        event.setCancelled(true);
        composterData.setLevel(0);
        block.setBlockData(composterData);

        ConfigManager.CustomComposterOutput customComposterOutput = GardeningTweaks.getConfigManager().getCustomComposterOutput();
        List<Material> newDrops = customComposterOutput.items();
        Location location = block.getLocation().clone().add(0, 0.5, 0);
        World world = block.getWorld();
        world.dropItemNaturally(location, new ItemStack(newDrops.get(random.nextInt(newDrops.size()))));
        world.playSound(location, Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1f, 1.5f);
        world.playSound(location, Sound.ENTITY_ITEM_PICKUP, 0.5f, 1f);
    }
}
