package me.dave.gardeningtweaks.hooks;

public enum HookId {
    CORE_PROTECT("core_protect"),
    GRIEF_PREVENTION("grief_prevention"),
    HUSK_CLAIMS("husk_claims"),
    HUSK_TOWNS("husk_towns"),
    LANDS("lands"),
    PACKET_EVENTS("packet_events"),
    PROTOCOL_LIB("protocol_lib"),
    REALISTIC_BIOMES("realistic_biomes");

    private final String id;

    HookId(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
