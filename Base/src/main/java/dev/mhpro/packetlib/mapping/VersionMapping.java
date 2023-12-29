package dev.mhpro.packetlib.mapping;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class VersionMapping<T> {
    public static VersionMapping<Integer> v1_9 = new VersionMapping<>(ProtocolVersion.v1_9, ProtocolVersion.v1_9_4);
    public static VersionMapping<Integer> v1_11 = new VersionMapping<>(ProtocolVersion.v1_11, ProtocolVersion.v1_11_2);
    public static VersionMapping<Integer> v1_12 = new VersionMapping<>(ProtocolVersion.v1_12, ProtocolVersion.v1_12_2);
    public static VersionMapping<Integer> v1_13 = new VersionMapping<>(ProtocolVersion.v1_13, ProtocolVersion.v1_13_2);
    public static VersionMapping<Integer> v1_14 = new VersionMapping<>(ProtocolVersion.v1_14, ProtocolVersion.v1_14_4);
    public static VersionMapping<Integer> v1_15 = new VersionMapping<>(ProtocolVersion.v1_15, ProtocolVersion.v1_15_2);
    public static VersionMapping<Integer> v1_16 = new VersionMapping<>(ProtocolVersion.v1_16, ProtocolVersion.v1_16_5);
    public static VersionMapping<Integer> v1_17 = new VersionMapping<>(ProtocolVersion.v1_17, ProtocolVersion.v1_17_1);
    public static VersionMapping<Integer> v1_18 = new VersionMapping<>(ProtocolVersion.v1_18, ProtocolVersion.v1_18_2);
    public static VersionMapping<Integer> v1_19 = new VersionMapping<>(ProtocolVersion.v1_19, ProtocolVersion.v1_19_4);
    private final List<ProtocolVersion> supported = new ArrayList<>();
    private T value;

    public VersionMapping(@NotNull ProtocolVersion from, @NotNull ProtocolVersion to) {
        this.supported.addAll(Arrays.asList(Arrays.copyOfRange(ProtocolVersion.getVersions(), from.ordinal(), to.ordinal())));
    }

    public VersionMapping(@NotNull ProtocolVersion from, @NotNull ProtocolVersion to, T value) {
        this.supported.addAll(Arrays.asList(Arrays.copyOfRange(ProtocolVersion.getVersions(), from.ordinal(), to.ordinal())));
        this.value = value;
    }

    public VersionMapping(ProtocolVersion version, T value) {
        this.supported.add(version);
        this.value = value;
    }

    public VersionMapping(T value, ProtocolVersion... versions) {
        this.supported.addAll(Arrays.asList(versions));
        this.value = value;
    }

    public VersionMapping(T value, @NotNull VersionMapping<T> mapping, ProtocolVersion... versions) {
        this.supported.addAll(mapping.getSupported());
        this.supported.addAll(Arrays.asList(versions));
        this.value = value;
    }


    @SafeVarargs
    public VersionMapping(T value, VersionMapping<T>... mappings) {
        for (VersionMapping<T> mapping : mappings) {
            this.supported.addAll(mapping.getSupported());
        }
        this.value = value;
    }

    public VersionMapping(ProtocolVersion version) {
        this.supported.add(version);
    }


    public boolean supported(@NotNull ProtocolVersion version) {
        if (ProtocolVersion.ALL == version) return true;
        return supported.contains(version);
    }


}
