package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CampfireBlock extends FaceableBlock {
    private boolean lit;
    private boolean signal_fire;
    private boolean waterlogged;
}
