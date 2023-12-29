package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class ChiseledBookhalf extends FaceableBlock {
    private boolean slot_0_occupied;
    private boolean slot_1_occupied;
    private boolean slot_2_occupied;
    private boolean slot_3_occupied;
    private boolean slot_4_occupied;
    private boolean slot_5_occupied;
}
