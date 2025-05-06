package org.lushplugins.gardeningtweaks.module;

import org.bukkit.event.Listener;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.lushplugins.lushlib.module.Module;
import org.lushplugins.lushlib.registry.RegistryUtils;
import org.lushplugins.lushlib.utils.RandomCollection;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class GrassDrops extends Module implements Listener {
    public static final String ID = "GRASS_DROPS";
    private static final Material GRASS_MATERIAL;

    static {
        Material grassMaterial;
        try {
            grassMaterial = Material.SHORT_GRASS;
        } catch (NoSuchFieldError ignored) {
            grassMaterial = RegistryUtils.parseString("grass", Registry.MATERIAL);
        }
        GRASS_MATERIAL = grassMaterial;
    }

    private RandomCollection<Material> items;

    public GrassDrops() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/grass-drops.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/grass-drops.yml"));

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

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        BlockState blockState = event.getBlockState();
        List<ItemStack> drops = event.getItems().stream().map(Item::getItemStack).toList();
        if (blockState.getType() != GRASS_MATERIAL || drops.isEmpty()) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE || mainHand.getType() == Material.SHEARS) {
            return;
        }

        event.setCancelled(true);
        World world = blockState.getWorld();
        Location location = blockState.getLocation();

        for (int i = 0; i < drops.iterator().next().getAmount(); i++) {
            world.dropItemNaturally(location, new ItemStack(items.next()));
        }
    }
}
