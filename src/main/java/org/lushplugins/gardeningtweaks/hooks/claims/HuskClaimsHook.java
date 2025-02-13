package org.lushplugins.gardeningtweaks.hooks.claims;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.lushplugins.gardeningtweaks.hooks.HookId;
import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.libraries.cloplib.operation.Operation;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;

public class HuskClaimsHook extends ClaimHook {
    private final OperationType interactiveHarvestOperationType;

    public HuskClaimsHook() {
        super(HookId.HUSK_CLAIMS.toString());

        BukkitHuskClaimsAPI huskClaimsAPI = BukkitHuskClaimsAPI.getInstance();
        interactiveHarvestOperationType = huskClaimsAPI.getOperationTypeRegistry().createOperationType(Key.key(
            "gardening_tweaks",
            "interactive_harvest")
        );
        huskClaimsAPI.getOperationTypeRegistry().registerOperationType(interactiveHarvestOperationType);
    }

    @Override
    public boolean hasClaimAt(Location location) {
        BukkitHuskClaimsAPI huskClaimsAPI = BukkitHuskClaimsAPI.getInstance();
        return huskClaimsAPI.isOperationAllowed(Operation.of(OperationType.BLOCK_PLACE, huskClaimsAPI.getPosition(location)));
    }

    public boolean isInteractiveHarvestCancelled(Player player, Location location) {
        BukkitHuskClaimsAPI huskClaimsAPI = BukkitHuskClaimsAPI.getInstance();
        return huskClaimsAPI.getOperationTypeRegistry().getHandler().cancelOperation(Operation.of(
            huskClaimsAPI.getOnlineUser(player),
            interactiveHarvestOperationType,
            huskClaimsAPI.getPosition(location)
        ));
    }
}
