package me.dave.gardeningtweaks.hooks.claims;

import me.dave.gardeningtweaks.hooks.claims.ClaimHook;
import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.libraries.cloplib.operation.Operation;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;

public class HuskClaimsHook implements ClaimHook {
    public static String ID = "husk-claims";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasClaimAt(Location location) {
        BukkitHuskClaimsAPI huskClaimsAPI = BukkitHuskClaimsAPI.getInstance();
        return huskClaimsAPI.isOperationAllowed(Operation.of(OperationType.BLOCK_PLACE, huskClaimsAPI.getPosition(location)));
    }
}
