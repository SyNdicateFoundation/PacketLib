package dev.mhpro.packetlib.objects.world.blocks;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BlockDigMeta {
    private final boolean diggable;
    private final double hardness;
}
