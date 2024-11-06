package org.lushplugins.gardeningtweaks.hooks.claims;

import me.angeschossen.lands.api.LandsIntegration;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.gardeningtweaks.hooks.HookId;
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
