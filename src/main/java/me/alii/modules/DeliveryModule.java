package me.alii.modules;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import me.alii.AllomancyPlugin;
import me.alii.components.packages.PackageComponent;
import me.alii.components.packages.PackageSpawnableComponent;
import me.alii.systems.PickupSystem;

@Getter
public class DeliveryModule {
    private final AllomancyPlugin allomancyPlugin;

    private ComponentType<ChunkStore, PackageSpawnableComponent> packageSpawnableComponentType;
    private ComponentType<EntityStore, PackageComponent> packageDeliverableComponentType;

    public DeliveryModule() {
        this.allomancyPlugin = AllomancyPlugin.getInstance();
    }

    public void registerEntityStoreItems() {
        ComponentRegistryProxy<ChunkStore> chunkStoreRegistry = allomancyPlugin.getChunkStoreRegistry();
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = allomancyPlugin.getEntityStoreRegistry();

        this.packageSpawnableComponentType = chunkStoreRegistry.registerComponent(PackageSpawnableComponent.class, "PackageSpawnableComponent",
                PackageSpawnableComponent.CODEC);
        this.packageDeliverableComponentType = entityStoreRegistry.registerComponent(PackageComponent.class, "PackageDeliverableComponent",
                PackageComponent.CODEC);
        entityStoreRegistry.registerSystem(new PickupSystem());
        entityStoreRegistry.registerSystem(new PickupSystem.PackageItemPickupBlockSystem());
    }
}