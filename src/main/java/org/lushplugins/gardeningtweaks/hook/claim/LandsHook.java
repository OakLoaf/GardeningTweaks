package org.lushplugins.gardeningtweaks.hook.claim;

import me.angeschossen.lands.api.LandsIntegration;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.bukkit.Location;
import org.lushplugins.gardeningtweaks.hook.Hook;

public class LandsHook extends Hook implements ClaimHandler {

    @Override
    public boolean hasClaimAt(Location location) {
        LandsIntegration landsApi = LandsIntegration.of(GardeningTweaks.getInstance());
        return (location.getChunk().isLoaded() ? landsApi.getArea(location) : landsApi.getUnloadedArea(location)) != null;
    }
}
