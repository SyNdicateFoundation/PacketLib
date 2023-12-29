package dev.mhpro.packetlib.packets.client.play;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.mapping.VersionMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.packets.RawPacket;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.UUID;

@Data
@With
@SuperBuilder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ClientChatPacket implements RawPacket {
    @Builder.Default
    @ToString.Exclude
    private final ProtocolMapping chat = new ProtocolMapping(
            new VersionMapping<>(0x01, ProtocolVersion.v1_8),
            new VersionMapping<>(0x02, ProtocolVersion.v1_9, ProtocolVersion.v1_9_1, ProtocolVersion.v1_9_2, ProtocolVersion.v1_9_4, ProtocolVersion.v1_10, ProtocolVersion.v1_11, ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2, ProtocolVersion.v1_13, ProtocolVersion.v1_13_1, ProtocolVersion.v1_13_2),
            new VersionMapping<>(0x03, ProtocolVersion.v1_12, ProtocolVersion.v1_14, ProtocolVersion.v1_14_1, ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_4, ProtocolVersion.v1_15, ProtocolVersion.v1_15_1, ProtocolVersion.v1_15_2, ProtocolVersion.v1_16, ProtocolVersion.v1_16_1, ProtocolVersion.v1_16_2, ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, ProtocolVersion.v1_18, ProtocolVersion.v1_18_2, ProtocolVersion.v1_19),
            new VersionMapping<>(0x05, ProtocolVersion.v1_19, ProtocolVersion.v1_19_2, ProtocolVersion.v1_19_3, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20)
    );
    @ToString.Exclude
    @Builder.Default
    private final ProtocolMapping command = new ProtocolMapping(
            new VersionMapping<>(0x04, ProtocolVersion.v1_19, ProtocolVersion.v1_19_2, ProtocolVersion.v1_19_3, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20),
            new VersionMapping<>(0x06, ProtocolVersion.v1_19_2)
    );

    private long timestamp;
    private long salt;
    private boolean signedPreview;
    private int messages;
    private String message;
    @Builder.Default
    private SeenMessage[] seenMessages = new SeenMessage[0];
    @Builder.Default
    private byte[] signatures = new byte[0];
    @Builder.Default
    private ArgSignatures[] argSignatures = null;
    @Builder.Default
    private BitSet acknowledged = null;
    @Builder.Default
    private @Nullable SeenMessage lastMessage = null;


    private boolean isCommand(ProtocolVersion version, int packetId) {
        return command.get(version) == packetId;
    }
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        boolean iscommand = isCommand(version, packetId);
        this.message = input.readString(256);

        switch (version) {
            case v1_19:
                this.timestamp = input.readLong();
                this.salt = input.readLong();
                this.signatures = input.readBytesArray();
                this.signedPreview = input.readBoolean();
                break;

            case v1_19_2:
                this.timestamp = input.readLong();
                this.salt = input.readLong();
                this.signatures = input.readBytesArray();
                this.signedPreview = input.readBoolean();
                this.seenMessages = new SeenMessage[input.readVarInt()];

                for (int i = 0; i < this.seenMessages.length; i++) {
                    this.seenMessages[i] = new SeenMessage(input.readUUID(), input.readBytesArray());
                }

                if (input.readBoolean()) {
                    this.lastMessage = new SeenMessage(input.readUUID(), input.readBytesArray());
                }

                break;

            case v1_19_3:
            case v1_19_4:
            case v1_20:
                this.timestamp = input.readLong();
                this.salt = input.readLong();

                if (iscommand) {
                    int count = Math.min(input.readVarInt(), 8);

                    argSignatures = new ArgSignatures[count];

                    for (int i = 0; i < count; i++) {
                        byte[] bytes = new byte[256];

                        argSignatures[i] = ArgSignatures.builder()
                                .arg(input.readString(16))
                                .signatures(bytes)
                                .build();

                        input.readBytes(bytes);
                    }

                } else {
                    if (input.readBoolean()) {
                        byte[] bytes = new byte[256];
                        input.readBytes(bytes);
                        this.signatures = bytes;
                    }
                }

                this.messages = input.readVarInt();
                this.acknowledged = input.readBitSet(20);
                break;
        }

    }

    @Override
    public void writeRaw(PacketBuffer output, ProtocolVersion version) {
        if (this.argSignatures != null) {
            output.writeVarInt(command.get(version));
            output.writeString(this.message);
        } else {
            output.writeVarInt(chat.get(version));
            output.writeString(this.message);
        }

        switch (version) {
            case v1_19:
                output.writeLong(this.timestamp);
                output.writeLong(this.salt);
                output.writeByteArray(this.signatures);
                output.writeBoolean(this.signedPreview);
                break;

            case v1_19_2:
                output.writeLong(this.timestamp);
                output.writeLong(this.salt);
                output.writeByteArray(this.signatures);
                output.writeBoolean(this.signedPreview);

                output.writeVarInt(this.seenMessages.length);

                for (SeenMessage seenMessage : this.seenMessages) {
                    output.writeUUID(seenMessage.getUser());
                    output.writeByteArray(seenMessage.getSignature());
                }

                output.writeBoolean(this.lastMessage != null);

                if (this.lastMessage != null) {
                    output.writeUUID(this.lastMessage.getUser());
                    output.writeByteArray(this.lastMessage.getSignature());
                }

                break;

            case v1_19_3:
            case v1_19_4:
            case v1_20:
                output.writeLong(this.timestamp);
                output.writeLong(this.salt);

                output.writeBoolean(this.signatures.length >= 256);

                if (this.argSignatures != null) {
                    for (ArgSignatures argSignature : this.argSignatures) {
                        output.writeString(argSignature.arg);
                        output.writeBytes(argSignature.signatures);
                    }

                } else {
                    if (this.signatures.length >= 256) {
                        output.writeBytes(this.signatures);
                    }
                }

                output.writeVarInt(this.messages);

                if (this.acknowledged != null) {
                    output.writeFixedBitSet(this.acknowledged, 20);
                }

                break;
        }

    }

    @Override
    public boolean isSupport(ProtocolVersion version, int packetId) {
        return command.get(version) == packetId ||
                chat.get(version) == packetId;
    }

    @Data
    public static class SeenMessage {
        private final UUID user;
        private final byte[] signature;
    }

    @Data
    @With
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ArgSignatures {
        @Builder.Default
        private byte[] signatures = new byte[0];
        private String arg;
    }
}
