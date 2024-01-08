package me.dave.gardeningtweaks.hooks.claims;

import me.dave.gardeningtweaks.hooks.Hook;
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
