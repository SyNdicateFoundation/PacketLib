package dev.mhpro.packetlib.packets.server.play;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.mapping.VersionMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;
import net.kyori.adventure.bossbar.BossBar;

import java.util.UUID;


@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerBossbarPacket implements Packet {

    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(ProtocolVersion.v1_9, ProtocolVersion.v1_14, 0x0C),
            new VersionMapping<>(ProtocolVersion.v1_15, ProtocolVersion.v1_15_2, 0x0D),
            new VersionMapping<>(ProtocolVersion.v1_16, ProtocolVersion.v1_16_4, 0x0C),
            new VersionMapping<>(ProtocolVersion.v1_17, ProtocolVersion.v1_18_2, 0x0D),
            new VersionMapping<>(ProtocolVersion.v1_19, 0x0A),
            new VersionMapping<>(0x0B, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20)
    );
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    private BossBar bossBar;
    private int flags;
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        if (version.less(ProtocolVersion.v1_9)) return;

        uuid = input.readUUID();

        input.readVarInt();

        bossBar = BossBar.bossBar(input.readComponent(),
                bossBar.progress() / 20f,
                BossBar.Color.values()[input.readVarInt()],
                BossBar.Overlay.values()[input.readVarInt()]
        );

        flags = input.readByte();

    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        if (version.less(ProtocolVersion.v1_9)) return;

        output.writeUUID(uuid);
        output.writeVarInt(0);
        output.writeComponent(bossBar.name());
        output.writeFloat(bossBar.progress() * 20f);
        output.writeVarInt(bossBar.color().ordinal());
        output.writeVarInt(bossBar.overlay().ordinal());
        output.writeByte(flags);
    }


}
