package me.alii.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;

public class DamageSystem extends DamageEventSystem {

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store,
                       @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        PlayerRef playerRef = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        if (playerRef == null) return;

        playerRef.sendMessage(Message.raw("YOU TOOK DAMAGE: ").insert(Message.raw("%s".formatted(damage.getAmount()))
                .color(Color.RED).bold(true)));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
