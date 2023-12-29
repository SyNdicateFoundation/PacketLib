package dev.mhpro.packetlib.objects.world.blocks.types;

import dev.mhpro.packetlib.objects.world.blocks.types.enums.HalfState;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DoorBlock extends FaceableBlock {
    private HalfState halfState;
    private boolean open;
    private boolean powered;
    private boolean waterLogged;

}
