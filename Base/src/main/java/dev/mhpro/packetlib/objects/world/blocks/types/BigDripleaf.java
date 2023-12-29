package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BigDripleaf extends FaceableBlock {
    private TiltType tilt;
    private boolean waterLogged;

    public enum TiltType {
        NONE,
        UNSTABLE,
        PARTIAL,
        FULL
    }
}
