package org.lushplugins.gardeningtweaks.module;

import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.bukkit.Registry;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushlib.module.Module;
import org.lushplugins.lushlib.registry.RegistryUtils;
import org.lushplugins.lushlib.utils.RandomCollection;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class SnifferDrops extends Module implements EventListener {
    public static final String ID = "SNIFFER_DROPS";

    private RandomCollection<Material> drops;

    public SnifferDrops() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/sniffer-drops.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/sniffer-drops.yml"));

        drops = new RandomCollection<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            itemsSection.getValues(false).forEach((fromRaw, toRaw) -> {
                Material material = RegistryUtils.parseString(fromRaw, Registry.MATERIAL);
                if (material != null) {
                    drops.add(material, Double.parseDouble(String.valueOf(toRaw)));
                } else {
                    GardeningTweaks.getInstance().getLogger().warning("'" + fromRaw + "' is not a valid material");
                }
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
