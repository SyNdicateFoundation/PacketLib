package dev.mhpro.packetlib.packets.server.play;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.mapping.VersionMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import lombok.*;


@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerPluginMessage implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(ProtocolVersion.v1_8, ProtocolVersion.v1_12_2, 0x3F),
            new VersionMapping<>(ProtocolVersion.v1_13, ProtocolVersion.v1_13_2, 0x19),
            new VersionMapping<>(ProtocolVersion.v1_14, ProtocolVersion.v1_14_4, 0x18),
            new VersionMapping<>(ProtocolVersion.v1_15, ProtocolVersion.v1_15_2, 0x19),
            new VersionMapping<>(ProtocolVersion.v1_16, ProtocolVersion.v1_16_1, 0x18),
            new VersionMapping<>(ProtocolVersion.v1_16_2, ProtocolVersion.v1_16_4, 0x17),
            new VersionMapping<>(ProtocolVersion.v1_19_4, ProtocolVersion.v1_20, 0x17),
            new VersionMapping<>(ProtocolVersion.v1_17, ProtocolVersion.v1_18_2, 0x18),
            new VersionMapping<>(0x15, ProtocolVersion.v1_19),
            new VersionMapping<>(0x16, ProtocolVersion.v1_19_2),
            new VersionMapping<>(ProtocolVersion.v1_19_3, 0x15)
    );
    private String channel;
    private ByteBuf message;
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        channel = input.readString();
        message = input.readBytes(input.readVarInt());
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeString(channel);
        output.writeVarInt(message.readableBytes());
        output.writeBytes(message);
    }


}
