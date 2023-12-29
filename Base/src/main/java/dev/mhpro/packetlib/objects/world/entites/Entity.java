package dev.mhpro.packetlib.objects.world.entites;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class Entity {
    private final int entityId;
    private final EntityType entityType;
}
