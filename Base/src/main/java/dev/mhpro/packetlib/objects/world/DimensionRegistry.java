package dev.mhpro.packetlib.objects.world;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.VersionMapping;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DimensionRegistry {

    @ToString.Exclude
    private List<VersionMapping<CompoundBinaryTag>> codec = Collections.synchronizedList(new ArrayList<>());


    public DimensionRegistry(ProtocolVersion version, CompoundBinaryTag tag) {
        codec.add(new VersionMapping<>(version, tag));
    }

    public DimensionRegistry() {
        codec.addAll(Arrays.asList(
                new VersionMapping<>(readCodec("codec_1_16.snbt"), ProtocolVersion.v1_16, ProtocolVersion.v1_16_1),
                new VersionMapping<>(readCodec("codec_1_16_2.snbt"), ProtocolVersion.v1_16_2, ProtocolVersion.v1_16_3, ProtocolVersion.v1_16_4, ProtocolVersion.v1_16_5, ProtocolVersion.v1_17, ProtocolVersion.v1_17_1, ProtocolVersion.v1_18, ProtocolVersion.v1_18_1),
                new VersionMapping<>(readCodec("codec_1_18_2.snbt"), ProtocolVersion.v1_18_2),
                new VersionMapping<>(readCodec("codec_1_19.snbt"), ProtocolVersion.v1_19),
                new VersionMapping<>(readCodec("codec_1_19_1.snbt"), ProtocolVersion.v1_19_2, ProtocolVersion.v1_19_3, ProtocolVersion.v1_19_4)
        ));
    }


    public CompoundBinaryTag get(ProtocolVersion version) {
        for (VersionMapping<CompoundBinaryTag> mapping : codec) {
            if (!mapping.supported(version)) {
                continue;
            }
            return mapping.getValue();
        }

        return null;
    }


    @SneakyThrows
    private CompoundBinaryTag readCodec(String file) {
        @Cleanup InputStream in = this.getClass().getClassLoader().getResourceAsStream("dimension_codec/" + file);

        if (in == null) throw new Exception(String.format("Error %s not found!", file));

        return TagStringIO.get().asCompound(join(in));
    }

    @SneakyThrows
    private String join(InputStream in) {
        @Cleanup InputStreamReader streamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        @Cleanup BufferedReader bufferedReader = new BufferedReader(streamReader);
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }
}
