package dev.mhpro.packetlib.objects.world.blocks.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NoteBlock extends BlockState {
    private NoteType instrument;
    private int note;
    private boolean powered;

    public enum NoteType {
        HARP,
        BASEDRUM,
        SNARE,
        HAT,
        BASS,
        FLUTE,
        BELL,
        GUITAR,
        CHIME, XYLOPHONE
    }
}
