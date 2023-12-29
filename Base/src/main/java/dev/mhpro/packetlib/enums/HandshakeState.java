package dev.mhpro.packetlib.enums;

import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.client.handshake.ClientHandshake;
import dev.mhpro.packetlib.packets.client.login.ClientLoginStart;
import dev.mhpro.packetlib.packets.client.play.ClientChatPacket;
import dev.mhpro.packetlib.packets.client.play.ClientKeepAlivePacket;
import dev.mhpro.packetlib.packets.client.play.ClientSignUpdate;
import dev.mhpro.packetlib.packets.client.status.ClientLegacyStatusPing;
import dev.mhpro.packetlib.packets.client.status.ClientPingRequest;
import dev.mhpro.packetlib.packets.client.status.ClientStatusRequest;
import dev.mhpro.packetlib.packets.server.status.ServerPingResponse;
import dev.mhpro.packetlib.packets.server.status.ServerStatusResponse;
import dev.mhpro.packetlib.packets.server.login.ServerLoginDisconnect;
import dev.mhpro.packetlib.packets.server.login.ServerLoginSuccess;
import dev.mhpro.packetlib.packets.server.login.ServerSetCompression;
import dev.mhpro.packetlib.packets.server.play.*;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public enum HandshakeState {
    HANDSHAKE {
        @Getter
        private final List<Class<? extends Packet>> clientPackets = Arrays.asList(
                ClientHandshake.class
        );

        @Getter
        private final List<Class<? extends Packet>> serverPackets = Arrays.asList(
        );
    },
    STATUS {
        @Getter
        private final List<Class<? extends Packet>> clientPackets = Arrays.asList(
                ClientStatusRequest.class,
                ClientLegacyStatusPing.class,
                ClientPingRequest.class
        );

        @Getter
        private final List<Class<? extends Packet>> serverPackets = Arrays.asList(
                ServerStatusResponse.class,
                ServerPingResponse.class
        );
    },
    LOGIN {
        @Getter
        private final List<Class<? extends Packet>> clientPackets = Arrays.asList(
                ClientLoginStart.class
        );

        @Getter
        private final List<Class<? extends Packet>> serverPackets = Arrays.asList(
                ServerLoginSuccess.class,
                ServerSetCompression.class,
                ServerLoginDisconnect.class
        );
    },
    PLAY {
        @Getter
        private final List<Class<? extends Packet>> clientPackets = Arrays.asList(
                ClientChatPacket.class,
                ClientSignUpdate.class,
                ClientKeepAlivePacket.class
        );

        @Getter
        private final List<Class<? extends Packet>> serverPackets = Arrays.asList(
                ServerKeepAlivePacket.class,
                ServerPostionAndLookPacket.class,
                ServerPlayDisconnect.class,
                ServerMessagePacket.class,
                ServerPluginMessage.class,
                ServerSpawnPositionPacket.class,
                ServerBossbarPacket.class,
                ServerTitlePacket.class,
                ServerAbilitiesPacket.class,
                ServerJoinGamePacket.class
        );

    };

    public abstract List<Class<? extends Packet>> getClientPackets();

    public abstract List<Class<? extends Packet>> getServerPackets();

    private  List<Packet> clientInstances, serverInstances;


    @SneakyThrows
    public List<Packet> getClientPacketsInstances() {
        if (clientInstances == null){
            clientInstances = new ArrayList<>();
            for (Class<? extends Packet> packet : this.getClientPackets()) {
                clientInstances.add(packet.getConstructor().newInstance());
            }
        }
        return clientInstances;
    }

    @SneakyThrows
    public List<Packet> getServerPacketsInstances() {
        if (serverInstances == null){
            serverInstances = new ArrayList<>();
            for (Class<? extends Packet> packet : this.getServerPackets()) {
                serverInstances.add(packet.getConstructor().newInstance());
            }
        }
        return serverInstances;
    }


    public boolean hasPacket(Packet packet) {
        return this.getServerPackets().contains(packet.getClass()) ||
                this.getClientPackets().contains(packet.getClass());
    }
}
