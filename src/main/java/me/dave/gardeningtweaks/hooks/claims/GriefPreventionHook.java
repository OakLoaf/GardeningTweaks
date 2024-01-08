package me.dave.gardeningtweaks.hooks.claims;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class GriefPreventionHook extends ClaimHook {
    public static String ID = "grief-prevention";

    public GriefPreventionHook(String id) {
        super(id);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasClaimAt(Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, true, null) != null;
    }
}
