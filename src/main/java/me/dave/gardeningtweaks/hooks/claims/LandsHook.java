package me.dave.gardeningtweaks.hooks.claims;

import me.angeschossen.lands.api.LandsIntegration;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.Location;

public class LandsHook extends ClaimHook {
    public static final String ID = "LANDS";

    public LandsHook() {
        super(ID);
    }

    @Override
    public boolean hasClaimAt(Location location) {
        LandsIntegration landsApi = LandsIntegration.of(GardeningTweaks.getInstance());
        return (location.getChunk().isLoaded() ? landsApi.getArea(location) : landsApi.getUnloadedArea(location)) != null;
    }
}
