package org.lushplugins.gardeningtweaks.hook.claim;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.lushplugins.gardeningtweaks.hook.Hook;

public class GriefPreventionHook extends Hook implements ClaimHandler {

    @Override
    public boolean hasClaimAt(Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, true, null) != null;
    }
}
