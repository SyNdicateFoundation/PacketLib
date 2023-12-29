package dev.mhpro.packetlib.handlers;

import dev.mhpro.packetlib.data.BlockPosition;
import dev.mhpro.packetlib.data.Position;
import dev.mhpro.packetlib.enums.HandshakeState;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.objects.Player;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.packets.client.handshake.ClientHandshake;
import dev.mhpro.packetlib.packets.client.status.ClientLegacyStatusPing;
import dev.mhpro.packetlib.packets.client.status.ClientPingRequest;
import dev.mhpro.packetlib.packets.client.login.ClientLoginStart;
import dev.mhpro.packetlib.packets.client.status.ClientStatusRequest;
import dev.mhpro.packetlib.packets.server.status.ServerPingResponse;
import dev.mhpro.packetlib.packets.server.status.ServerStatusResponse;
import dev.mhpro.packetlib.packets.server.login.ServerLoginSuccess;
import dev.mhpro.packetlib.packets.server.play.*;
import dev.mhpro.packetlib.utils.Logger;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = false)
@Data
public class PacketInboundHandler extends SimpleChannelInboundHandler<Packet> {
    private final Player player;

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        player.getServer().removePlayer(player);
        player.getServer().callEvent("onPlayerLeave", player);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Logger.error(this.getClass(), cause);
        super.exceptionCaught(ctx, cause);
    }

    @SneakyThrows
    @Override
    public void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull Packet packet) {
        if (!player.isConnected()) return;


        EventHandler.PacketEvent event = EventHandler.PacketEvent.builder()
                .player(player)
                .packet(packet)
                .build();

        player.getServer().callEvent("onPacketReceive", event);

        if (event.isCanceled()) return;

        if (packet instanceof ClientHandshake) {
            ClientHandshake handshake = (ClientHandshake) packet;

            player.setCurrentState(handshake.getState());
            player.setVersion(handshake.getVersion());

            switch (handshake.getState()) {
                case STATUS:
                    break;

                case LOGIN:
                    if (player.getVersion() == null) {
                        player.disconnect("&cUnsupported minecraft version: " + handshake.getProtocolInt());
                        return;
                    }

            }
            return;
        }

        if (packet instanceof ClientStatusRequest || packet instanceof ClientLegacyStatusPing) {
            if (player.getCurrentState() != HandshakeState.STATUS) {
                player.sendPacket(ServerStatusResponse.builder()
                        .description("&cNo handshake packet sent before (Bot)")
                        .version(ProtocolVersion.v1_8)
                        .enforcesSecureChat(true)
                        .supportAll(true)
                        .playerList(new ServerStatusResponse.PlayerList(Collections.emptyList(), -1, -1))
                        .build()).addListener(ChannelFutureListener.CLOSE);
                return;
            }

            return;
        }

        if (packet instanceof ClientPingRequest) {
            if (player.getCurrentState() != HandshakeState.STATUS) {
                player.sendPacket(ServerStatusResponse.builder()
                        .description("&cNo handshake packet sent before (Bot)")
                        .version(ProtocolVersion.v1_8)
                        .enforcesSecureChat(true)
                        .supportAll(true)
                        .playerList(new ServerStatusResponse.PlayerList(Collections.emptyList(), -1, -1))
                        .build());
                player.getChannel().disconnect().sync();
                return;
            }

            player.sendPacket(new ServerPingResponse(((ClientPingRequest) packet).getPayload()));
            return;
        }

        if (packet instanceof ClientLoginStart) {
            if (player.getCurrentState() != HandshakeState.LOGIN) {
                player.disconnect("&cNo handshake packet sent before (Bot)");
                return;
            }


            ClientLoginStart login = (ClientLoginStart) packet;

            if (!Pattern.matches("[A-z0-9_]{3,16}", login.getUsername())) {
                player.disconnect("&cBad username only accept &6(A-z 1-9 And Underline)");
                return;
            }


            UUID uuid = login.getUuid() == null ? UUID.nameUUIDFromBytes(("OfflinePlayer:" + login.getUsername()).getBytes(StandardCharsets.UTF_8)) : login.getUuid();

            if (player.getServer().isOnline(login.getUsername()) || player.getServer().isOnline(uuid)) {
                player.disconnect("&cAlready connected to server");
                return;
            }

            player.setUuid(uuid);
            player.setName(login.getUsername());

            EventHandler.CancelableEvent event1 = EventHandler.CancelableEvent.builder()
                    .player(player)
                    .build();

            player.getServer().callEvent("onPlayerLogin", event1);

            if (event1.isCanceled()) return;

            player.sendPacket(ServerLoginSuccess.builder()
                    .uuid(player.getUuid())
                    .username(player.getName())
                    .build());
            player.setCurrentState(HandshakeState.PLAY);

            player.sendPacket(ServerJoinGamePacket.builder()
                    .currentWorld(player.getServer().getMainLevel())
                    .worlds(player.getServer().getWorlds().toArray(new dev.mhpro.packetlib.objects.world.World[0]))
                    .build());

            player.sendPacket(ServerAbilitiesPacket.builder()
                    .walkingSpeed(0.F)
                    .flags(0x02)
                    .flyingSpeed(0.F)
                    .build());

            Position spawn = Position.empty()
                    .add(0, player.getVersion().moreOrEqual(ProtocolVersion.v1_9) ? 400 : 64, 0);

            player.sendPacket(ServerPostionAndLookPacket.builder()
                    .position(spawn)
                    .teleportId(ThreadLocalRandom.current().nextInt())
                    .flags((byte) 0x08)
                    .build());


            if (player.getVersion().moreOrEqual(ProtocolVersion.v1_19_3)) {
                player.sendPacket(ServerSpawnPositionPacket.builder()
                        .location(new BlockPosition(spawn))
                        .build());
            }


            player.sendPacket(ServerPluginMessage.builder()
                    .message(PacketBuffer.string("PacketLib"))
                    .channel(player.getVersion().lessOrEqual(ProtocolVersion.v1_12_2) ? "MC|Brand" : "minecraft:brand")
                    .build());

            player.getServer().callEvent("onPlayerJoin", player);
        }
    }
}
