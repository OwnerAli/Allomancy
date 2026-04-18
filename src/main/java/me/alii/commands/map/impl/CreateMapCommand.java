package me.alii.commands.map.impl;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import me.alii.AllomancyPlugin;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

public class CreateMapCommand extends AbstractAsyncCommand {
    private final AllomancyPlugin allomancyPlugin;
    private final RequiredArg<String> mapIdArg;

    public CreateMapCommand(AllomancyPlugin allomancyPlugin) {
        super("create", "");
        this.allomancyPlugin = allomancyPlugin;
        this.mapIdArg = withRequiredArg("map-id", "map id", ArgTypes.STRING);
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {
        // Do stuff
        return new CompletableFuture<>();
    }
}
