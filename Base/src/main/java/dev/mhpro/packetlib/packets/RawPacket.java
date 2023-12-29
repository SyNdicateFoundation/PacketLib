package dev.mhpro.packetlib.packets;

import dev.mhpro.packetlib.enums.ProtocolVersion;

public interface RawPacket extends Packet {
    default void writeRaw(PacketBuffer output, ProtocolVersion protocolVersion) {
        throw new IllegalAccessError("Method write is not implemented!");
    }

    boolean isSupport(ProtocolVersion version, int packetId);
}
