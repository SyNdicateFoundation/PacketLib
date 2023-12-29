package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
public class EndPortal extends FaceableBlock {
    private boolean eye;
}
