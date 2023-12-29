package dev.mhpro.packetlib.objects.world.enums;

public enum DimensionType {
    OVERWORLD,
    THE_END,
    THE_NETHER;

    public static DimensionType getById(int i) {
        if (i < 0) return DimensionType.THE_NETHER;
        return values()[i];
    }

    public static DimensionType getByName(String s) {
        for (DimensionType value : values()) {
            if (s.toLowerCase().contains(value.name().toLowerCase())) return value;
        }
        return null;
    }

    public int getId() {
        if (this == DimensionType.THE_NETHER) return -1;
        return this.ordinal();
    }

    @Override
    public String toString() {
        return "minecraft:" + this.name().toLowerCase();
    }

}
