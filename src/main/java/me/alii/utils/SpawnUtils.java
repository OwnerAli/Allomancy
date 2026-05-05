package me.alii.utils;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.alii.components.packages.PackageComponent;
import org.joml.Vector3d;

public class SpawnUtils {

    public static void spawnItem(Vector3d position,
                                 String itemId,
                                 ComponentAccessor<ChunkStore> componentAccessor
    ) {
        World world = componentAccessor.getExternalData().getWorld();
        Store<EntityStore> entityStore = world.getEntityStore().getStore();
        ItemStack item = new ItemStack(itemId);

        world.execute(() -> {
            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
            TransformComponent transformComponent = new TransformComponent(position, Rotation3f.NaN);
            ItemComponent itemComponent = new ItemComponent(item);
            holder.addComponent(TransformComponent.getComponentType(), transformComponent);
            holder.addComponent(ItemComponent.getComponentType(), itemComponent);

            // Add package component to be able to uniquely detect packages
            holder.addComponent(PackageComponent.getComponentType(), new PackageComponent());

            entityStore.addEntity(holder, AddReason.SPAWN);
        });
    }
}
