package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.data.ConfigManager;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CustomSnifferDrops implements Listener {

    @EventHandler
    public void onSnifferSniffItem(EntityDropItemEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Sniffer)) return;

        ConfigManager.CustomSnifferDrops customSnifferDrops = GardeningTweaks.getConfigManager().getCustomSnifferDrops();
        if (!customSnifferDrops.enabled() || customSnifferDrops.items().size() == 0) return;

        event.getItemDrop().setItemStack(new ItemStack(customSnifferDrops.items().get(GardeningTweaks.getRandom().nextInt(customSnifferDrops.items().size()))));
    }
}
