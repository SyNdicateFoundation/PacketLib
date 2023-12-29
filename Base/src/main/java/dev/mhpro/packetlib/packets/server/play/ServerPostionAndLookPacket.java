package dev.mhpro.packetlib.packets.server.play;

import dev.mhpro.packetlib.data.Position;
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
public class ServerPostionAndLookPacket implements Packet {

    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(0x08, ProtocolVersion.v1_8),
            new VersionMapping<>(0x2e, ProtocolVersion.v1_9, ProtocolVersion.v1_9_1, ProtocolVersion.v1_9_2, ProtocolVersion.v1_9_4, ProtocolVersion.v1_10, ProtocolVersion.v1_11, ProtocolVersion.v1_12),
            new VersionMapping<>(0x2f, ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2),
            new VersionMapping<>(0x32, ProtocolVersion.v1_13, ProtocolVersion.v1_13_1, ProtocolVersion.v1_13_2),
            new VersionMapping<>(0x35, ProtocolVersion.v1_14, ProtocolVersion.v1_14_1, ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_4, ProtocolVersion.v1_16, ProtocolVersion.v1_16_1),
            new VersionMapping<>(0x36, ProtocolVersion.v1_15, ProtocolVersion.v1_15_1, ProtocolVersion.v1_15_2, ProtocolVersion.v1_19),
            new VersionMapping<>(0x34, ProtocolVersion.v1_16_2, ProtocolVersion.v1_16_3, ProtocolVersion.v1_16_4, ProtocolVersion.v1_16_5),
            new VersionMapping<>(0x38, ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, ProtocolVersion.v1_18, ProtocolVersion.v1_18_2, ProtocolVersion.v1_19_3),
            new VersionMapping<>(0x39, ProtocolVersion.v1_19_2),
            new VersionMapping<>(0x3C, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20)
    );
    private Position position;
    @Builder.Default
    private byte flags = 0x08;
    private int teleportId;
    private boolean dismountVehicle;
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        this.position = input.readPosition();

        switch (version) {
            case v1_8:
                this.flags = input.readByte();
                break;

            case v1_19_3:
            case v1_19_2:
            case v1_19:
            case v1_18_2:
            case v1_18_1:
            case v1_18:
            case v1_17_1:
            case v1_17:
                this.flags = input.readByte();
                this.teleportId = input.readVarInt();
                this.dismountVehicle = input.readBoolean();
                break;

            case v1_16_5:
            case v1_16_4:
            case v1_16_3:
            case v1_16_2:
            case v1_16_1:
            case v1_16:
            case v1_15_2:
            case v1_15_1:
            case v1_15:
            case v1_14_4:
            case v1_14_3:
            case v1_14_2:
            case v1_14:
            case v1_14_1:
            case v1_13_2:
            case v1_13:
            case v1_13_1:
            case v1_12_2:
            case v1_12_1:
            case v1_12:
            case v1_11_2:
            case v1_11:
            case v1_10:
            case v1_9_4:
            case v1_19_4:
            case v1_20:
            case v1_9_2:
            case v1_9_1:
            case v1_9:
                this.flags = input.readByte();
                this.teleportId = input.readVarInt();
                break;
        }

    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writePosition(position);

        switch (version) {
            case v1_8:
                output.writeByte(this.flags);
                break;

            case v1_19_3:
            case v1_19_2:
            case v1_19:
            case v1_18_2:
            case v1_18_1:
            case v1_18:
            case v1_17_1:
            case v1_17:
                output.writeByte(this.flags);
                output.writeVarInt(this.teleportId);
                output.writeBoolean(this.dismountVehicle);
                break;

            case v1_16_5:
            case v1_16_4:
            case v1_16_3:
            case v1_16_2:
            case v1_16_1:
            case v1_16:
            case v1_15_2:
            case v1_15_1:
            case v1_15:
            case v1_14_4:
            case v1_14_3:
            case v1_14_2:
            case v1_14:
            case v1_14_1:
            case v1_13_2:
            case v1_13:
            case v1_13_1:
            case v1_12_2:
            case v1_12_1:
            case v1_12:
            case v1_11_2:
            case v1_11:
            case v1_10:
            case v1_9_4:
            case v1_19_4:
            case v1_9_2:
            case v1_20:
            case v1_9_1:
            case v1_9:
                output.writeByte(this.flags);
                output.writeVarInt(this.teleportId);
                break;
        }


    }


}
