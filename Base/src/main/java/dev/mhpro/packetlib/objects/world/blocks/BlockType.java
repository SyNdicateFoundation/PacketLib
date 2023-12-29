package dev.mhpro.packetlib.objects.world.blocks;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.objects.world.blocks.types.BlockState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
public class BlockType {
    private final int legacyId;
    private final int stackSize;
    private final BlockDigMeta digMeta;
    private final BlockLightMeta lightMeta;
    private final List<BlockType> drops = new ArrayList<>();
    @Setter(AccessLevel.PACKAGE)
    private boolean legacy;
    @Setter(AccessLevel.PACKAGE)
    private BlockStateId stateId;
    private Class<? extends BlockState> stateClass;
    private String display, key;

    public StateMapping getState(ProtocolVersion protocolVersion) {
        StateMapping state = this.getStateId().getState(protocolVersion);
        return state == null ? StateMapping.all(this.legacyId) : state;
    }
}
