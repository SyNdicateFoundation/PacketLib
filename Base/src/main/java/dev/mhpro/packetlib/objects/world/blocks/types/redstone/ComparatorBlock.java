package dev.mhpro.packetlib.objects.world.blocks.types.redstone;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ComparatorBlock extends RedstoneBlock {
    private final ComparatorMode mode;

    public enum ComparatorMode {
        COMPARE,
        SUBTRACT
    }
}
