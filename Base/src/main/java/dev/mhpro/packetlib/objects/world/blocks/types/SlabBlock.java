package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class SlabBlock extends BlockState {
    private boolean waterlogged;
    private SlabType slabType;

    public enum SlabType {
        TOP, BOTTOM, DOUBLE
    }
}
