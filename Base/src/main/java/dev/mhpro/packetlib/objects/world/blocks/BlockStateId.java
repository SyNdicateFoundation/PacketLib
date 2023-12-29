package dev.mhpro.packetlib.objects.world.blocks;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.VersionMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class BlockStateId {
    private final List<VersionMapping<StateMapping>> mappings = new ArrayList<>();

    @SafeVarargs
    public static BlockStateId create(VersionMapping<StateMapping>... mappings) {
        return new BlockStateId().add(mappings);
    }

    public static BlockStateId all(StateMapping mapping) {
        return new BlockStateId().add(new VersionMapping<>(ProtocolVersion.getVersions()[1], ProtocolVersion.getVersions()[ProtocolVersion.getVersions().length - 1]));
    }

    @SafeVarargs
    public final BlockStateId add(VersionMapping<StateMapping>... mappings) {
        this.mappings.addAll(Arrays.asList(mappings));
        return this;
    }

    public StateMapping getState(ProtocolVersion version) {
        if (version.less(ProtocolVersion.v1_13)) {
            return null;
        }

        for (VersionMapping<StateMapping> mapping : mappings) {
            if (!mapping.supported(version)) continue;
            return mapping.getValue();
        }

        return null;
    }
}
