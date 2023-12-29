package dev.mhpro.packetlib.objects.world.enums;

public enum GameMode {
    Survival,
    Creative,
    Adventure,
    Spectator,
    Undefined;

    public static GameMode getById(int i) {
        if (i < 0) return GameMode.Undefined;
        return values()[i];
    }

    public int getId() {
        if (this == GameMode.Undefined) return -1;
        return this.ordinal();
    }
}
