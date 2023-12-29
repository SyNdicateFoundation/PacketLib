package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.awt.*;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Colorable extends BlockState {
    private final Color color;
}
