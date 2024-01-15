package me.dave.gardeningtweaks.hooks.claims;

import me.dave.gardeningtweaks.hooks.HookId;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class GriefPreventionHook extends ClaimHook {

    public GriefPreventionHook() {
        super(HookId.GRIEF_PREVENTION.toString());
    }

    @Override
    public boolean hasClaimAt(Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, true, null) != null;
    }
}
