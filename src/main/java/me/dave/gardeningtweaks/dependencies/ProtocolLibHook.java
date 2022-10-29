package me.dave.gardeningtweaks.dependencies;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtocolLibHook {
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public boolean armInteractAnimation(Player player) {
        if (protocolManager != null) {
            PacketContainer armAnimation = new PacketContainer(PacketType.Play.Server.ANIMATION);
            armAnimation.getIntegers()
                .write(0, player.getEntityId())
                .write(1, 0);

            try {
                protocolManager.sendServerPacket(player, armAnimation);
                return true;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
