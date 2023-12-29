package dev.mhpro.packetlib.objects.world.blocks.types.sculk;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class SculkSensor extends SculkState {
    private int power;
    private SculkSensorPhase phase;
    private boolean waterlogged;
}
