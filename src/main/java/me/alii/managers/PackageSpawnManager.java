package me.alii.managers;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import me.alii.domain.PackageSpawnPoint;
import me.alii.domain.packages.DeliveryPackage;
import me.alii.utils.SpawnUtils;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PackageSpawnManager {
    private static final int MAX_CONCURRENT_SPAWNS = 2;
    private static final long SPAWN_PERIOD = 20L;
    private static final TimeUnit SPAWN_TIME_UNIT = TimeUnit.MINUTES;
    private static final Random random = new Random();

    private final World world;
    private final PackageSpawnConfig config;

    /**
     * Tracks the spawn points used in the previous cycle to avoid
     * repeating the same locations back-to-back.
     */
    private final List<Vector3d> lastUsedSpawnPoints = new ArrayList<>();

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
        List<Vector3d> selected = selectSpawnPoints();
        selected.forEach(this::spawnPackageAt);
        lastUsedSpawnPoints.clear();
        lastUsedSpawnPoints.addAll(selected);
    }

    /**
     * Selects up to {@link #MAX_CONCURRENT_SPAWNS} spawn points, preferring
     * points not used in the previous cycle where possible.
     */
    private List<Vector3d> selectSpawnPoints() {
        List<Vector3d> allPoints = new ArrayList<>(
                config.getSpawnPoints()
                        .stream()
                        .map(PackageSpawnPoint::getSpawnPosition).toList()
        );
        int count = Math.min(MAX_CONCURRENT_SPAWNS, allPoints.size());

        // Prefer points not used last cycle
        List<Vector3d> preferred = new ArrayList<>(allPoints);
        preferred.removeAll(lastUsedSpawnPoints);

        // If we don't have enough fresh points, fall back to the full pool
        List<Vector3d> pool = preferred.size() >= count ? preferred : allPoints;

        Collections.shuffle(pool, random);
        return pool.subList(0, count);
    }

    private void spawnPackageAt(Vector3d spawnPoint) {
        DeliveryPackage pkg = DeliveryPackage.createPackage();
        Store<ChunkStore> store = world.getChunkStore().getStore();
        NotificationUtil.sendNotificationToUniverse("A new %s Package is spawning at (%s, %s)!"
                .formatted(pkg.packageRarity().name(), spawnPoint.x, spawnPoint.z));
        SpawnUtils.spawnItem(spawnPoint, pkg.packageRarity().getItemId(), store);
    }
}