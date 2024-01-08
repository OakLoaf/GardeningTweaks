package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.utils.RandomCollection;
import me.dave.platyutils.module.Module;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class SnifferDrops extends Module implements Listener {
    public static String ID = "SNIFFER_DROPS";

    private RandomCollection<Material> drops;

    public SnifferDrops() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        File configFile = new File(plugin.getDataFolder(), "modules/sniffer-drops.yml");
        if (!configFile.exists()) {
            plugin.saveResource("modules/sniffer-drops.yml", false);
            plugin.getLogger().info("File Created: sniffer-drops.yml");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        drops = new RandomCollection<>();
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

                drops.add(from, Double.parseDouble(String.valueOf(toRaw)));
            });
        }
    }

    @Override
    public void onDisable() {
        if (drops != null) {
            drops.clear();
            drops = null;
        }
    }

    @EventHandler
    public void onSnifferSniffItem(EntityDropItemEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Sniffer) || drops.isEmpty()) {
            return;
        }

        event.getItemDrop().setItemStack(new ItemStack(drops.next()));
    }
}
