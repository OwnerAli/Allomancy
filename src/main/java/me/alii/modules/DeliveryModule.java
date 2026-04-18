package me.alii.modules;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import lombok.Getter;
import me.alii.AllomancyPlugin;
import me.alii.components.packages.PackageDeliverableComponent;
import me.alii.components.packages.PackageSpawnableComponent;
import me.alii.systems.PackageSpawningSystem;

@Getter
public class DeliveryModule {
    private final AllomancyPlugin allomancyPlugin;

    private ComponentType<ChunkStore, PackageSpawnableComponent> packageSpawnableComponentType;
    private ComponentType<ChunkStore, PackageDeliverableComponent> packageDeliverableComponentType;

    public DeliveryModule() {
        this.allomancyPlugin = AllomancyPlugin.getInstance();
    }

    public void registerEntityStoreItems() {
        ComponentRegistryProxy<ChunkStore> chunkStoreRegistry = allomancyPlugin.getChunkStoreRegistry();

        this.packageSpawnableComponentType = chunkStoreRegistry.registerComponent(PackageSpawnableComponent.class, "PackageSpawnableComponent",
                PackageSpawnableComponent.CODEC);
        this.packageDeliverableComponentType = chunkStoreRegistry.registerComponent(PackageDeliverableComponent.class, "PackageDeliverableComponent",
                PackageDeliverableComponent.CODEC);
        chunkStoreRegistry.registerSystem(new PackageSpawningSystem());
    }
}