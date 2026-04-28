package me.alii.managers;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import lombok.Getter;
import me.alii.domain.PackageSpawnPoint;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PackageSpawnConfig {
    public static final BuilderCodec<PackageSpawnConfig> CODEC;

    private Set<PackageSpawnPoint> spawnPoints = new HashSet<>();

    public void save(PackageSpawnPoint packageSpawnPoint) {
        spawnPoints.add(packageSpawnPoint);
    }

    static {
        var packageSpawnSetCodec = new SetCodec<>(PackageSpawnPoint.CODEC, HashSet::new, false);
        CODEC = BuilderCodec.builder(PackageSpawnConfig.class, PackageSpawnConfig::new)
                .append(new KeyedCodec<>("SpawnPoints", packageSpawnSetCodec),
                        (type, value) -> type.spawnPoints = value,
                        (type) -> type.spawnPoints).add()
                .build();
    }
}
