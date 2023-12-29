package dev.mhpro.packetlib.handlers;

import dev.mhpro.packetlib.MinecraftClient;
import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
@Data
public class PacketInboundHandler extends SimpleChannelInboundHandler<Packet> {
    private final MinecraftClient player;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(this.getClass());
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull Packet packet) {
        if (!player.isConnected()) return;

        if (player.getCurrentState() != null && !player.getCurrentState().hasPacket((Packet) packet)) {
            System.out.println("Unregistered packet " + packet.getClass().getSimpleName() + " " + player.getCurrentState());
            return;
        }

        EventHandler.PacketEvent event = EventHandler.PacketEvent.builder()
                .packet(packet)
                .build();

        player.callEvent("onPacketReceive",event);


        if (event.isCanceled()) return;


    }
}
