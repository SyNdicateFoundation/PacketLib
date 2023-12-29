package dev.mhpro.packetlib.packets.server.login;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerLoginSuccess implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0x02);
    private String username;
    private UUID uuid;
    @Builder.Default
    private Property[] properties = new Property[0];
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        switch (version) {
            case v1_11_1:
            case v1_11_2:
            case v1_14_2:
            case v1_13_2:
            case v1_9_2:
            case v1_14:
            case v1_9_4:
            case v1_10:
            case v1_12_2:
            case v1_12_1:
            case v1_14_4:
            case v1_13:
            case v1_8:
            case v1_12:
            case v1_14_1:
            case v1_13_1:
            case v1_15_1:
            case v1_15:
            case v1_11:
            case v1_9_1:
            case v1_9:
            case v1_15_2:
            case v1_14_3:
                this.uuid = UUID.fromString(input.readString());
                this.username = input.readString();
                break;

            case v1_16_3:
            case v1_16_4:
            case v1_16_5:
            case v1_18_2:
            case v1_18:
            case v1_16_2:
            case v1_17:
            case v1_16_1:
            case v1_16:
            case v1_17_1:
                this.uuid = input.readUUID();
                this.username = input.readString();
                break;

            case v1_19_2:
            case v1_19:
            case v1_19_3:
            case v1_19_4:
            case v1_20:
                this.uuid = input.readUUID();
                this.username = input.readString();

                this.properties = new Property[input.readVarInt()];
                for (int i = 0; i < this.properties.length; i++) {
                    this.properties[i] = new Property(input.readString(), input.readString(), input.readBoolean() ? input.readString() : null);
                }

                break;
            case v1_18_1:
                break;
        }
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        switch (version) {
            case v1_13_2:
            case v1_9_2:
            case v1_14:
            case v1_9_4:
            case v1_10:
            case v1_12_2:
            case v1_12_1:
            case v1_14_4:
            case v1_13:
            case v1_8:
            case v1_12:
            case v1_14_1:
            case v1_13_1:
            case v1_15_1:
            case v1_15:
            case v1_11:
            case v1_9_1:
            case v1_9:
            case v1_15_2:
            case v1_14_3:
                output.writeString(this.uuid.toString());
                output.writeString(this.username);
                break;

            case v1_18_2:
            case v1_18:
            case v1_16_2:
            case v1_17:
            case v1_16_1:
            case v1_16:
            case v1_17_1:
                output.writeUUID(this.uuid);
                output.writeString(this.username);
                break;

            case v1_19_2:
            case v1_19:
            case v1_19_3:
            case v1_19_4:
            case v1_20:
                output.writeUUID(this.uuid);
                output.writeString(this.username);

                output.writeVarInt(this.properties.length);
                for (Property s : this.properties) {
                    output.writeString(s.name);
                    output.writeString(s.value);
                    output.writeBoolean(s.signature != null);
                    if (s.signature != null) {
                        output.writeString(s.signature);
                    }
                }

                break;
        }
    }


    @Data
    @AllArgsConstructor
    public static class Property {
        private String name;
        private String value;
        private @Nullable String signature;
    }
}
