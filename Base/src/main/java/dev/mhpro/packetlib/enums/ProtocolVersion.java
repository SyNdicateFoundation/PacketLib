package dev.mhpro.packetlib.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public enum ProtocolVersion {
    ALL(0, null),
    v1_8(47, ProtocolVersion.ALL),
    v1_9(107, ProtocolVersion.ALL),
    v1_9_1(108, ProtocolVersion.v1_9),
    v1_9_2(109, ProtocolVersion.v1_9),
    v1_9_4(110, ProtocolVersion.v1_9),
    v1_10(210, ProtocolVersion.ALL),
    v1_11(315, ProtocolVersion.ALL),
    v1_11_1(316, ProtocolVersion.v1_11),
    v1_11_2(315, ProtocolVersion.v1_11),
    v1_12(335, ProtocolVersion.ALL),
    v1_12_1(338, ProtocolVersion.v1_12),
    v1_12_2(340, ProtocolVersion.v1_12),
    v1_13(393, ProtocolVersion.ALL),
    v1_13_1(401, ProtocolVersion.v1_13),
    v1_13_2(404, ProtocolVersion.v1_13),
    v1_14(477, ProtocolVersion.ALL),
    v1_14_1(480, ProtocolVersion.v1_14),
    v1_14_2(485, ProtocolVersion.v1_14),
    v1_14_3(490, ProtocolVersion.v1_14),
    v1_14_4(498, ProtocolVersion.v1_14),
    v1_15(573, ProtocolVersion.ALL),
    v1_15_1(575, ProtocolVersion.v1_15),
    v1_15_2(578, ProtocolVersion.v1_15),
    v1_16(735, ProtocolVersion.ALL),
    v1_16_1(736, ProtocolVersion.v1_16),
    v1_16_2(751, ProtocolVersion.v1_16),
    v1_16_3(753, ProtocolVersion.v1_16),
    v1_16_4(754, ProtocolVersion.v1_16),
    v1_16_5(754, ProtocolVersion.v1_16),
    v1_17(755, ProtocolVersion.ALL),
    v1_17_1(756, ProtocolVersion.v1_17),
    v1_18(757, ProtocolVersion.ALL),
    v1_18_1(757, ProtocolVersion.v1_18),
    v1_18_2(758, ProtocolVersion.v1_18),
    v1_19(759, ProtocolVersion.ALL),
    v1_19_2(760, ProtocolVersion.v1_9),
    v1_19_3(761, ProtocolVersion.v1_19),
    v1_19_4(762, ProtocolVersion.v1_19),
    v1_20(763, ProtocolVersion.ALL),
    ;
    private static ProtocolVersion[] versions;
    private final int protocol;
    private final ProtocolVersion major;

    public static @Nullable ProtocolVersion fromProtocol(int protocolVersion) {
        for (ProtocolVersion value : ProtocolVersion.getVersions()) {
            if (value.protocol != protocolVersion) continue;
            return value;
        }
        return null;
    }

    public static ProtocolVersion fromString(String key) {
        String replace = key.replace('.', '_');
        return ProtocolVersion.valueOf(replace.startsWith("v") ? replace.toLowerCase() : "v" + replace);
    }

    public static ProtocolVersion[] getVersions() {
        if (versions == null) {
            versions = ProtocolVersion.values();
        }
        return versions;
    }

    public String getName() {
        return this.name().contains("_") ?
                this.name().replace("_", ".").substring(1)
                : this.name();
    }

    @Override
    public String toString() {
        return String.format("Version(protocol=%d, name=%s, major=%s)", protocol, getName(), major != null ? major.getName() : null);
    }

    public boolean moreOrEqual(ProtocolVersion version) {
        return version.ordinal() <= this.ordinal();

    }

    public boolean lessOrEqual(ProtocolVersion version) {
        return version.ordinal() >= this.ordinal();
    }

    public boolean less(ProtocolVersion version) {
        return version.ordinal() > this.ordinal();
    }
}
