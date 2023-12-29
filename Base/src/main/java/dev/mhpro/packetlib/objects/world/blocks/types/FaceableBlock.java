package dev.mhpro.packetlib.objects.world.blocks.types;

import dev.mhpro.packetlib.objects.world.blocks.types.enums.BlockFace;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class FaceableBlock extends BlockState {
    private BlockFace face;
}
