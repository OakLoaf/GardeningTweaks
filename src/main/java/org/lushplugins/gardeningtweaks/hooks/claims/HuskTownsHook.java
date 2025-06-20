package org.lushplugins.gardeningtweaks.hooks.claims;

import net.william278.husktowns.api.BukkitHuskTownsAPI;
import net.william278.husktowns.libraries.cloplib.operation.Operation;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import org.lushplugins.gardeningtweaks.hooks.HookId;
import org.bukkit.Location;

public class HuskTownsHook extends ClaimHook {

    public HuskTownsHook() {
        super(HookId.HUSK_TOWNS.toString());
    }

    @Override
    public boolean hasClaimAt(Location location) {
        BukkitHuskTownsAPI huskTownsApi = BukkitHuskTownsAPI.getInstance();
        return huskTownsApi.isOperationAllowed(Operation.of(OperationType.BLOCK_PLACE, huskTownsApi.getPosition(location)));
    }
}