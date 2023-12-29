package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Tripwire extends BlockState {
    private boolean attached;
    private boolean disarmed;
    private boolean east;
    private boolean north;
    private boolean powered;
    private boolean south;
    private boolean west;
}
