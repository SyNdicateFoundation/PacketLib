package dev.mhpro.packetlib.objects.world.blocks.types.sculk;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class SculkShrieker extends SculkState {
    private boolean can_summon;
    private boolean sharieking;
    private boolean waterLogged;
}
