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
public class ServerKeepAlivePacket implements Packet {

    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(0x00, ProtocolVersion.v1_8),
            new VersionMapping<>(0x1f, ProtocolVersion.v1_9, ProtocolVersion.v1_9_1, ProtocolVersion.v1_9_2, ProtocolVersion.v1_9_4, ProtocolVersion.v1_10, ProtocolVersion.v1_11, ProtocolVersion.v1_12, ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2, ProtocolVersion.v1_16_2, ProtocolVersion.v1_19_3),
            new VersionMapping<>(0x21, ProtocolVersion.v1_13, ProtocolVersion.v1_13_1, ProtocolVersion.v1_13_2, ProtocolVersion.v1_15, ProtocolVersion.v1_15_1, ProtocolVersion.v1_15_2, ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, ProtocolVersion.v1_18, ProtocolVersion.v1_18_2),
            new VersionMapping<>(0x20, ProtocolVersion.v1_14, ProtocolVersion.v1_14_1, ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_4, ProtocolVersion.v1_16, ProtocolVersion.v1_16_1, ProtocolVersion.v1_19_2),
            new VersionMapping<>(0x1e, ProtocolVersion.v1_19),
            new VersionMapping<>(0x23, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20)
    );
    private long keepAliveId;
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        switch (version) {
            case v1_9_2:
            case v1_8:
            case v1_11:
            case v1_12_1:
            case v1_9:
            case v1_12:
            case v1_10:
            case v1_9_4:
            case v1_9_1:
                this.keepAliveId = input.readVarInt();
                break;

            default:
                this.keepAliveId = input.readLong();
                break;
        }
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        switch (version) {
            case v1_9_2:
            case v1_8:
            case v1_11:
            case v1_12_1:
            case v1_9:
            case v1_12:
            case v1_10:
            case v1_9_4:
            case v1_9_1:
                output.writeVarInt((int) this.keepAliveId);
                break;

            default:
                output.writeLong(this.keepAliveId);
                break;
        }

    }


}
