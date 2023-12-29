package dev.mhpro.packetlib.objects.world.blocks.types.snowy;

import dev.mhpro.packetlib.objects.world.blocks.types.BlockState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SnowyBlock extends BlockState {
    private boolean snowy;
}
