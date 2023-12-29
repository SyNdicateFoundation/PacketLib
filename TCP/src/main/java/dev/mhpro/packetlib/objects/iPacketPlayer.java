package dev.mhpro.packetlib.objects;

import dev.mhpro.packetlib.enums.HandshakeState;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.packets.Packet;

import java.util.UUID;

public interface iPacketPlayer {

    HandshakeState getCurrentState();

    ProtocolVersion getVersion();

    String getName();

    UUID getUuid();

    boolean isConnected();

    void sendPacket(Packet... packets);
}
