package dev.mhpro.packetlib.packets.server.play;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.mapping.VersionMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;


@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerAbilitiesPacket implements Packet {

    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(0x39, ProtocolVersion.v1_8),
            new VersionMapping<>(0x2b, ProtocolVersion.v1_9, ProtocolVersion.v1_9_1, ProtocolVersion.v1_9_2, ProtocolVersion.v1_9_4, ProtocolVersion.v1_10, ProtocolVersion.v1_11, ProtocolVersion.v1_12),
            new VersionMapping<>(0x2c, ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2),
            new VersionMapping<>(0x2e, ProtocolVersion.v1_13, ProtocolVersion.v1_13_1, ProtocolVersion.v1_13_2),
            new VersionMapping<>(0x31, ProtocolVersion.v1_14, ProtocolVersion.v1_14_1, ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_4, ProtocolVersion.v1_16, ProtocolVersion.v1_16_1, ProtocolVersion.v1_19_2),
            new VersionMapping<>(0x32, ProtocolVersion.v1_15, ProtocolVersion.v1_15_1, ProtocolVersion.v1_15_2, ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, ProtocolVersion.v1_18, ProtocolVersion.v1_18_2),
            new VersionMapping<>(0x30, ProtocolVersion.v1_16_2, ProtocolVersion.v1_19_3),
            new VersionMapping<>(0x34, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20),
            new VersionMapping<>(0x2f, ProtocolVersion.v1_19)
    );
    private int flags;
    private float walkingSpeed;
    private float flyingSpeed;
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        this.flags = input.readByte();
        this.flyingSpeed = input.readFloat();
        this.walkingSpeed = input.readFloat();
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeByte(this.flags);
        output.writeFloat(this.flyingSpeed);
        output.writeFloat(this.walkingSpeed);
    }


}
