package me.alii.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import me.alii.components.packages.PackageSpawnableComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.atomic.AtomicLong;

public class PackageSpawningSystem extends EntityTickingSystem<ChunkStore> {
    private static final long TIME_BETWEEN_SPAWNS_MILLIS = 30_000;
    private final AtomicLong lastPackageSpawnMillis = new AtomicLong(System.currentTimeMillis());

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<ChunkStore> archetypeChunk,
                     @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        if (System.currentTimeMillis() - lastPackageSpawnMillis.get() < TIME_BETWEEN_SPAWNS_MILLIS) return;

        // Get spawnable component
        PackageSpawnableComponent spawnableComponent =
                archetypeChunk.getComponent(i, PackageSpawnableComponent.getComponentType());

        if (spawnableComponent == null) {
            System.out.println("NULL COMPONENT!");
            return;
        }
        if (spawnableComponent.getSpawnedPackage() != null) {
            System.out.println("NOT NULL PACKAGE!");
            return;
        }

        // Set the last spawned time to now
        this.lastPackageSpawnMillis.set(System.currentTimeMillis());

        spawnableComponent.spawn(commandBuffer);
    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return PackageSpawnableComponent.getComponentType();
    }
}
