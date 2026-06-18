package org.lushplugins.gardeningtweaks.hook.claim;

import net.william278.husktowns.api.BukkitHuskTownsAPI;
import net.william278.husktowns.libraries.cloplib.operation.Operation;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;
import org.lushplugins.gardeningtweaks.hook.Hook;

public class HuskTownsHook extends Hook implements ClaimHandler {

    @Override
    public boolean hasClaimAt(Location location) {
        BukkitHuskTownsAPI huskTownsApi = BukkitHuskTownsAPI.getInstance();
        return huskTownsApi.isOperationAllowed(Operation.of(OperationType.BLOCK_PLACE, huskTownsApi.getPosition(location)));
    }
}