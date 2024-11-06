package org.lushplugins.gardeningtweaks.module;

import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushlib.module.Module;
import org.lushplugins.lushlib.registry.RegistryUtils;
import org.lushplugins.lushlib.utils.RandomCollection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class ComposterOutput extends Module implements EventListener {
    public static final String ID = "COMPOSTER_OUTPUT";

    private RandomCollection<Material> items;

    public ComposterOutput() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/composter-output.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/composter-output.yml"));

        items = new RandomCollection<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            itemsSection.getValues(false).forEach((fromRaw, toRaw) -> {
                Material from = RegistryUtils.parseString(fromRaw, Registry.MATERIAL);
                if (from != null) {
                    items.add(from, Double.parseDouble(String.valueOf(toRaw)));
                } else {
                    GardeningTweaks.getInstance().getLogger().warning("'" + fromRaw + "' is not a valid material");
                }
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

        for (Item item : event.getItems()) {
            ItemStack itemStack = item.getItemStack();
            if (itemStack.getType() == Material.BONE_MEAL) {
                itemStack.setType(items.next());
            }
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
