package dev.mhpro.packetlib.objects.world.enums;

public enum Difficulty {
    PEACEFUL, EASY, NORMAL, HARD;

    public static Difficulty getById(int i) {
        if (i < 0) return Difficulty.NORMAL;
        return values()[i];
    }

}
