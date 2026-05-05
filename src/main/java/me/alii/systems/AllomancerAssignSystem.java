package me.alii.systems;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import me.alii.AllomancyPlugin;
import me.alii.components.allomancer.AllomancerComponent;
import me.alii.domain.Metal;
import me.alii.managers.PackageSpawnManager;
import me.alii.ui.hud.HudRegistry;
import me.alii.ui.hud.impl.MetalHud;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class AllomancerAssignSystem extends HolderSystem<EntityStore> {
    @Getter
    private static PackageSpawnManager packageSpawnManager = null;
    private final HudRegistry hudRegistry;

    public AllomancerAssignSystem(HudRegistry hudRegistry) {
        this.hudRegistry = hudRegistry;
    }

    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
                            @NonNullDecl AddReason addReason,
                            @NonNullDecl Store<EntityStore> store) {
        AllomancerComponent allomancer = holder.getComponent(AllomancerComponent.getComponentType());
        if (allomancer != null) {
            allomancer.grantMetal(Metal.IRON, 100);
            allomancer.grantMetal(Metal.STEEL, 100);
            allomancer.startBurningInfinite(Metal.IRON);
        } else {
            AllomancerComponent allomancerComponent = new AllomancerComponent();
            allomancerComponent.grantMetal(Metal.IRON, 100);
            allomancerComponent.grantMetal(Metal.STEEL, 100);

            holder.addComponent(AllomancerComponent.getComponentType(), allomancerComponent);
        }

        // Hide players default hud
        Player player = holder.getComponent(Player.getComponentType());
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (player != null && playerRef != null) {
            HudManager hudManager = player.getHudManager();
            Set<HudComponent> visibleHudComponents = hudManager.getVisibleHudComponents();

            visibleHudComponents.forEach(hudComponent -> {
                if (HudComponent.Reticle.equals(hudComponent) ||
                        HudComponent.BuilderToolsLegend.equals(hudComponent) ||
                        HudComponent.Notifications.equals(hudComponent)) return;
                hudManager.hideHudComponents(playerRef, hudComponent);
            });

            MetalHud metalHud = new MetalHud(playerRef);
            hudManager.setCustomHud(playerRef, metalHud);
            hudRegistry.register(playerRef, metalHud);

            int health = DefaultEntityStatTypes.getHealth();
            EntityStatMap statMap = holder.getComponent(EntityStatMap.getComponentType());
            StaticModifier staticModifier = new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE, -99);
            statMap.putModifier(health, "allomancer", staticModifier);
            if (packageSpawnManager == null) {
                packageSpawnManager = new PackageSpawnManager(player.getWorld(), AllomancyPlugin.getInstance().getPackageConfig().get());
                packageSpawnManager.startSpawning();
            }
        }
    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder,
                                @NonNullDecl RemoveReason removeReason,
                                @NonNullDecl Store<EntityStore> store) {

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    private void addMetalHud(PlayerRef ref) {

    }
}
