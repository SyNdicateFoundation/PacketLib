package dev.mhpro.packetlib.objects.world.blocks.types.snowy;

import dev.mhpro.packetlib.objects.world.blocks.types.BlockState;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class PowderSnowCauldron extends BlockState {
    private int level;
}
