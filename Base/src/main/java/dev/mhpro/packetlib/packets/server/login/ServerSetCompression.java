package dev.mhpro.packetlib.packets.server.login;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;

@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerSetCompression implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0x03);

    private int threshold;

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }
    @Override
    public void read(PacketBuffer input, ProtocolVersion protocolVersion, int packetId) {
        threshold = input.readVarInt();
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion protocolVersion, int packetId) {
        output.writeVarInt(threshold);
    }
}
