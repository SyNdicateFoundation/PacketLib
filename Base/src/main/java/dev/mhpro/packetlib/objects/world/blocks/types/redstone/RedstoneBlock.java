package dev.mhpro.packetlib.objects.world.blocks.types.redstone;

import dev.mhpro.packetlib.objects.world.blocks.types.BlockState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class RedstoneBlock extends BlockState {
    private final boolean powered;
    private final int delay;
}
