package dev.mhpro.packetlib.packets.server;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerRawPacket implements Packet {
    private final PacketBuffer buffer;

    @Override
    public void read(PacketBuffer input, ProtocolVersion protocolVersion, int packetId) {
        input.writeBytes(buffer);
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion protocolVersion, int packetId) {
        buffer.clear();
        output.readBytes(buffer);
    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }
}
