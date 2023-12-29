package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Vines {
    private boolean down;
    private boolean east;
    private boolean north;
    private boolean south;
    private boolean up;
    private boolean waterlogged;
    private boolean west;

}
