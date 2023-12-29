package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@SuperBuilder
@Getter
public class Torch extends BlockState {
    private boolean lit;
}
