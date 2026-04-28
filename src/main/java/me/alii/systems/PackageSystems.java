package me.alii.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import me.alii.components.packages.PackageSpawnableComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.atomic.AtomicLong;

public class PackageSystems extends EntityTickingSystem<ChunkStore> {
    private static final long TIME_BETWEEN_SPAWNS_MILLIS = 5_000;
    private final AtomicLong lastPackageSpawnMillis = new AtomicLong(System.currentTimeMillis());

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<ChunkStore> archetypeChunk,
                     @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        System.out.println("TICK!");
        long l = System.currentTimeMillis() - lastPackageSpawnMillis.get();
        if (l < TIME_BETWEEN_SPAWNS_MILLIS) return;

        System.out.println("SPAWNING SOON!");

        World world = commandBuffer.getExternalData().getWorld();

        world.execute(() -> {
            // Get spawnable component
            Ref<ChunkStore> referenceTo = archetypeChunk.getReferenceTo(i);

            PackageSpawnableComponent component = commandBuffer.getComponent(referenceTo, PackageSpawnableComponent.getComponentType());

            System.out.println("COMPONENT: " + component);

            if (component == null) {
                System.out.println("NULL COMPONENT!");
                return;
            }

            // Set the last spawned time to now
            this.lastPackageSpawnMillis.set(System.currentTimeMillis());

            component.spawn(commandBuffer);
        });
    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return PackageSpawnableComponent.getComponentType();
    }
}
