package dev.mhpro.packetlib.objects.world;

import com.google.gson.annotations.SerializedName;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.objects.world.enums.Difficulty;
import dev.mhpro.packetlib.objects.world.enums.DimensionType;
import dev.mhpro.packetlib.objects.world.enums.GameMode;
import dev.mhpro.packetlib.objects.world.enums.LevelType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class World {
    private DimensionRegistry registry;
    private Dimension data;
    @Builder.Default
    private WorldSettings settings = new WorldSettings();
    @Builder.Default
    private int maxPlayers = 64;
    private boolean hardcore;
    @Getter(AccessLevel.NONE)
    private long hashedSeed;

    public Dimension getData(ProtocolVersion version) {
        if (data == null) {
            return new Dimension(registry.get(version), this.settings.type);
        }
        return data;
    }

    public String getName() {
        return "minecraft:" + settings.getName().toLowerCase();
    }

    public long getHashSeed() {
        return hashedSeed <= 0 ? (long) this.hashCode() * Integer.MAX_VALUE : hashedSeed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class WorldSettings {
        @SerializedName("WorldName")
        private String name;
        @SerializedName("DimensionType")
        private DimensionType type;
        @SerializedName("LevelType")
        private LevelType levelType;
        @SerializedName("DefaultGameMode")
        private GameMode gameMode;
        @SerializedName("Difficulty")
        private Difficulty difficulty;

    }
}
