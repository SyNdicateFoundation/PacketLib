package dev.mhpro.packetlib.objects.world.blocks.types.redstone;

import dev.mhpro.packetlib.objects.world.blocks.types.FaceableBlock;
import dev.mhpro.packetlib.objects.world.blocks.types.enums.MathBlock;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class LeverBlock extends FaceableBlock {
    private MathBlock mathBlock;
    private boolean powered;
}
