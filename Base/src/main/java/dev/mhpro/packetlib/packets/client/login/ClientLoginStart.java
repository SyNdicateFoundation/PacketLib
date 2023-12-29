package dev.mhpro.packetlib.packets.client.login;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;

import java.util.UUID;

@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ClientLoginStart implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0x00);
    private String username;
    @Builder.Default
    private UUID uuid = null;
    @Builder.Default
    private long timestamp = System.currentTimeMillis();
    private boolean sigData;
    private byte[] publicKeys;
    private byte[] signatures;
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeString(username);

        switch (version) {
            case v1_19:
            case v1_19_2:
                output.writeBoolean(sigData);

                if (sigData) {
                    output.writeLong(timestamp);
                    output.writeByteArray(publicKeys);
                    output.writeByteArray(signatures);
                }

                output.writeBoolean(uuid != null);

                if (uuid != null) {
                    output.writeUUID(uuid);
                }

            case v1_19_3:
            case v1_19_4:
            case v1_20:
                output.writeBoolean(uuid != null);
                if (uuid == null) return;
                output.writeUUID(uuid);
                break;
        }
    }


    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        username = input.readString();

        switch (version) {
            case v1_19:
            case v1_19_3:
            case v1_19_4:
            case v1_20:
                if (!input.readBoolean()) return;
                uuid = input.readUUID();
                break;

            case v1_19_2:
                sigData = input.readBoolean();

                if (sigData) {
                    timestamp = input.readLong();
                    publicKeys = input.readBytesArray();
                    signatures = input.readBytesArray();
                }

                if (input.readBoolean()) {
                    uuid = input.readUUID();
                }
                break;


        }

    }

}
