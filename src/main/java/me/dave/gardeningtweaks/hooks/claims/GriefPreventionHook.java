package me.dave.gardeningtweaks.hooks.claims;

import me.dave.gardeningtweaks.hooks.claims.ClaimHook;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class GriefPreventionHook implements ClaimHook {
    public static String ID = "grief-prevention";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasClaimAt(Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, true, null) != null;
    }
}
