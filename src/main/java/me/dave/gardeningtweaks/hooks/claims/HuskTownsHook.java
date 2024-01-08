package me.dave.gardeningtweaks.hooks.claims;

import net.william278.husktowns.api.HuskTownsAPI;
import net.william278.husktowns.listener.Operation;
import org.bukkit.Location;

public class HuskTownsHook extends ClaimHook {
    public static String ID = "husk-towns";

    public HuskTownsHook() {
        super(ID);
    }

    @Override
    public boolean hasClaimAt(Location location) {
        HuskTownsAPI huskTownsApi = HuskTownsAPI.getInstance();
        return huskTownsApi.isOperationAllowed(Operation.of(Operation.Type.BLOCK_PLACE, huskTownsApi.getPosition(location)));
    }
}