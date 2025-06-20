package org.lushplugins.gardeningtweaks.hooks.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.lushplugins.gardeningtweaks.hooks.HookId;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtocolLibHook extends PacketHook {
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public ProtocolLibHook() {
        super(HookId.PROTOCOL_LIB.toString());
    }

    @Override
    public void armInteractAnimation(Player player) {
        if (this.protocolManager == null) {
            return;
        }

        PacketContainer armAnimation = new PacketContainer(PacketType.Play.Server.ANIMATION);
        armAnimation.getIntegers()
            .write(0, player.getEntityId())
            .write(1, 0);

        protocolManager.sendServerPacket(player, armAnimation);
    }
}
