package me.dave.gardeningtweaks.hooks;

import java.util.HashMap;
import java.util.Optional;

public interface Hook {
    HashMap<String, Hook> hooks = new HashMap<>();

    String getId();

    static Optional<Hook> get(String hookId) {
        return Optional.ofNullable(hooks.get(hookId));
    }

    static void register(Hook hook) {
        hooks.put(hook.getId(), hook);
    }

    static void unregister(String hookId) {
        hooks.remove(hookId);
    }

    static void unregisterAll() {
        hooks.clear();
    }
}
