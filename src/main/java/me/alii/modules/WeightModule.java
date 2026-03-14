package me.alii.modules;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import me.alii.AllomancyPlugin;
import me.alii.components.allomancer.AllomancerComponent;
import me.alii.systems.AllomancerAssignSystem;
import me.alii.systems.DamageSystem;

@Getter
public class WeightModule {
    private final AllomancyPlugin allomancyPlugin;

    private ComponentType<EntityStore, AllomancerComponent> allomancerComponentType;

    public WeightModule() {
        this.allomancyPlugin = AllomancyPlugin.getInstance();
        registerEntityStoreItems();
    }

    public void registerEntityStoreItems() {
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = allomancyPlugin.getEntityStoreRegistry();

        entityStoreRegistry.registerSystem(new AllomancerAssignSystem(allomancyPlugin.getHudRegistry()));
        entityStoreRegistry.registerSystem(new DamageSystem());

        this.allomancerComponentType = entityStoreRegistry.registerComponent(AllomancerComponent.class, "AllomancerComponent",
                AllomancerComponent.CODEC);
    }
}