package me.dave.gardeningtweaks.module.custom;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.module.Module;
import me.dave.gardeningtweaks.utils.RandomCollection;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class GrassDrops extends Module implements Listener {
    private RandomCollection<Material> items;

    public GrassDrops(String id) {
        super(id);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        File configFile = new File(plugin.getDataFolder(), "modules/grass-drops.yml");
        if (!configFile.exists()) {
            plugin.saveResource("modules/grass-drops.yml", false);
            plugin.getLogger().info("File Created: grass-drops.yml");
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

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        BlockState blockState = event.getBlockState();
        List<ItemStack> drops = event.getItems().stream().map(Item::getItemStack).toList();
        if (blockState.getType() != Material.GRASS || drops.size() == 0) {
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
