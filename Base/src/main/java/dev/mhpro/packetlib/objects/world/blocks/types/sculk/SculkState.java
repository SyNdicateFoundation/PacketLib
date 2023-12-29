package dev.mhpro.packetlib.objects.world.blocks.types.sculk;

import dev.mhpro.packetlib.objects.world.blocks.types.BlockState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public abstract class SculkState extends BlockState {
}
