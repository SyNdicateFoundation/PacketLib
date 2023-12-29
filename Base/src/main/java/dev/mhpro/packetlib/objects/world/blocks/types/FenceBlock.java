package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class FenceBlock extends BlockState {
    private boolean east;
    private boolean north;
    private boolean south;
    private boolean waterlogged;
    private boolean west;

}
