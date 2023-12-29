package dev.mhpro.packetlib.objects.world.blocks.types.piston;

import dev.mhpro.packetlib.objects.world.blocks.types.enums.Sticky;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class MovingPiston extends Piston {
    private final Sticky sticky;
    private boolean Short;
}
