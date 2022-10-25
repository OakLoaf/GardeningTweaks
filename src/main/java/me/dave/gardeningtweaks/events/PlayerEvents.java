package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.utilities.GrowthDance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking() && GardeningTweaks.configManager.getGrowthDanceMode() != GrowthDance.OFF) {

        }
    }
}
