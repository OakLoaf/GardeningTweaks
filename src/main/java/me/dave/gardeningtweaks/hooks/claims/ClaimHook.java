package me.dave.gardeningtweaks.hooks.claims;

import org.lushplugins.lushlib.hook.Hook;
import org.bukkit.Location;

public abstract class ClaimHook extends Hook {

    public ClaimHook(String id) {
        super(id);
    }

    abstract public boolean hasClaimAt(Location location);
}
