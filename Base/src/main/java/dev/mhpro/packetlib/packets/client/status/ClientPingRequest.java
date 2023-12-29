package dev.mhpro.packetlib.packets.client.status;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;

@Data
@With
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientPingRequest implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0x01);
    private long payload;

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeLong(payload);
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        this.payload = input.readLong();
    }
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

}
