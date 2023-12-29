package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class JigSawBlock extends BlockState {
    private JigsawFace facing;

    public enum JigsawFace {
        NORTH,
        SOUTH,
        WEST,
        EAST,
        UP,
        DOWN

    }
}
