package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BambooBlock extends AgeableBlock {

    private Leaves leaves;
    private int stage;

    public enum Leaves {
        NONE,
        SMALL,
        LARGE
    }
}
