package dev.mhpro.packetlib.objects.world.blocks;

import lombok.Data;

@Data
public class BlockLightMeta {
    private final int filterLight;
    private final int emitLight;
}
