package dev.mhpro.packetlib.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BlockPosition {
    private int x, y, z;

    public BlockPosition(Vec3d location) {
        this.x = (int) Math.round(location.getY());
        this.y = (int) Math.round(location.getY());
        this.z = (int) Math.round(location.getZ());
    }
}
