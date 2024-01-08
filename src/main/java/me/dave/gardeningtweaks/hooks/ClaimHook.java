package me.dave.gardeningtweaks.hooks;

import org.bukkit.Location;

public interface ClaimHook extends Hook {

    boolean hasClaimAt(Location location);

    static boolean hasPrivateClaimAt(Location location) {
        for (Hook hook : hooks.values()) {
            if (hook instanceof ClaimHook claimHook && claimHook.hasClaimAt(location)) {
                return true;
            }
        }

        return false;
    }
}
