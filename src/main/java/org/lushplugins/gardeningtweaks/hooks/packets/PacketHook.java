package org.lushplugins.gardeningtweaks.hooks.packets;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.hook.Hook;

public abstract class PacketHook extends Hook {

    public PacketHook(String id) {
        super(id);
    }

    public abstract void armInteractAnimation(Player player);
}
