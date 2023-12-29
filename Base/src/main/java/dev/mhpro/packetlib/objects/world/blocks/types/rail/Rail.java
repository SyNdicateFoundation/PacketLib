package dev.mhpro.packetlib.objects.world.blocks.types.rail;

import dev.mhpro.packetlib.objects.world.blocks.types.BlockState;
import dev.mhpro.packetlib.objects.world.blocks.types.enums.DirectionFace;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Rail extends BlockState {
    private DirectionFace shape;
    private boolean waterlogged;
}
