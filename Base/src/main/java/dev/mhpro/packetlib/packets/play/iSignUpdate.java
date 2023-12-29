package dev.mhpro.packetlib.packets.play;

import dev.mhpro.packetlib.data.BlockPosition;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.util.ComponentColorize;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public abstract class iSignUpdate implements Packet {
    @Builder.Default
    private String[] lines = new String[4];
    private BlockPosition position;

    public void setLine(int line, String message) {
        if (this.lines.length > 4) {
            throw new IllegalArgumentException("A Sign can only have 4 args");
        }

        if (message.length() > 100) {
            throw new IllegalArgumentException("The maximum size of text in a sign 100");
        }

        this.lines[line] = message;
    }

    public void setLines(String... lines) {
        for (int i = 0; i < lines.length; i++) {
            this.setLine(i, lines[i]);
        }
    }

    public void setLines(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            this.setLine(i, lines.get(i));
        }
    }
    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        this.setPosition(input.readBlockPosition(version));

        for (int i = 0; i < 4; i++) {
            if (version.lessOrEqual(ProtocolVersion.v1_8)) {
                this.getLines()[i] = input.readComponent().insertion();
                continue;
            }

            this.getLines()[i] = input.readString();
        }
    }


    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeBlockPosition(this.getPosition(), version);

        for (String line : this.getLines()) {

            if (version.lessOrEqual(ProtocolVersion.v1_8)) {
                output.writeComponent(ComponentColorize.toComponent(line));
                continue;
            }

            output.writeString(line);
        }
    }
}
