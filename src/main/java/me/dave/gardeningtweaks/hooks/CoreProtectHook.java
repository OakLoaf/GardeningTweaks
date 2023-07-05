package me.dave.gardeningtweaks.hooks;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class CoreProtectHook {
    CoreProtectAPI coreProtect;

    public CoreProtectHook() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that the API is enabled
        CoreProtectAPI coreProtect = ((CoreProtect) plugin).getAPI();
        if (!coreProtect.isEnabled()) return;

        // Check that a compatible version of the API is loaded
        if (coreProtect.APIVersion() < 9) return;

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

        return dataList.isEmpty();
    }
}
