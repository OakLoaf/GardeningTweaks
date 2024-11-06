package me.dave.gardeningtweaks.util;

import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.registry.RegistryUtils;

import java.util.Collection;
import java.util.Collections;

public class ConfigUtils {

    public static <T extends Keyed> @Nullable T getRegistryValue(ConfigurationSection config, String path, Registry<T> registry) {
        String key = config.getString(path);
        if (key == null) {
            return null;
        }

        return RegistryUtils.parseString(key, registry);
    }

    public static <T extends Keyed> Collection<T> getRegistryValues(ConfigurationSection config, String path, Registry<T> registry) {
        if (!config.isList(path)) {
            String key = config.getString(path);
            if (key == null) {
                return Collections.emptyList();
            }

            return RegistryUtils.fromString(key, registry);
        } else {
            return RegistryUtils.fromStringList(config.getStringList(path), registry);
        }
    }
}
