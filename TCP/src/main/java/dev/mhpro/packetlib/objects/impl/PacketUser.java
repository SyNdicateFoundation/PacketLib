package dev.mhpro.packetlib.objects.impl;

import dev.mhpro.packetlib.enums.HandshakeState;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.objects.iPacketPlayer;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.server.login.ServerLoginDisconnect;
import dev.mhpro.packetlib.packets.server.play.ServerPlayDisconnect;
import dev.mhpro.packetlib.util.ComponentColorize;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import lombok.*;

import java.util.UUID;

@Data
public class PacketUser extends ChannelHandlerAdapter implements iPacketPlayer {
    @ToString.Exclude
    private final Channel channel;
    private HandshakeState currentState = HandshakeState.HANDSHAKE;
    private ProtocolVersion version;
    private UUID uuid;
    private String name;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PacketUser)) return false;

        PacketUser player = (PacketUser) o;

        if (getUuid() != null ? !getUuid().equals(player.getUuid()) : player.getUuid() != null) return false;
        return getName() != null ? getName().equals(player.getName()) : player.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getUuid() != null ? getUuid().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }

    public ChannelFuture sendPacket(Packet packet) {
        if (!this.isConnected()) {
            throw new RuntimeException("Client session closed before sent packet!");
        }

        return channel.writeAndFlush(packet);
    }


    public boolean isPlaying() {
        return this.currentState == HandshakeState.PLAY;
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    @Override
    public void sendPacket(Packet... packets) {
        if (!this.isConnected()) {
            throw new RuntimeException("Client session closed before sent packet!");
        }

        for (Packet packet : packets) {
            channel.writeAndFlush(packet);
        }
    }

    @SneakyThrows
    public void disconnect() {
        channel.disconnect().sync();
    }

    @SneakyThrows
    public void disconnect(String message) {
        switch (currentState) {
            case PLAY:
                this.sendPacket(ServerPlayDisconnect.builder()
                        .reason(ComponentColorize.toComponent(message))
                        .build());
                break;
            case LOGIN:
                this.sendPacket(ServerLoginDisconnect.builder()
                        .reason(ComponentColorize.toComponent(message))
                        .build());
                break;
        }

        this.disconnect();
    }

}
