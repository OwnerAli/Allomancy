package me.alii.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class CoinInteraction extends SimpleInteraction {
    public static final BuilderCodec<CoinInteraction> CODEC =
            BuilderCodec.builder(CoinInteraction.class, CoinInteraction::new,
                            SimpleInteraction.CODEC)
                    .build();

    @Override
    protected void tick0(boolean firstRun, float time, @NonNullDecl InteractionType type,
                         @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        if (commandBuffer == null) return;

        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Player player = commandBuffer.getComponent(owningEntity, Player.getComponentType());
        if (player == null) return;

        player.sendMessage(Message.raw("WORKED!"));
    }
}
