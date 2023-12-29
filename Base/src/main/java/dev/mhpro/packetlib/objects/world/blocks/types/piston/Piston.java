package dev.mhpro.packetlib.objects.world.blocks.types.piston;

import dev.mhpro.packetlib.objects.world.blocks.types.FaceableBlock;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Piston extends FaceableBlock {
    private final boolean extended;
}
