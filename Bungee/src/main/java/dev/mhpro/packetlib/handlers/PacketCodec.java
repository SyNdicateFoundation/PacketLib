package dev.mhpro.packetlib.handlers;

import dev.mhpro.packetlib.PacketLibAPI;
import dev.mhpro.packetlib.enums.HandshakeState;
import dev.mhpro.packetlib.events.PacketEvent;
import dev.mhpro.packetlib.events.PacketLoginEvent;
import dev.mhpro.packetlib.objects.impl.PacketUser;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.packets.client.handshake.ClientHandshake;
import dev.mhpro.packetlib.packets.client.login.ClientLoginStart;
import dev.mhpro.packetlib.packets.client.status.ClientStatusRequest;
import dev.mhpro.packetlib.server.codec.PacketConverterServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.protocol.PacketWrapper;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PacketCodec extends MessageToMessageCodec<PacketWrapper, Packet> {
    private final PacketUser player;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.printf("Error in %s%n", this.getClass().getSimpleName());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) {
        ByteBuf empty = Unpooled.buffer();
        PacketConverterServer.writePacket(player, msg, empty);
        out.add(new PacketWrapper(null, empty));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, PacketWrapper msg, List<Object> out) {
        if (player.getCurrentState() == null) return;

        ArrayList<Object> arrayList = new ArrayList<>();

        PacketBuffer buffer = new PacketBuffer(msg.buf);

        try {
            PacketConverterServer.readPacket(player, arrayList, buffer, player.getCurrentState().getClientPacketsInstances());

            for (Object o : arrayList) {
                Packet packet = (Packet) o;

                if (packet instanceof ClientHandshake) {
                    ClientHandshake clientHandshake = (ClientHandshake) packet;
                    player.setCurrentState(clientHandshake.getState());
                    player.setVersion(clientHandshake.getVersion());
                }

                if (packet instanceof ClientLoginStart) {
                    ClientLoginStart clientLoginStart = (ClientLoginStart) packet;
                    player.setName(clientLoginStart.getUsername());
                    player.setUuid(clientLoginStart.getUuid());
                    player.setCurrentState(HandshakeState.PLAY);

                    PacketLoginEvent event1 = new PacketLoginEvent(player);
                    PacketLibAPI.callEvent(event1);

                    if (event1.isCancelled()){
                        player.disconnect(event1.getCancelReason());
                        break;
                    }
                }

                if (packet instanceof ClientStatusRequest){
                    player.setCurrentState(HandshakeState.STATUS);
                }

                PacketEvent event = new PacketEvent(packet, PacketLibAPI.toProxied(player.getUuid()), player);
                PacketLibAPI.callEvent(event);

                if (event.isCancelled()) return;

                buffer.clear();
                PacketConverterServer.writePacket(player, packet, buffer);
            }

            out.add(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buffer.resetReaderIndex();
        }
    }
}
