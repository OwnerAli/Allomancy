package me.alii.domain.abilities;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.alii.domain.Metal;

public interface MetalAbility {
    void apply(Entity entity, ComponentAccessor<EntityStore> accessor);

    Force getForce();

    Metal getMetal();
}
