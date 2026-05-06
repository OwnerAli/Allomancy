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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PackageSpawnManager {
    private static final int MAX_CONCURRENT_SPAWNS = 1;
    private static final long SPAWN_PERIOD_SECONDS = 300; // 5 minutes
    private static final Random random = new Random();

    private final World world;
    private final PackageSpawnConfig config;

    @Getter
    private final List<Ref<EntityStore>> spawnedEntities = new ArrayList<>(MAX_CONCURRENT_SPAWNS);
    private final List<SpawnPointNamePair> lastUsedSpawnPoints = new ArrayList<>();

    private ScheduledFuture<?> scheduledSpawn;
    private Instant nextSpawnTime;

    public PackageSpawnManager(World world, PackageSpawnConfig config) {
        this.world = world;
        this.config = config;
    }

    public void startSpawning() {
        scheduleNext();
    }

    public void stopSpawning() {
        if (scheduledSpawn != null && !scheduledSpawn.isDone()) {
            scheduledSpawn.cancel(false);
        }
    }

    /**
     * Immediately spawns a package and resets the timer from now.
     */
    public void forceSpawnNow() {
        stopSpawning();
        spawnPackages();
        scheduleNext();
    }

    /**
     * Returns how many seconds until the next spawn, or -1 if not scheduled.
     */
    public long getSecondsUntilNextSpawn() {
        if (nextSpawnTime == null) return -1;
        long seconds = Instant.now().until(nextSpawnTime, ChronoUnit.SECONDS);
        return Math.max(0, seconds);
    }

    /**
     * Returns a formatted string like "2m 34s" for display in-game.
     */
    public String getFormattedTimeUntilNextSpawn() {
        long seconds = getSecondsUntilNextSpawn();
        if (seconds < 0) return "Not scheduled";
        long minutes = seconds / 60;
        long secs = seconds % 60;
        if (minutes > 0) return "%dm %ds".formatted(minutes, secs);
        return "%ds".formatted(secs);
    }

    public boolean isPackageCurrentlySpawned() {
        return !spawnedEntities.isEmpty();
    }

    private void scheduleNext() {
        nextSpawnTime = Instant.now().plusSeconds(SPAWN_PERIOD_SECONDS);
        scheduledSpawn = HytaleServer.SCHEDULED_EXECUTOR
                .schedule(this::onTimerFired, SPAWN_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    private void onTimerFired() {
        spawnPackages();
        scheduleNext();
    }

    private void spawnPackages() {
        if (!spawnedEntities.isEmpty()) {
            world.execute(() -> {
                spawnedEntities.forEach(entity ->
                        world.getEntityStore().getStore().removeEntity(entity, RemoveReason.REMOVE)
                );
                spawnedEntities.clear();
            });
        }

        List<SpawnPointNamePair> selected = selectSpawnPoints();
        selected.forEach(this::spawnPackageAt);
        lastUsedSpawnPoints.clear();
        lastUsedSpawnPoints.addAll(selected);
    }

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

        Message mainMessage = Message.raw(
                "%s package incoming! Head to %s to grab it!".formatted(
                        pkg.packageRarity().name(),
                        spawnPoint.displayName()
                )
        ).color(Color.orange);

        NotificationUtil.sendNotificationToUniverse(mainMessage);

        String itemId = pkg.packageRarity().getItemId();
        World world = store.getExternalData().getWorld();
        Store<EntityStore> entityStore = world.getEntityStore().getStore();
        ItemStack item = new ItemStack(itemId);

        world.execute(() -> {
            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
            holder.addComponent(TransformComponent.getComponentType(),
                    new TransformComponent(spawnPoint.spawnPosition(), Rotation3f.NaN));
            holder.addComponent(ItemComponent.getComponentType(),
                    new ItemComponent(item));
            holder.addComponent(PackageComponent.getComponentType(),
                    new PackageComponent());

            spawnedEntities.add(entityStore.addEntity(holder, AddReason.SPAWN));
        });
    }

    private record SpawnPointNamePair(String displayName, Vector3d spawnPosition) {
    }
}