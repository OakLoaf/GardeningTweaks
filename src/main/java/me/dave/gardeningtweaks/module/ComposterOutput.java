package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.platyutils.module.Module;
import me.dave.platyutils.utils.RandomCollection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class ComposterOutput extends Module implements Listener {
    public static String ID = "COMPOSTER_OUTPUT";

    private RandomCollection<Material> items;

    public ComposterOutput() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        File configFile = new File(plugin.getDataFolder(), "modules/composter-output.yml");
        if (!configFile.exists()) {
            plugin.saveResource("modules/composter-output.yml", false);
            plugin.getLogger().info("File Created: composter-output.yml");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        items = new RandomCollection<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            itemsSection.getValues(false).forEach((fromRaw, toRaw) -> {
                Material from;
                try {
                    from = Material.valueOf(String.valueOf(fromRaw));
                } catch (IllegalArgumentException e) {
                    GardeningTweaks.getInstance().getLogger().warning("'" + fromRaw + "' is not a valid material");
                    return;
                }

                items.add(from, Double.parseDouble(String.valueOf(toRaw)));
            });
        }
    }

    @Override
    public void onDisable() {
        if (items != null) {
            items.clear();
            items = null;
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (event.isCancelled() || items.isEmpty()) {
            return;
        }

        BlockState blockState = event.getBlockState();
        if (blockState.getType() != Material.COMPOSTER || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
        List<ItemStack> drops = event.getItems().stream().map(Item::getItemStack).toList();
        Location location = blockState.getLocation();
        World world = blockState.getWorld();
        for (ItemStack item : drops) {
            if (item.getType() == Material.BONE_MEAL) {
                item.setType(items.next());
            }

            world.dropItemNaturally(location, item);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.isCancelled() || items.isEmpty()) {
            return;
        }

        Inventory source = event.getSource();
        if (source.getType() != InventoryType.COMPOSTER) {
            return;
        }

        ItemStack item = event.getItem();
        if (item.getType() == Material.BONE_MEAL) {
            item.setType(items.next());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null ||
                block.getType() != Material.COMPOSTER ||
                !(block.getBlockData() instanceof Levelled composterData) ||
                composterData.getLevel() != composterData.getMaximumLevel()) {
            return;
        }

        event.setCancelled(true);
        composterData.setLevel(0);
        block.setBlockData(composterData);

        Location location = block.getLocation().clone().add(0, 0.5, 0);
        World world = block.getWorld();
        world.dropItemNaturally(location, new ItemStack(items.next()));
        world.playSound(location, Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1f, 1.5f);
        world.playSound(location, Sound.ENTITY_ITEM_PICKUP, 0.5f, 1f);
    }
}
