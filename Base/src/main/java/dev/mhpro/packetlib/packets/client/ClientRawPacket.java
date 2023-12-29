package dev.mhpro.packetlib.packets.client;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.packets.RawPacket;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientRawPacket implements RawPacket {
    private  int packetId;
    private final PacketBuffer buffer;

    @Override
    public void read(PacketBuffer input, ProtocolVersion protocolVersion, int packetId) {
        input.writeBytes(buffer);
        this.packetId=  packetId;
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion protocolVersion, int packetId) {
        buffer.clear();
        output.readBytes(buffer);
    }

    @Override
    public void writeRaw(PacketBuffer output, ProtocolVersion protocolVersion) {
        output.writeVarInt(packetId);
        this.write(output, protocolVersion, packetId);
    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public boolean isSupport(ProtocolVersion version, int packetId) {
        return false;
    }
}
