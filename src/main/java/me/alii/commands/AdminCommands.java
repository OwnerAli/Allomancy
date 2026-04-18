package me.alii.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.alii.AllomancyPlugin;
import me.alii.commands.delivery.DeliveryLocationCommand;
import me.alii.commands.delivery.PackageLocationCommand;
import me.alii.components.allomancer.AllomancerComponent;
import me.alii.domain.ItemWeight;
import me.alii.managers.WeightConfig;
import me.alii.managers.WeightManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AdminCommands extends AbstractPlayerCommand {
    private final AllomancyPlugin allomancyPlugin;

    public AdminCommands(AllomancyPlugin allomancyPlugin) {
        super("trickster", "");
        this.allomancyPlugin = allomancyPlugin;
        addSubCommand(new ReloadCommand());
        addSubCommand(new MapCommand(allomancyPlugin));
        addSubCommand(new DeliveryLocationCommand());
        addSubCommand(new PackageLocationCommand());
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

    public static class MapCommand extends AbstractAsyncCommand {
        private final AllomancyPlugin allomancyPlugin;

        public MapCommand(AllomancyPlugin allomancyPlugin) {
            super("map", "");
            this.allomancyPlugin = allomancyPlugin;
            addSubCommand(new CreateCommand(allomancyPlugin));
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

        public static class CreateCommand extends AbstractAsyncCommand {
            private final AllomancyPlugin allomancyPlugin;
            private final RequiredArg<String> mapIdArg;

            public CreateCommand(AllomancyPlugin allomancyPlugin) {
                super("create", "");
                this.allomancyPlugin = allomancyPlugin;
                this.mapIdArg = withRequiredArg("map-id", "map id", ArgTypes.STRING);
            }

            @NonNullDecl
            @Override
            protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {
                String mapId = mapIdArg.get(commandContext);
                allomancyPlugin.getApiClient()
                        .uploadMap(mapId, Path.of(""), Path.of(""));
                return new CompletableFuture<>();
            }
        }
    }
}
