package dev.mhpro.packetlib.packets;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import lombok.SneakyThrows;

public interface Packet extends Cloneable {

    @SneakyThrows
    static Integer getPacketId(ProtocolVersion version, Packet packet) {
        return packet.getMapping().get(version);
    }

    default ProtocolMapping getMapping() {
        return null;
    }

    default void read(PacketBuffer input, ProtocolVersion protocolVersion, int packetId) {
        throw new IllegalAccessError(String.format("%s Method read is not implemented!", this.getClass().getSimpleName()));
    }

    default void write(PacketBuffer output, ProtocolVersion protocolVersion, int packetId) {
        throw new IllegalAccessError(String.format("%s Method write is not implemented!", this.getClass().getSimpleName()));
    }

    public Packet clone() throws CloneNotSupportedException;


}
