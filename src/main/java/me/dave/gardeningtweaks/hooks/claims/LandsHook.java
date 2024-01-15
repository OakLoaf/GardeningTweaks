package me.dave.gardeningtweaks.hooks.claims;

import me.angeschossen.lands.api.LandsIntegration;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.hooks.HookId;
import org.bukkit.Location;

public class LandsHook extends ClaimHook {

    public LandsHook() {
        super(HookId.LANDS.toString());
    }

    @Override
    public boolean hasClaimAt(Location location) {
        LandsIntegration landsApi = LandsIntegration.of(GardeningTweaks.getInstance());
        return (location.getChunk().isLoaded() ? landsApi.getArea(location) : landsApi.getUnloadedArea(location)) != null;
    }
}
