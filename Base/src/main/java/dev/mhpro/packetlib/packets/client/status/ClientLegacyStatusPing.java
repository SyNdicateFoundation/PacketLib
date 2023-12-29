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
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ClientLegacyStatusPing implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0xFE);
    private int payload;

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeVarInt(payload);
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        this.payload = input.readVarInt();
    }
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

}
