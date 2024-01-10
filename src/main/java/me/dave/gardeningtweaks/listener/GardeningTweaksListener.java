package me.dave.gardeningtweaks.listener;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.api.events.SaplingReplantEvent;
import me.dave.platyutils.listener.EventListener;
import org.bukkit.event.EventHandler;

public class GardeningTweaksListener implements EventListener {

    @EventHandler
    public void onSaplingReplant(SaplingReplantEvent event) {
        if (event.getPlayer() == null && GardeningTweaks.getInstance().hasPrivateClaimAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
