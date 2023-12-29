package dev.mhpro.packetlib.objects.world.blocks.types.sculk;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class SculkCatalyst extends SculkState {
    private boolean bloom;
}
