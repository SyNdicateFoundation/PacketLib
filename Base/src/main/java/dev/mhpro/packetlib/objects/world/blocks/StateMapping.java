package dev.mhpro.packetlib.objects.world.blocks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
public class StateMapping {
    private int min, Default, max;

    public static StateMapping all(int i) {
        return new StateMapping(i, i, i);
    }

    public boolean is(int i) {
        return Default == i || (i > min && i < max);
    }
}
