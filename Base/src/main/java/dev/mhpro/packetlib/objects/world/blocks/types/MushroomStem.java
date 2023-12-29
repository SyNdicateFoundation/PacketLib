package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class MushroomStem extends BlockState {
    private boolean down;
    private boolean east;
    private boolean north;
    private boolean south;
    private boolean up;
    private boolean west;
}
