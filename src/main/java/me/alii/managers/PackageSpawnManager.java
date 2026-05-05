package me.alii.managers;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import lombok.Getter;
import me.alii.components.packages.PackageComponent;
import me.alii.domain.packages.DeliveryPackage;
import org.joml.Vector3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PackageSpawnManager {
    private static final int MAX_CONCURRENT_SPAWNS = 1;
    private static final long SPAWN_PERIOD = 10;
    private static final TimeUnit SPAWN_TIME_UNIT = TimeUnit.SECONDS;
    private static final Random random = new Random();

    private final World world;
    private final PackageSpawnConfig config;

    @Getter
    private final List<Ref<EntityStore>> spawnedEntities = new ArrayList<>(MAX_CONCURRENT_SPAWNS);

    /**
     * Tracks the spawn points used in the previous cycle to avoid
     * repeating the same locations back-to-back.
     */
    private final List<SpawnPointNamePair> lastUsedSpawnPoints = new ArrayList<>();

    public PackageSpawnManager(World world, PackageSpawnConfig config) {
        this.world = world;
        this.config = config;
    }

    public void startSpawning() {
        System.out.println("Starting spawning of packages with the following pool: " + config.getSpawnPoints());
        HytaleServer.SCHEDULED_EXECUTOR
                .scheduleAtFixedRate(this::spawnPackages, 0L, SPAWN_PERIOD, SPAWN_TIME_UNIT);
    }

    private void spawnPackages() {
        if (!spawnedEntities.isEmpty()) return;
        List<SpawnPointNamePair> selected = selectSpawnPoints();
        selected.forEach(this::spawnPackageAt);
        lastUsedSpawnPoints.clear();
        lastUsedSpawnPoints.addAll(selected);
    }

    /**
     * Selects up to {@link #MAX_CONCURRENT_SPAWNS} spawn points, preferring
     * points not used in the previous cycle where possible.
     */
    private List<SpawnPointNamePair> selectSpawnPoints() {
        List<SpawnPointNamePair> allPoints = config.getSpawnPoints()
                .stream()
                .map(p -> new SpawnPointNamePair(p.getDisplayName(), p.getSpawnPosition()))
                .toList();

        int count = Math.min(MAX_CONCURRENT_SPAWNS, allPoints.size());

        List<SpawnPointNamePair> preferred = new ArrayList<>(allPoints);
        preferred.removeAll(lastUsedSpawnPoints);

        List<SpawnPointNamePair> pool = preferred.size() >= count ? preferred : new ArrayList<>(allPoints);

        Collections.shuffle(pool, random);
        return pool.subList(0, count);
    }

    private void spawnPackageAt(SpawnPointNamePair spawnPoint) {
        DeliveryPackage pkg = DeliveryPackage.createPackage();
        Store<ChunkStore> store = world.getChunkStore().getStore();

        // Send all players message about package spawning
        Message mainMessage = Message.raw(
                "%s package incoming! Head to %s to grab it!".formatted(
                        pkg.packageRarity().name(),
                        spawnPoint.displayName()
                )
        ).color(Color.orange);

        String itemId = pkg.packageRarity().getItemId();
        NotificationUtil.sendNotificationToUniverse(mainMessage);

        World world = store.getExternalData().getWorld();
        Store<EntityStore> entityStore = world.getEntityStore().getStore();
        ItemStack item = new ItemStack(itemId);

        world.execute(() -> {
            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
            TransformComponent transformComponent = new TransformComponent(spawnPoint.spawnPosition(), Rotation3f.NaN);
            ItemComponent itemComponent = new ItemComponent(item);
            holder.addComponent(TransformComponent.getComponentType(), transformComponent);
            holder.addComponent(ItemComponent.getComponentType(), itemComponent);

            // Add package component to be able to uniquely detect packages
            holder.addComponent(PackageComponent.getComponentType(), new PackageComponent());

            spawnedEntities.add(entityStore.addEntity(holder, AddReason.SPAWN));
        });
    }

    private record SpawnPointNamePair(String displayName, Vector3d spawnPosition) {
    }
}