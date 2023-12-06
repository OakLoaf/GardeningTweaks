package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import org.jetbrains.annotations.NotNull;

public abstract class Module {
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
}