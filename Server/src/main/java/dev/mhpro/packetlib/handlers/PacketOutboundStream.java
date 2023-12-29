package dev.mhpro.packetlib.handlers;

import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.objects.Player;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.utils.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class PacketOutboundStream extends ChannelOutboundHandlerAdapter {
    private final Player player;

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if (!player.isConnected()) return;

        if (!(packet instanceof Packet)) {
            Logger.error("Invalid packet with type " + packet.getClass().getSimpleName());
            return;
        }


        if (player.getCurrentState() != null && !player.getCurrentState().hasPacket((Packet) packet)) {
            Logger.error("Unregistered packet " + packet.getClass().getSimpleName() + " " + player.getCurrentState());
            return;
        }

        EventHandler.PacketEvent event = EventHandler.PacketEvent.builder()
                .player(player)
                .packet(((Packet) packet))
                .build();

        player.getServer().callEvent("onPacketSent", event);

        if (event.isCanceled()) return;

        super.write(ctx, packet, promise);
    }
}
