package me.alii.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.alii.AllomancyPlugin;
import me.alii.components.allomancer.AllomancerComponent;
import me.alii.domain.ItemWeight;
import me.alii.managers.WeightConfig;
import me.alii.managers.WeightManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AdminCommands extends AbstractPlayerCommand {

    public AdminCommands() {
        super("builder", "");
        addSubCommand(new ReloadCommand());
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
                           @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl Ref<EntityStore> ref,
                           @NonNullDecl PlayerRef playerRef,
                           @NonNullDecl World world) {
        AllomancerComponent allomancer = store.getComponent(ref, AllomancerComponent.getComponentType());
        if (allomancer == null) return;
        allomancer.setBuildMode(!allomancer.isBuildMode());
    }

    public static class ReloadCommand extends AbstractAsyncCommand {

        public ReloadCommand() {
            super("reload", "");
        }

        @NonNullDecl
        @Override
        protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {
            return CompletableFuture.runAsync(() -> {
                AllomancyPlugin instance = AllomancyPlugin.getInstance();
                CompletableFuture<WeightConfig> load = instance.getWeightConfig()
                        .load();
                load.thenAccept(weightConfig -> {
                    WeightManager weightManager = instance.getWeightManager();
                    Map<String, ItemWeight> weightsByItem = weightManager.getWeightsByItem();
                    weightsByItem.clear();
                    weightsByItem.putAll(weightConfig.buildWeightMap());
                });
            });
        }
    }
}
