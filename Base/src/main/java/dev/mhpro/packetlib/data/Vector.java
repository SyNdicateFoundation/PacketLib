package dev.mhpro.packetlib.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Vector extends iVector<Long, Vector> {
    @Setter
    private Long x, y, z;

    public Vector(Vec3d vec3d) {
        this.x = vec3d.getX().longValue();
        this.y = vec3d.getY().longValue();
        this.z = vec3d.getZ().longValue();
    }

    public static Vector empty() {
        return new Vector(0L, 0L, 0L);
    }
}
