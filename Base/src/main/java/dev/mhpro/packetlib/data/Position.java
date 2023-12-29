package dev.mhpro.packetlib.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class Position extends Vec3d {

    private final float yaw, pitch;

    public Position(double x, double y, double z) {
        super(x, y, z);
        this.yaw = Float.NaN;
        this.pitch = Float.NaN;
    }

    public Position(double x, double y, double z, float yaw, float pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(Vector vector) {
        super(((double) vector.getX()), ((double) vector.getY()), ((double) vector.getZ()));
        this.yaw = 0;
        this.pitch = 0;
    }

    public static Position empty() {
        return new Position(0, 0, 0, 0, 0);
    }

    public Position add(double x, double y, double z) {
        this.setX(x + this.getX());
        this.setY(y + this.getY());
        this.setZ(z + this.getZ());
        return this;
    }
}
