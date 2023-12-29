package dev.mhpro.packetlib.objects.world.blocks.types.redstone;

import dev.mhpro.packetlib.objects.world.blocks.types.FaceableBlock;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class RepeaterBlock extends FaceableBlock {
    private int delay;
    private boolean locked;
    private boolean powered;


}
