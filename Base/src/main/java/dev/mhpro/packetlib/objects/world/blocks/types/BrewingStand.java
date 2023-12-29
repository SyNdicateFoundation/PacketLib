package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class BrewingStand extends BlockState {
    private boolean bottle_1, bottle_2, bottle_3;
}
