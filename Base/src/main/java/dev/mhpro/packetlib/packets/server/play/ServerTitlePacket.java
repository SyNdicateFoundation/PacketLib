package dev.mhpro.packetlib.packets.server.play;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.mapping.VersionMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.packets.RawPacket;
import lombok.*;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;


@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
public class ServerTitlePacket implements RawPacket {
    private static final ProtocolMapping set_title = new ProtocolMapping(
            // Packet Set Title
            new VersionMapping<>(ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, 0x59),
            new VersionMapping<>(ProtocolVersion.v1_18, ProtocolVersion.v1_19, 0x5A),
            new VersionMapping<>(0x5D, ProtocolVersion.v1_19_2),
            new VersionMapping<>(ProtocolVersion.v1_19_3, 0x5B),
            new VersionMapping<>(ProtocolVersion.v1_19_4, ProtocolVersion.v1_20, 0x5F)
    );
    private static final ProtocolMapping set_subtitle = new ProtocolMapping(
            // Packet Set Sub Title
            new VersionMapping<>(ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, 0x57),
            new VersionMapping<>(ProtocolVersion.v1_18, ProtocolVersion.v1_19, 0x58),
            new VersionMapping<>(0x5B, ProtocolVersion.v1_19_2),
            new VersionMapping<>(ProtocolVersion.v1_19_3, 0x59),
            new VersionMapping<>(ProtocolVersion.v1_19_4, ProtocolVersion.v1_20, 0x5D)
    );
    private static final ProtocolMapping set_times = new ProtocolMapping(
            // Packet Set Title Times
            new VersionMapping<>(ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, 0x5A),
            new VersionMapping<>(ProtocolVersion.v1_18, ProtocolVersion.v1_19, 0x5B),
            new VersionMapping<>(0x5E, ProtocolVersion.v1_19_2),
            new VersionMapping<>(ProtocolVersion.v1_19_3, 0x5C),
            new VersionMapping<>(ProtocolVersion.v1_19_4, ProtocolVersion.v1_20, 0x60)
    );

    private static final ProtocolMapping legacy = new ProtocolMapping(
            // Packet Legacy Title
            new VersionMapping<>(ProtocolVersion.v1_8, ProtocolVersion.v1_11_2, 0x45),
            new VersionMapping<>(0x47, ProtocolVersion.v1_12),
            new VersionMapping<>(ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2, 0x48),
            new VersionMapping<>(ProtocolVersion.v1_13, ProtocolVersion.v1_13_2, 0x4B),
            new VersionMapping<>(ProtocolVersion.v1_14, ProtocolVersion.v1_14_4, 0x4F),
            new VersionMapping<>(ProtocolVersion.v1_15, ProtocolVersion.v1_15_2, 0x50),
            new VersionMapping<>(ProtocolVersion.v1_16, ProtocolVersion.v1_16_4, 0x4F)
    );

    @ToString.Exclude
    private TitleTimes times = new TitleTimes(0, 15, 0);

    @Builder.Default
    private Action action = Action.SET_TITLE;
    private @NotNull Component message;

    public ServerTitlePacket() {

    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        // Packet ID
        if (version.less(ProtocolVersion.v1_17)) {
            action = Action.getById(input.readVarInt(), version);
        } else {
            if (packetId == set_title.get(version)) {
                action = Action.SET_TITLE;
            } else if (packetId == set_subtitle.get(version)) {
                action = Action.SET_TITLE;
            } else if (packetId == set_times.get(version)) {
                action = Action.SET_TIMES_AND_DISPLAY;
            }
        }

        // Data
        switch (action) {
            case SET_TITLE:
            case SET_SUBTITLE:
                message = input.readComponent();
                break;

            case SET_TIMES_AND_DISPLAY:
                times = TitleTimes.builder()
                        .fadeIn(input.readVarInt())
                        .stay(input.readVarInt())
                        .fadeOut(input.readVarInt())
                        .build();

                break;
        }
    }

    @Override
    public void writeRaw(PacketBuffer output, ProtocolVersion version) {
        // Packet ID
        if (version.less(ProtocolVersion.v1_17)) {
            output.writeVarInt(legacy.get(version));
            output.writeVarInt(action.getId(version));
        } else {
            if (action == Action.SET_TITLE) {
                output.writeVarInt(set_title.get(version));
            } else if (action == Action.SET_SUBTITLE) {
                output.writeVarInt(set_subtitle.get(version));
            } else if (action == Action.SET_TIMES_AND_DISPLAY) {
                output.writeVarInt(set_times.get(version));
            }
        }

        // Data
        switch (action) {
            case SET_TITLE:
            case SET_SUBTITLE:
                output.writeComponent(message);
                break;

            case SET_TIMES_AND_DISPLAY:
                output.writeInt(times.getFadeIn());
                output.writeInt(times.getStay());
                output.writeInt(times.getFadeOut());
                break;
        }
    }

    @Override
    public boolean isSupport(ProtocolVersion version, int packetId) {
        return set_title.get(version) == packetId ||
                set_subtitle.get(version) == packetId ||
                set_times.get(version) == packetId ||
                legacy.get(version) == packetId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public enum Action {
        SET_TITLE,
        SET_SUBTITLE,
        SET_TIMES_AND_DISPLAY(3);

        private int id;

        public static Action getById(int i, ProtocolVersion version) {
            for (Action value : Action.values()) {
                if (value.getId(version) != i) continue;
                return value;
            }

            return Action.SET_TITLE;
        }

        public int getId(ProtocolVersion version) {
            return version.less(ProtocolVersion.v1_11) ? this.ordinal() : id > 0 ? id : ordinal();
        }
    }

    @AllArgsConstructor
    @Data
    @Builder
    public static class TitleTimes {
        @Builder.Default
        private int fadeIn = 0, stay = 20, fadeOut = 0;
    }
}
