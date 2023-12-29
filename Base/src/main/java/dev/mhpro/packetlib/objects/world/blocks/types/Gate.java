package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Gate extends FaceableBlock {
    private boolean powered;
    private boolean open;
    private boolean in_wall;
}
