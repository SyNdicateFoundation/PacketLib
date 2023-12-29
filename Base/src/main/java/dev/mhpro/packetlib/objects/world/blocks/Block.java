package dev.mhpro.packetlib.objects.world.blocks;

import dev.mhpro.packetlib.data.BlockPosition;
import dev.mhpro.packetlib.objects.world.blocks.types.BlockState;
import dev.mhpro.packetlib.objects.world.chunk.Chunk;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Block {
    private final Chunk chunk;
    private BlockType type;
    private BlockState state;
    private BlockPosition position;
}
