package org.lushplugins.gardeningtweaks.hook.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class ProtocolLibPacketHandler implements PacketHandler {
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

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
