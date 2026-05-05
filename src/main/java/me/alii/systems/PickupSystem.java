package me.alii.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.OrderPriority;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.system.PlayerSpatialSystem;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.alii.components.packages.PackageComponent;
import me.alii.managers.PackageSpawnManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class PickupSystem extends HolderSystem<EntityStore> {

    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {

    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl RemoveReason removeReason,
                                @NonNullDecl Store<EntityStore> store) {
        // Get our component (getQuery guarantees only entities with query call this method)
        PackageComponent pkg = holder.getComponent(PackageComponent.getComponentType());
        if (pkg == null) return;

        System.out.println("ITEM REMOVED!");
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PackageComponent.getComponentType();
    }

    public static class PackageItemPickupBlockSystem extends EntityTickingSystem<EntityStore> {
        private final ComponentType<EntityStore, ItemComponent> itemComponentType = ItemComponent.getComponentType();
        private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();

        private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialComponent =
                EntityModule.get().getPlayerSpatialResourceType();

        private final Set<Dependency<EntityStore>> dependencies;

        public PackageItemPickupBlockSystem() {
            this.dependencies = Set.of(new SystemDependency<>(Order.AFTER, PlayerSpatialSystem.class, OrderPriority.CLOSE));
        }

        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return this.dependencies;
        }

        @Nonnull
        public Query<EntityStore> getQuery() {
            return Query.and(PackageComponent.getComponentType());
        }

        public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store,
                         @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            ItemComponent itemComponent = archetypeChunk.getComponent(i, this.itemComponentType);
            if (itemComponent == null) return;

            ItemStack itemStack = itemComponent.getItemStack();
            if (itemStack == null) return;

            String itemId = itemStack.getItem().getId().toLowerCase();
            if (!itemId.contains("package")) return;

            // Allows us to search in an area around player
            SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = store.getResource(this.playerSpatialComponent);

            TransformComponent transformComponent = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
            if (transformComponent == null) return;

            List<Ref<EntityStore>> targetPlayerRefs = SpatialResource.getThreadLocalReferenceList();
            playerSpatialResource.getSpatialStructure().ordered(
                    transformComponent.getPosition(), itemComponent.getPickupRadius(commandBuffer),
                    targetPlayerRefs);

            for (Ref<EntityStore> targetPlayerRef : targetPlayerRefs) {
                if (store.getArchetype(targetPlayerRef).contains(DeathComponent.getComponentType())) continue;
                Player playerComponent = store.getComponent(targetPlayerRef, this.playerComponentType);
                if (playerComponent == null) continue;

                PackageSpawnManager packageSpawnManager = AllomancerAssignSystem.getPackageSpawnManager();
                if (packageSpawnManager == null) return;

                commandBuffer.removeEntity(archetypeChunk.getReferenceTo(i), RemoveReason.REMOVE);
                packageSpawnManager.getSpawnedEntities().clear();
                SoundUtil.playSoundEvent2d(
                        targetPlayerRef,
                        SoundEvent.getAssetMap().getIndex("SFX_Deliver"),
                        SoundCategory.SFX,
                        2,
                        1,
                        store
                );
                break;
            }
        }
    }
}