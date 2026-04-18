package me.alii.commands.map;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import me.alii.AllomancyPlugin;
import me.alii.commands.AdminCommands;
import me.alii.domain.ItemWeight;
import me.alii.managers.WeightConfig;
import me.alii.managers.WeightManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapCommand extends AbstractAsyncCommand {
    private final AllomancyPlugin allomancyPlugin;

    public MapCommand(AllomancyPlugin allomancyPlugin) {
        super("map", "");
        this.allomancyPlugin = allomancyPlugin;
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