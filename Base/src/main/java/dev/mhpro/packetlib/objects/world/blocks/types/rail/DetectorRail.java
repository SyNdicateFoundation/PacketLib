package dev.mhpro.packetlib.objects.world.blocks.types.rail;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DetectorRail extends Rail {
    private boolean detected;
}
