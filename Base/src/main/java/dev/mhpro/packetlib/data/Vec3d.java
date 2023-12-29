package dev.mhpro.packetlib.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class Vec3d extends iVector<Double, Vec3d> {
    private Double x, y, z;
}
