package dev.mhpro.packetlib.handlers;

import dev.mhpro.packetlib.MinecraftClient;
import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.client.handshake.ClientHandshake;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class PacketOutboundStream extends ChannelOutboundHandlerAdapter {
    private final MinecraftClient player;

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if (!player.isConnected()) return;

        if (!(packet instanceof Packet)) {
            System.out.println("Invalid packet with type " + packet.getClass().getSimpleName());
            return;
        }


        if (player.getCurrentState() != null && !player.getCurrentState().hasPacket((Packet) packet)) {
            System.out.println("Unregistered packet " + packet.getClass().getSimpleName() + " " + player.getCurrentState());
            return;
        }

        EventHandler.PacketEvent event = EventHandler.PacketEvent.builder()
                .packet(((Packet) packet))
                .build();

        player.callEvent("onPacketSent",event);

        if (event.isCanceled()) return;

        if (packet instanceof ClientHandshake){
            player.setCurrentState(((ClientHandshake) packet).getState());
        }

        super.write(ctx, packet, promise);
    }
}
