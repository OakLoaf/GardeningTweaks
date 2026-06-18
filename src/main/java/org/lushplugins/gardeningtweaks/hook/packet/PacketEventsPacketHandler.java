package org.lushplugins.gardeningtweaks.hook.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import org.bukkit.entity.Player;

public class PacketEventsPacketHandler implements PacketHandler {

    @Override
    public void armInteractAnimation(Player player) {
        WrapperPlayServerEntityAnimation packet = new WrapperPlayServerEntityAnimation(player.getEntityId(), WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}
