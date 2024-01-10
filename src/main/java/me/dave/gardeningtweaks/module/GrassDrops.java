package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.platyutils.module.Module;
import me.dave.platyutils.utils.RandomCollection;
import me.dave.platyutils.utils.StringUtils;
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
    public static final String ID = "GRASS_DROPS";

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
                StringUtils.getEnum(String.valueOf(fromRaw), Material.class).ifPresentOrElse(
                    from ->  items.add(from, Double.parseDouble(String.valueOf(toRaw))),
                    () -> GardeningTweaks.getInstance().getLogger().warning("'" + fromRaw + "' is not a valid material")
                );
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
