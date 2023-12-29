package dev.mhpro.packetlib.objects.world;

import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.objects.world.enums.DimensionType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;

@Data
@RequiredArgsConstructor
public class Dimension {
    private final int id;
    private final String name;
    private final CompoundBinaryTag entries;


    public Dimension(CompoundBinaryTag tag, DimensionType type) {
        this.entries = tag;

        switch (type) {
            case OVERWORLD:
                this.id = 0;
                this.name = type.toString();
                break;

            case THE_END:
                this.id = -1;
                this.name = type.toString();
                break;

            case THE_NETHER:
                this.id = 1;
                this.name = type.toString();
                break;

            default:
                this.id = 1;
                this.name = DimensionType.THE_END.toString();
        }

    }

    public Dimension(DimensionRegistry registry, ProtocolVersion version, DimensionType type) {
        ListBinaryTag dimensions = registry.get(version).getCompound("minecraft:dimension_type").getList("value");

        CompoundBinaryTag overWorld = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(0)).get("element");
        CompoundBinaryTag nether = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(2)).get("element");
        CompoundBinaryTag theEnd = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(3)).get("element");

        switch (type) {
            case OVERWORLD:
                this.id = 0;
                this.entries = overWorld;
                this.name = type.toString();
                break;

            case THE_END:
                this.id = -1;
                this.entries = nether;
                this.name = type.toString();
                break;

            case THE_NETHER:
                this.id = 1;
                this.entries = theEnd;
                this.name = type.toString();
                break;

            default:
                this.id = 1;
                this.entries = theEnd;
                this.name = DimensionType.THE_END.toString();

        }

    }

    public Dimension(CompoundBinaryTag tag) {
        this.entries = tag;
        System.out.println(tag);
        this.id = 0;
        this.name = "dasd";

    }
}
