package dev.mhpro.packetlib.packets.server.play;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.mapping.VersionMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.util.ComponentColorize;
import lombok.*;
import net.kyori.adventure.text.Component;

import java.util.UUID;


@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerMessagePacket implements Packet {

    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(0x02, ProtocolVersion.v1_8),
            new VersionMapping<>(ProtocolVersion.v1_9, ProtocolVersion.v1_12_2, 0x0F),
            new VersionMapping<>(ProtocolVersion.v1_13, ProtocolVersion.v1_14_4, 0x0E),
            new VersionMapping<>(ProtocolVersion.v1_15, ProtocolVersion.v1_15_2, 0x0F),
            new VersionMapping<>(ProtocolVersion.v1_16, ProtocolVersion.v1_16_4, 0x0E),
            new VersionMapping<>(ProtocolVersion.v1_17, ProtocolVersion.v1_18_2, 0x0F),
            new VersionMapping<>(0x5F, ProtocolVersion.v1_19),
            new VersionMapping<>(0x62, ProtocolVersion.v1_19_2 ),
            new VersionMapping<>(0x60, ProtocolVersion.v1_19_3 ),
            new VersionMapping<>(0x64, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20)
    );
    private Component message;
    @Builder.Default
    private Position position = Position.SYSTEM_MESSAGE;
    @Builder.Default
    private UUID sender = new UUID(0, 0);

    public ServerMessagePacket(String message) {
        this.message = ComponentColorize.toComponent(message);
        sender = new UUID(0, 0);
        position = Position.SYSTEM_MESSAGE;
    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        if (version.moreOrEqual(ProtocolVersion.v1_13)) {
            message = input.readComponent();
        } else {
            message = input.readLegacyComponent();
        }

        if (version.moreOrEqual(ProtocolVersion.v1_19_2)) {
            position = input.readBoolean() ? Position.ACTION_BAR : Position.SYSTEM_MESSAGE;
        } else if (version.moreOrEqual(ProtocolVersion.v1_19)) {
            position = Position.values()[input.readVarInt()];
        } else if (version.moreOrEqual(ProtocolVersion.v1_8)) {
            position = Position.values()[input.readByte()];
        }

        if (version.moreOrEqual(ProtocolVersion.v1_16) && version.less(ProtocolVersion.v1_19)) {
            sender = input.readUUID();
        }
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        if (version.moreOrEqual(ProtocolVersion.v1_13)) {
            output.writeComponent(message);
        } else {
            output.writeLegacyComponent(message);
        }

        if (version.moreOrEqual(ProtocolVersion.v1_19_2)) {
            output.writeBoolean(position == Position.ACTION_BAR);
        } else if (version.moreOrEqual(ProtocolVersion.v1_19)) {
            output.writeVarInt(position.ordinal());
        } else if (version.moreOrEqual(ProtocolVersion.v1_8)) {
            output.writeByte(position.ordinal());
        }

        if (version.moreOrEqual(ProtocolVersion.v1_16) && version.less(ProtocolVersion.v1_19)) {
            output.writeUUID(sender);
        }

    }

    public enum Position {

        CHAT,
        SYSTEM_MESSAGE,
        ACTION_BAR


    }

}
