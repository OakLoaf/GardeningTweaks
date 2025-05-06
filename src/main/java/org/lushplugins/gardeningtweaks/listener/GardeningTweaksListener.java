package org.lushplugins.gardeningtweaks.listener;

import org.bukkit.event.Listener;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.api.events.SaplingReplantEvent;
import org.bukkit.event.EventHandler;

public class GardeningTweaksListener implements Listener {

    @EventHandler
    public void onSaplingReplant(SaplingReplantEvent event) {
        if (event.getPlayer() == null && GardeningTweaks.getInstance().hasPrivateClaimAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
