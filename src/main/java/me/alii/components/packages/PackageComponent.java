package me.alii.components.packages;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import me.alii.AllomancyPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3d;

@Getter
@Setter
public class PackageComponent implements Component<EntityStore> {
    public static final BuilderCodec<PackageComponent> CODEC;

    private Vector3d deliveryPosition;
    private boolean returnToVendorPossible;

    public static ComponentType<EntityStore, PackageComponent> getComponentType() {
        return AllomancyPlugin.getInstance().getDeliveryModule().getPackageDeliverableComponentType();
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PackageComponent packageComponent = new PackageComponent();
        packageComponent.returnToVendorPossible = returnToVendorPossible;
        return packageComponent;
    }

    static {
        CODEC = BuilderCodec.builder(PackageComponent.class, PackageComponent::new)
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
