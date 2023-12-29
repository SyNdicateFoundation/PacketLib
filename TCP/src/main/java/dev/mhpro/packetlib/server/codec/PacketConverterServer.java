package dev.mhpro.packetlib.server.codec;

import dev.mhpro.packetlib.objects.iPacketPlayer;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.packets.RawPacket;
import dev.mhpro.packetlib.packets.client.ClientRawPacket;
import dev.mhpro.packetlib.packets.client.play.ClientKeepAlivePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;


@RequiredArgsConstructor
public class PacketConverterServer extends ByteToMessageCodec<Packet> {
    private final iPacketPlayer player;

    public static void writePacket(iPacketPlayer player, Packet packet, ByteBuf out) {
        if (packet == null) return;

        PacketBuffer buffer = new PacketBuffer(out);

        if (packet instanceof RawPacket) {
            ((RawPacket) packet).writeRaw(buffer, player.getVersion());
            return;
        }

        Integer packetId = packet.getMapping().get(player.getVersion());
        buffer.writeVarInt(packetId);

        packet.write(buffer, player.getVersion(), packetId);
    }

    public static void readPacket(iPacketPlayer user, List<Object> out, PacketBuffer buffer, List<Packet> packets) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        int packetID;

        try {
            packetID = buffer.readVarInt();
        } catch (Exception ignored) {
            return;
        }

        for (Packet packet : packets) {
            if (packet instanceof RawPacket) {
                if (!((RawPacket) packet).isSupport(user.getVersion(), packetID)) continue;
            } else {
                Integer packetId = packet.getMapping().get(user.getVersion());
                if (packetId != packetID) continue;
            }

            try {
                Packet instance = packet.clone();

                instance.read(buffer, user.getVersion(), packetID);

                if (instance instanceof ClientKeepAlivePacket && ((ClientKeepAlivePacket) instance).getKeepAliveId() == 0)
                    return;

                out.add(instance);
                return;

            } catch (IllegalArgumentException | IndexOutOfBoundsException | CloneNotSupportedException ignored) {

            }
        }

        if (packetID >= 0) {
            buffer.resetReaderIndex();
            out.add(new ClientRawPacket(buffer.readVarInt(), new PacketBuffer(buffer.copy(buffer.readerIndex(), buffer.readableBytes()))));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.printf("Error in %s%n", this.getClass().getSimpleName());
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        writePacket(player, packet, out);

    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!ctx.channel().isActive()) return;
        if (player.getCurrentState() == null) return;

        PacketBuffer buffer = new PacketBuffer(in);
        readPacket(player, out, buffer, player.getCurrentState().getClientPacketsInstances());
    }

}
