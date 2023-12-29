package dev.mhpro.packetlib.handlers;

import dev.mhpro.packetlib.PacketLibAPI;
import dev.mhpro.packetlib.enums.HandshakeState;
import dev.mhpro.packetlib.events.PacketEvent;
import dev.mhpro.packetlib.events.PacketLoginSuccessEvent;
import dev.mhpro.packetlib.objects.impl.PacketUser;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.packets.server.status.ServerPingResponse;
import dev.mhpro.packetlib.packets.server.status.ServerStatusResponse;
import dev.mhpro.packetlib.packets.server.login.ServerLoginDisconnect;
import dev.mhpro.packetlib.packets.server.login.ServerLoginSuccess;
import dev.mhpro.packetlib.packets.server.login.ServerSetCompression;
import dev.mhpro.packetlib.packets.server.play.ServerPlayDisconnect;
import dev.mhpro.packetlib.packets.server.play.ServerPluginMessage;
import dev.mhpro.packetlib.server.codec.PacketConverterServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.*;

import java.util.ArrayList;
import java.util.Arrays;

@RequiredArgsConstructor
public class PacketOutBound extends ChannelOutboundHandlerAdapter {
    private final PacketUser player;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof DefinedPacket) {
            Packet packet = null;

            if (msg instanceof StatusResponse) {
                packet = new ServerStatusResponse();
                ((ServerStatusResponse) packet).fromJson(((StatusResponse) msg).getResponse());
            }

            if (msg instanceof PingPacket) {
                packet = new ServerPingResponse();
                ((ServerPingResponse) packet).setPayload(((PingPacket) msg).getTime());
            }

            if (msg instanceof PluginMessage) {
                packet = new ServerPluginMessage();
                PluginMessage pluginMessage = (PluginMessage) msg;
                ((ServerPluginMessage) packet).setMessage(Unpooled.copiedBuffer(pluginMessage.getData()));
                ((ServerPluginMessage) packet).setChannel(pluginMessage.getTag());
            }

            if (msg instanceof SetCompression) {
                packet = new ServerSetCompression();
                SetCompression setCompression = (SetCompression) msg;
                ((ServerSetCompression) packet).setThreshold(setCompression.getThreshold());
            }

            if (msg instanceof LoginSuccess) {
                packet = new ServerLoginSuccess();
                LoginSuccess loginSuccess = (LoginSuccess) msg;
                ServerLoginSuccess success = (ServerLoginSuccess) packet;

                success.setUsername(loginSuccess.getUsername());
                success.setUuid(loginSuccess.getUuid());

                if (loginSuccess.getProperties() != null) {
                    success.setProperties(
                            Arrays.stream(loginSuccess.getProperties())
                                    .map(p -> p == null ? null : new ServerLoginSuccess.Property(p.getName(), p.getValue(), p.getSignature()))
                                    .toArray(ServerLoginSuccess.Property[]::new));
                }
            }

            if (msg instanceof Kick) {

                Kick kick = (Kick) msg;

                if (player.getCurrentState() == HandshakeState.PLAY) {
                    packet = new ServerPlayDisconnect();
                    ((ServerPlayDisconnect) packet).setReason(kick.getMessage());

                } else {
                    packet = new ServerLoginDisconnect();
                    ((ServerLoginDisconnect) packet).setReason(kick.getMessage());
                }
            }

            if (packet == null) {
                PacketLibAPI.getLogger().warning("UnSerialized Packet --> " + msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }

            PacketEvent event = new PacketEvent(packet, PacketLibAPI.toProxied(player.getUuid()), player);
            PacketLibAPI.callEvent(event);

            if (event.isCancelled()) return;

            PacketBuffer buffer = PacketBuffer.empty();
            PacketConverterServer.writePacket(player, packet, buffer);

            super.write(ctx, buffer, promise);

            return;
        }

        if (!(msg instanceof ByteBuf)) {
            super.write(ctx, msg, promise);
            return;
        }


        PacketBuffer buffer = new PacketBuffer(((ByteBuf) msg));

        ArrayList<Object> arrayList = new ArrayList<>();

        try {
            PacketConverterServer.readPacket(player, arrayList, buffer, player.getCurrentState().getServerPacketsInstances());

            for (Object o : arrayList) {
                Packet packet = (Packet) o;

                if (packet instanceof ServerLoginSuccess) {
                    ServerLoginSuccess serverLoginSuccess = (ServerLoginSuccess) packet;
                    player.setUuid(serverLoginSuccess.getUuid());
                    player.setName(serverLoginSuccess.getUsername());

                    PacketLoginSuccessEvent event = new PacketLoginSuccessEvent(player);
                    PacketLibAPI.callEvent(event);

                    if (event.isCancelled()) {
                        player.disconnect(event.getCancelReason());
                        return;
                    }
                }

                PacketEvent event = new PacketEvent(packet, PacketLibAPI.toProxied(player.getUuid()), player);
                PacketLibAPI.callEvent(event);

                if (event.isCancelled()) return;

                buffer.clear();
                PacketConverterServer.writePacket(player, packet, buffer);

                super.write(ctx, msg, promise);
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buffer.resetReaderIndex();
        }

        super.write(ctx, msg, promise);
    }


}
