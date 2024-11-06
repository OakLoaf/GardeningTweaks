package org.lushplugins.gardeningtweaks.hooks.claims;

import org.lushplugins.lushlib.hook.Hook;
import org.bukkit.Location;

public abstract class ClaimHook extends Hook {

    public ClaimHook(String id) {
        super(id);
    }

    public abstract boolean hasClaimAt(Location location);
}
