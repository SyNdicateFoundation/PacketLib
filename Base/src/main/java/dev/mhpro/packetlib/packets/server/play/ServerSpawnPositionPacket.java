package dev.mhpro.packetlib.packets.server.play;

import dev.mhpro.packetlib.data.BlockPosition;
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
public class ServerSpawnPositionPacket implements Packet {

    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(0x05, ProtocolVersion.v1_8),
            new VersionMapping<>(0x43, ProtocolVersion.v1_9, ProtocolVersion.v1_9_1, ProtocolVersion.v1_9_2, ProtocolVersion.v1_9_4, ProtocolVersion.v1_10, ProtocolVersion.v1_11),
            new VersionMapping<>(0x45, ProtocolVersion.v1_12),
            new VersionMapping<>(0x46, ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2),
            new VersionMapping<>(0x49, ProtocolVersion.v1_13, ProtocolVersion.v1_13_1, ProtocolVersion.v1_13_2),
            new VersionMapping<>(0x4d, ProtocolVersion.v1_14, ProtocolVersion.v1_14_1, ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_4, ProtocolVersion.v1_19_2),
            new VersionMapping<>(0x4e, ProtocolVersion.v1_15, ProtocolVersion.v1_15_1, ProtocolVersion.v1_15_2),
            new VersionMapping<>(0x42, ProtocolVersion.v1_16, ProtocolVersion.v1_16_1, ProtocolVersion.v1_16_2),
            new VersionMapping<>(0x4b, ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, ProtocolVersion.v1_18, ProtocolVersion.v1_18_2),
            new VersionMapping<>(0x4a, ProtocolVersion.v1_19),
            new VersionMapping<>(0x4c, ProtocolVersion.v1_19_3),
            new VersionMapping<>(0x50, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20)
    );
    private float angle;
    private BlockPosition location;
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        this.location = input.readBlockPosition(version);

        switch (version) {
            case v1_18:
            case v1_19:
            case v1_17:
            case v1_19_3:
            case v1_19_4:
            case v1_18_2:
            case v1_19_2:
            case v1_20:
            case v1_17_1:
                this.angle = input.readFloat();
                break;
        }
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeBlockPosition(this.location, version);

        switch (version) {
            case v1_18:
            case v1_19:
            case v1_17:
            case v1_19_3:
            case v1_19_4:
            case v1_18_2:
            case v1_19_2:
            case v1_20:
            case v1_17_1:
                output.writeFloat(this.angle);
                break;
        }
    }


}
