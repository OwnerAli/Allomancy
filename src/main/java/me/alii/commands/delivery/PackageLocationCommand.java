package me.alii.commands.delivery;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.alii.components.packages.PackageSpawnableComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.awt.*;

public class PackageLocationCommand extends CommandBase {

    public PackageLocationCommand() {
        super("package", "");
        addSubCommand(new AddLocationCommand());
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
    }

    private static class AddLocationCommand extends CommandBase {

        public AddLocationCommand() {
            super("add-loc", "");
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            Ref<EntityStore> ref = commandContext.senderAsPlayerRef();
            if (ref == null) return;

            World world = ref.getStore().getExternalData()
                    .getWorld();

            world.execute(() -> {
                Store<EntityStore> store = ref.getStore();
                PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                if (playerRef == null) return;

                Vector3i targetBlock = TargetUtil.getTargetBlock(ref, 6, store);
                if (targetBlock == null) return;

                ChunkStore chunkStore = world.getChunkStore();
                long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);

                int index = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
                WorldChunk chunk = world.getChunk(chunkIndex);
                BlockComponentChunk blockComponentChunk = chunk.getBlockComponentChunk();

                PackageSpawnableComponent component =
                        blockComponentChunk.getComponent(index, PackageSpawnableComponent.getComponentType());
                Ref<ChunkStore> entityReference = blockComponentChunk
                        .getEntityReference(index);

                System.out.println("COMPONENT: " + component);
                System.out.println("ENTITY REF: " + entityReference);

                Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
                if (chunkRef == null) return;

                Store<ChunkStore> storeChunk = chunkStore.getStore();
                if (storeChunk.getComponent(chunkRef, PackageSpawnableComponent.getComponentType()) != null) return;

                // Get a centered location that isn't inside the block
                Vector3d spawnPosition = new Vector3d(targetBlock).add(0.5, 0.5, 0.5);

                // Create a new component with desired settings
                PackageSpawnableComponent packageSpawnableComponent = new PackageSpawnableComponent();
                packageSpawnableComponent.setSpawnPosition(spawnPosition);

                // Add the component to the block
                storeChunk.addComponent(chunkRef, PackageSpawnableComponent.getComponentType(), packageSpawnableComponent);

                playerRef.sendMessage(Message.raw("Spawn location added!").color(Color.GREEN));
            });
        }
    }
}
