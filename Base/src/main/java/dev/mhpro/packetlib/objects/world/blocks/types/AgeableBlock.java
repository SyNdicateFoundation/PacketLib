package dev.mhpro.packetlib.objects.world.blocks.types;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class AgeableBlock extends BlockState {
    private int age;
}
