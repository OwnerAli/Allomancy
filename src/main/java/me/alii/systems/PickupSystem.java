package me.alii.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class PickupSystem extends PickupItemSystem {

    public PickupSystem(@NonNullDecl ComponentType<EntityStore, PickupItemComponent> pickupItemComponentType,
                        @NonNullDecl ComponentType<EntityStore, TransformComponent> transformComponentType) {
        super(pickupItemComponentType, transformComponentType);
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        super.tick(dt, index, archetypeChunk, store, commandBuffer);
    }
}
