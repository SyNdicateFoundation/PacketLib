package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class Farmland extends BlockState {
    private int moisture;
}
