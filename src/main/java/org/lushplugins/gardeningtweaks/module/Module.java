package org.lushplugins.gardeningtweaks.module;

public class Module {

    public void onEnable() {}

    public void enable() {
        onEnable();
    }

    public void onDisable() {}

    public void disable() {
        onDisable();
    }

    public void reload() {
        disable();
        enable();
    }
}
