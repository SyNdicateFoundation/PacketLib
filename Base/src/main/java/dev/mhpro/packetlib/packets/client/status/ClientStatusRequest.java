package dev.mhpro.packetlib.packets.client.status;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;

@Data
@With
@Builder
@NoArgsConstructor
@ToString(doNotUseGetters = true)
public class ClientStatusRequest implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0x00);

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        if (!input.isEmpty()) throw new IllegalArgumentException("Handshake packet detected!");
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion protocolVersion, int packetId) {
    }   @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

}
