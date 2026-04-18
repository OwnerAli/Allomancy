package me.alii.commands.delivery;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.alii.components.packages.PackageDeliverableComponent;
import me.alii.components.packages.PackageSpawnableComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class DeliveryLocationCommand extends AbstractAsyncCommand {

    public DeliveryLocationCommand() {
        super("deliver", "");
        addSubCommand(new AddLocationCommand());
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {
        return new CompletableFuture<>();
    }

    private static class AddLocationCommand extends AbstractAsyncCommand {

        public AddLocationCommand() {
            super("loc-add", "");
        }

        @NonNullDecl
        @Override
        protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {
            Ref<EntityStore> ref = commandContext.senderAsPlayerRef();
            if (ref == null) return CompletableFuture.completedFuture(null);

            Store<EntityStore> store = ref.getStore();
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRef == null) return CompletableFuture.completedFuture(null);

            World world = ref.getStore().getExternalData()
                    .getWorld();

            world.execute(() -> {
                Vector3i targetBlock = TargetUtil.getTargetBlock(ref, 6, store);
                if (targetBlock == null) return;

                ChunkStore chunkStore = world.getChunkStore();

                long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
                Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
                if (chunkRef == null) return;

                Store<ChunkStore> storeChunk = chunkStore.getStore();
                if (storeChunk.getComponent(chunkRef, PackageSpawnableComponent.getComponentType()) != null) return;

                // Get a centered location that isn't inside the block
                Vector3d position = new Vector3d(targetBlock).add(0.5, 0.5, 0.5);

                // Create a new component with desired settings
                PackageDeliverableComponent packageDeliverableComponent = new PackageDeliverableComponent();
                packageDeliverableComponent.setDeliveryPosition(position);
                packageDeliverableComponent.setReturnToVendorPossible(true);

                // Add the component to the block
                storeChunk.addComponent(chunkRef, PackageDeliverableComponent.getComponentType(), packageDeliverableComponent);

                playerRef.sendMessage(Message.raw("Delivery location added!").color(Color.GREEN));
            });

            return CompletableFuture.completedFuture(null);
        }
    }
}
