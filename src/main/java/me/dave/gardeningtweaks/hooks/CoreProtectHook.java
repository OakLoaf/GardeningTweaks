package me.dave.gardeningtweaks.hooks;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.platyutils.hook.Hook;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class CoreProtectHook extends Hook {
    public static String ID = "core-protect";
    private CoreProtectAPI coreProtect;

    @Override
    public String getId() {
        return ID;
    }

    public CoreProtectHook() {
        super(ID);
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that the API is enabled
        CoreProtectAPI coreProtect = ((CoreProtect) plugin).getAPI();

        // Check that a compatible version of the API is loaded
        if (coreProtect.APIVersion() < 9) {
            GardeningTweaks.getInstance().getLogger().warning("Using incompatible version of CoreProtect");
            return;
        }

        this.coreProtect = coreProtect;
    }

    public boolean isBlockNatural(Location location) {
        List<String[]> dataList = coreProtect.performLookup(
            315360000 /* 10 years in seconds */,
            null,
            null,
            null,
            null,
            Arrays.asList(1) /* Checks for place action only */,
            0,
            location
        );
        if (dataList == null) return true;

        return dataList.isEmpty();
    }
}
