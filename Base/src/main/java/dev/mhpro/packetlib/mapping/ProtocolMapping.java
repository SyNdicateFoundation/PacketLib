package dev.mhpro.packetlib.mapping;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import lombok.Data;
import lombok.Synchronized;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class ProtocolMapping {
    private final List<VersionMapping<Integer>> mappingList = Collections.synchronizedList(new ArrayList<>());

    @SafeVarargs
    public ProtocolMapping(VersionMapping<Integer>... mappings) {
        this.add(mappings);
    }

    public static ProtocolMapping all(int packetId) {
        return new ProtocolMapping(
                new VersionMapping<>(ProtocolVersion.getVersions()[0], ProtocolVersion.getVersions()[ProtocolVersion.getVersions().length - 1], packetId)
        );
    }

    @SafeVarargs
    @Synchronized("mappingList")
    public final void add(VersionMapping<Integer>... mappings) {
        Collections.addAll(mappingList, mappings);
    }

    @SafeVarargs
    @Synchronized("mappingList")
    public final void remove(VersionMapping<Integer>... mappings) {
        for (VersionMapping<Integer> mapping : mappings) {
            mappingList.remove(mapping);
        }
    }

    @Synchronized("mappingList")
    public Integer get(ProtocolVersion version) {
        for (VersionMapping<Integer> mapping : mappingList) {
            if (!mapping.supported(version)) continue;
            return mapping.getValue();
        }

        return mappingList.get(mappingList.size() - 1).getValue();
    }
}
