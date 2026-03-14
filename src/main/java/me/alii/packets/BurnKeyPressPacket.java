package me.alii.packets;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.alii.components.allomancer.AllomancerComponent;

public class BurnKeyPressPacket implements PlayerPacketFilter {

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

            AllomancerComponent allomancer = store.getComponent(ref, AllomancerComponent.getComponentType());
            if (allomancer == null) return;

            for (SyncInteractionChain update : interactionChains.updates) {
                if (update.interactionType != InteractionType.Use) continue;
                allomancer.applyMetalAbilities(player, store);
            }
        });

        return false;
    }
}