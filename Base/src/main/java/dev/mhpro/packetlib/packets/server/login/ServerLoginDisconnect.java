package dev.mhpro.packetlib.packets.server.login;

import com.google.gson.JsonSyntaxException;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerLoginDisconnect implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0x0);
    private Component reason;

    public void setReason(String reason) {
        this.reason = GsonComponentSerializer.gson().deserialize(reason);

    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeComponent(reason);

    }

    @Override
    @SneakyThrows
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        try {
            this.reason = input.readComponent();
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

}
