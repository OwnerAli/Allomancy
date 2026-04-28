package me.alii.components.packages;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import lombok.Getter;
import lombok.Setter;
import me.alii.AllomancyPlugin;
import me.alii.domain.packages.DeliveryPackage;
import me.alii.utils.SpawnUtils;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3d;

@Getter
@Setter
public class PackageSpawnableComponent implements Component<ChunkStore> {
    public static final BuilderCodec<PackageSpawnableComponent> CODEC;

    private Vector3d spawnPosition;
    private transient DeliveryPackage spawnedPackage = null;

    public PackageSpawnableComponent() {
    }

    public PackageSpawnableComponent(Vector3d spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public static ComponentType<ChunkStore, PackageSpawnableComponent> getComponentType() {
        return AllomancyPlugin.getInstance().getDeliveryModule().getPackageSpawnableComponentType();
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        PackageSpawnableComponent packageSpawnableComponent = new PackageSpawnableComponent(spawnPosition);
        packageSpawnableComponent.spawnedPackage = spawnedPackage;
        return packageSpawnableComponent;
    }

    public void spawn(ComponentAccessor<ChunkStore> componentAccessor) {
        if (this.spawnPosition == null) {
            System.out.println("SPAWN POSITION NULL!");
            return;
        }
        this.spawnedPackage = DeliveryPackage.createPackage();
        NotificationUtil.sendNotificationToUniverse("A new %s Package is spawning at (%s, %s)!"
                .formatted(spawnedPackage.packageRarity().name(), spawnPosition.x, spawnPosition.z));
        SpawnUtils.spawnItem(spawnPosition, spawnedPackage.packageRarity().getItemId(), componentAccessor);
    }

    static {
        CODEC = BuilderCodec.builder(PackageSpawnableComponent.class, PackageSpawnableComponent::new)
                .append(new KeyedCodec<>("SpawnPosition", Vector3dUtil.CODEC),
                        ((type, value) -> type.spawnPosition = value),
                        (type -> type.spawnPosition))
                .add()
                .build();
    }
}
