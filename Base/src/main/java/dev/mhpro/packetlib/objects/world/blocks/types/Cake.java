package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Cake extends BlockState {
    private int bites;
}
