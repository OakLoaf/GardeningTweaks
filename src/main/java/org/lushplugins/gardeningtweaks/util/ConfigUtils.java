package org.lushplugins.gardeningtweaks.util;

import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.lushplugins.lushlib.utils.registry.RegistryUtils;

import java.util.Collection;
import java.util.Collections;

public class ConfigUtils {

    public static <T extends Keyed> Collection<T> getRegistryValues(ConfigurationSection config, String path, Registry<T> registry) {
        if (!config.isList(path)) {
            String key = config.getString(path);
            return key != null ? RegistryUtils.fromString(key, registry) : Collections.emptyList();
        } else {
            return RegistryUtils.fromStringList(config.getStringList(path), registry);
        }
    }
}
