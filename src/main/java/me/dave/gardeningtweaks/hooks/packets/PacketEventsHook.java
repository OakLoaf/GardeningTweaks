package me.dave.gardeningtweaks.hooks.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import me.dave.gardeningtweaks.hooks.HookId;
import org.bukkit.entity.Player;

public class PacketEventsHook extends PacketHook {

    public PacketEventsHook() {
        super(HookId.PACKET_EVENTS.toString());
    }

    @Override
    public void armInteractAnimation(Player player) {
        WrapperPlayServerEntityAnimation packet = new WrapperPlayServerEntityAnimation(player.getEntityId(), WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}
