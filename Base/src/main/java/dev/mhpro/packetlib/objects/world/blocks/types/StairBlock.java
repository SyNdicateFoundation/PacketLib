package dev.mhpro.packetlib.objects.world.blocks.types;

import dev.mhpro.packetlib.objects.world.blocks.types.enums.DirectionFace;
import dev.mhpro.packetlib.objects.world.blocks.types.enums.HalfState;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class StairBlock extends FaceableBlock {
    private HalfState halfState;
    private DirectionFace directionFace;
    private boolean waterLogged;
}
