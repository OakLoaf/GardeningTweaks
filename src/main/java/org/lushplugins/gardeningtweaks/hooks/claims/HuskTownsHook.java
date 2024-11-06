package org.lushplugins.gardeningtweaks.hooks.claims;

import org.lushplugins.gardeningtweaks.hooks.HookId;
import net.william278.husktowns.api.HuskTownsAPI;
import net.william278.husktowns.listener.Operation;
import org.bukkit.Location;

public class HuskTownsHook extends ClaimHook {

    public HuskTownsHook() {
        super(HookId.HUSK_TOWNS.toString());
    }

    @Override
    public boolean hasClaimAt(Location location) {
        HuskTownsAPI huskTownsApi = HuskTownsAPI.getInstance();
        return huskTownsApi.isOperationAllowed(Operation.of(Operation.Type.BLOCK_PLACE, huskTownsApi.getPosition(location)));
    }
}