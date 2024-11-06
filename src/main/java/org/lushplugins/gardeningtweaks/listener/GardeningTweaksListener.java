package org.lushplugins.gardeningtweaks.listener;

import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.api.events.SaplingReplantEvent;
import org.bukkit.event.EventHandler;
import org.lushplugins.lushlib.listener.EventListener;

public class GardeningTweaksListener implements EventListener {

    @EventHandler
    public void onSaplingReplant(SaplingReplantEvent event) {
        if (event.getPlayer() == null && GardeningTweaks.getInstance().hasPrivateClaimAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
