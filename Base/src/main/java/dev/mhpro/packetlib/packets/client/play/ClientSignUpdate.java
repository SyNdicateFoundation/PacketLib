package dev.mhpro.packetlib.packets.client.play;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.mapping.VersionMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.play.iSignUpdate;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@With
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class ClientSignUpdate extends iSignUpdate implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(0x12, ProtocolVersion.v1_8),
            new VersionMapping<>(0x19, ProtocolVersion.v1_9, ProtocolVersion.v1_9_1, ProtocolVersion.v1_9_2, ProtocolVersion.v1_9_4, ProtocolVersion.v1_10, ProtocolVersion.v1_11),
            new VersionMapping<>(0x1c, ProtocolVersion.v1_12, ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2),
            new VersionMapping<>(0x26, ProtocolVersion.v1_13, ProtocolVersion.v1_13_1, ProtocolVersion.v1_13_2),
            new VersionMapping<>(0x29, ProtocolVersion.v1_14, ProtocolVersion.v1_14_1, ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_4, ProtocolVersion.v1_15, ProtocolVersion.v1_15_1, ProtocolVersion.v1_15_2),
            new VersionMapping<>(0x2a, ProtocolVersion.v1_16, ProtocolVersion.v1_16_1),
            new VersionMapping<>(0x2b, ProtocolVersion.v1_16_2, ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, ProtocolVersion.v1_18, ProtocolVersion.v1_18_2),
            new VersionMapping<>(0x2d, ProtocolVersion.v1_19),
            new VersionMapping<>(0x2e, ProtocolVersion.v1_19_2, ProtocolVersion.v1_19_3, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20)
    );
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
