package dev.mhpro.packetlib.packets.client.handshake;

import dev.mhpro.packetlib.enums.HandshakeState;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;

@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ClientHandshake implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0x00);
    private ProtocolVersion version;
    private String address;
    private short port;
    private HandshakeState state;
    private int protocolInt;

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeVarInt(this.protocolInt);
        output.writeString(address);
        output.writeShort(port);
        output.writeVarInt((short) state.ordinal() + 1);
    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }


    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        this.protocolInt = input.readVarInt();
        this.version = ProtocolVersion.fromProtocol(protocolInt);
        this.address = input.readString();
        this.port = input.readShort();
        this.state = HandshakeState.values()[input.readVarInt()];
    }

}
