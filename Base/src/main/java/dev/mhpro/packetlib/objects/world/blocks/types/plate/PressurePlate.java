package dev.mhpro.packetlib.objects.world.blocks.types.plate;

import dev.mhpro.packetlib.objects.world.blocks.types.BlockState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PressurePlate extends BlockState {
    private final boolean powered;
}
