package me.alii.commands.delivery;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

public class PackageDeliveryLocationCommand extends AbstractAsyncCommand {

    public PackageDeliveryLocationCommand() {
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
//            Ref<EntityStore> ref = commandContext.senderAsPlayerRef();
//            if (ref == null) return CompletableFuture.completedFuture(null);
//
//            Store<EntityStore> store = ref.getStore();
//            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
//            if (playerRef == null) return CompletableFuture.completedFuture(null);
//
//            World world = ref.getStore().getExternalData()
//                    .getWorld();
//
//            world.execute(() -> {
//                Vector3i targetBlock = TargetUtil.getTargetBlock(ref, 6, store);
//                if (targetBlock == null) return;
//
//                ChunkStore chunkStore = world.getChunkStore();
//
//                long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
//                Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
//                if (chunkRef == null) return;
//
//                Store<ChunkStore> storeChunk = chunkStore.getStore();
//                if (storeChunk.getComponent(chunkRef, PackageSpawnableComponent.getComponentType()) != null) return;
//
//                // Get a centered location that isn't inside the block
//                Vector3d position = new Vector3d(targetBlock).add(0.5, 0.5, 0.5);
//
//                // Create a new component with desired settings
//                PackageComponent packageComponent = new PackageComponent();
//                packageComponent.setDeliveryPosition(position);
//                packageComponent.setReturnToVendorPossible(true);
//
//                // Add the component to the block
//                storeChunk.addComponent(chunkRef, PackageComponent.getComponentType(), packageComponent);
//
//                playerRef.sendMessage(Message.raw("Delivery location added!").color(Color.GREEN));
//            });
//
            return CompletableFuture.completedFuture(null);
        }
    }
}
