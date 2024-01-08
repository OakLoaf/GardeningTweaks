package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Module {
    private static final ConcurrentHashMap<String, Module> modules = new ConcurrentHashMap<>();

    private final String id;
    private boolean enabled = false;

    public Module(String id) {
        this.id = id.toLowerCase();
    }

    public void enable() {
        this.enabled = true;
        try {
            this.onEnable();
        } catch (Exception e) {
            GardeningTweaks.getInstance().getLogger().severe("Error when enabling module '" + id + "' at:");
            e.printStackTrace();
        }
    }

    public void onEnable() {}

    public void disable() {
        this.enabled = false;
        try {
            this.onDisable();
        } catch (Exception e) {
            GardeningTweaks.getInstance().getLogger().severe("Error when disabling module '" + id + "' at:");
            e.printStackTrace();
        }
        this.onDisable();
    }
    public void onDisable() {}

    public void reload() {
        try {
            this.onReload();
        } catch (Exception e) {
            GardeningTweaks.getInstance().getLogger().severe("Error when reloading module '" + id + "' at:");
            e.printStackTrace();
        }
    }

    public void onReload() {
        this.disable();
        this.enable();
    }

    @NotNull
    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static Optional<Module> get(String id) {
        return Optional.ofNullable(modules.get(id));
    }

    public static void register(Module module) {
        if (modules.containsKey(module.getId())) {
            GardeningTweaks.getInstance().getLogger().severe("Failed to register module with id '" + module.getId() + "', a module with this id is already running");
            return;
        }

        modules.put(module.getId(), module);
        module.enable();
        if (module instanceof Listener listener) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, GardeningTweaks.getInstance());
        }
    }

    public static void unregister(String moduleId) {
        Module module = modules.get(moduleId);
        if (module != null) {
            module.disable();

            if (module instanceof Listener listener) {
                HandlerList.unregisterAll(listener);
            }
        }
        modules.remove(moduleId);
    }

    public static void unregisterAll() {
        modules.values().forEach(Module::disable);
        modules.clear();
    }
}