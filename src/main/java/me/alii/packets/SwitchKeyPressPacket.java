package me.alii.packets;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.alii.components.allomancer.AllomancerComponent;
import me.alii.ui.hud.HudRegistry;

public class SwitchKeyPressPacket implements PlayerPacketFilter {
    private final HudRegistry hudRegistry;

    public SwitchKeyPressPacket(HudRegistry hudRegistry) {
        this.hudRegistry = hudRegistry;
    }

    @Override
    public boolean test(PlayerRef playerRef, Packet packet) {
        if (!(packet instanceof SyncInteractionChains interactionChains)) return false;

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null) return false;

        Store<EntityStore> store = ref.getStore();

        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) return;

            for (SyncInteractionChain update : interactionChains.updates) {
                if (update.interactionType != InteractionType.SwapFrom) continue;

                AllomancerComponent allomancer = store.getComponent(ref, AllomancerComponent.getComponentType());
                if (allomancer == null) return;

                if (allomancer.isBuildMode()) return;

                allomancer.swapPushAndPull();
                SoundUtil.playSoundEvent2d(
                        ref,
                        SoundEvent.getAssetMap().getIndex("SFX_Axe_Iron_Swing"),
                        SoundCategory.SFX,
                        2,
                        12f,
                        store
                );
                hudRegistry.updateMetalHud(playerRef, allomancer);
            }
        });
        return false;
    }
}