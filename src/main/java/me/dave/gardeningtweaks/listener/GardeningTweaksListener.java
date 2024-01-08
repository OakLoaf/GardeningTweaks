package me.dave.gardeningtweaks.listener;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.api.events.SaplingReplantEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GardeningTweaksListener implements Listener {

    @EventHandler
    public void onSaplingReplant(SaplingReplantEvent event) {
        if (event.getPlayer() == null && GardeningTweaks.getInstance().hasPrivateClaimAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
