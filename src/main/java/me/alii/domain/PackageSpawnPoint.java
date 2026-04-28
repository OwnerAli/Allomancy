package me.alii.domain;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.alii.domain.packages.PackageRarity;
import org.joml.Vector3d;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PackageSpawnPoint {
    public static final BuilderCodec<PackageSpawnPoint> CODEC;

    private Vector3d spawnPosition;
    private EnumSet<PackageRarity> rarities = EnumSet.copyOf(List.of(PackageRarity.VALUES));

    static {
        EnumCodec<PackageRarity> packageRarityEnumCodec =
                new EnumCodec<>(PackageRarity.class, EnumCodec.EnumStyle.LEGACY);
        SetCodec<PackageRarity, HashSet<PackageRarity>> packageRarityHashSetCodec =
                new SetCodec<>(packageRarityEnumCodec, HashSet::new, false);

        CODEC = BuilderCodec.builder(PackageSpawnPoint.class, PackageSpawnPoint::new)
                .append(new KeyedCodec<>("SpawnPosition", Vector3dUtil.CODEC),
                        (type, value) -> type.spawnPosition = value,
                        (type) -> type.spawnPosition).add()
                .append(new KeyedCodec<>("Rarities", packageRarityHashSetCodec),
                        (type, value) -> {
                            type.rarities.clear();
                            type.rarities.addAll(value);
                        },
                        (type) -> type.rarities).add()
                .build();
    }
}
