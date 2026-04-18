package me.alii.components.packages;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import lombok.Setter;
import me.alii.AllomancyPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3d;

@Setter
public class PackageDeliverableComponent implements Component<ChunkStore> {
    public static final BuilderCodec<PackageDeliverableComponent> CODEC;

    private Vector3d deliveryPosition;
    private boolean returnToVendorPossible;

    public static ComponentType<ChunkStore, PackageDeliverableComponent> getComponentType() {
        return AllomancyPlugin.getInstance().getDeliveryModule().getPackageDeliverableComponentType();
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        PackageDeliverableComponent packageDeliverableComponent = new PackageDeliverableComponent();
        packageDeliverableComponent.returnToVendorPossible = returnToVendorPossible;
        return packageDeliverableComponent;
    }

    static {
        CODEC = BuilderCodec.builder(PackageDeliverableComponent.class, PackageDeliverableComponent::new)
                .append(new KeyedCodec<>("SpawnPosition", Vector3dUtil.CODEC),
                        ((type, value) -> type.deliveryPosition = value),
                        (type -> type.deliveryPosition))
                .add()
                .append(new KeyedCodec<>("RTV", BuilderCodec.BOOLEAN),
                        (type, value) -> type.returnToVendorPossible = value,
                        (type) -> type.returnToVendorPossible).add()
                .build();
    }
}
