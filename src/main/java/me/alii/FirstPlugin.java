package me.alii;

import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import me.alii.commands.FirstCommand;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FirstPlugin extends JavaPlugin {

    public FirstPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        // Initialize everything
        CommandRegistry commandRegistry = this.getCommandRegistry();
        commandRegistry.registerCommand(new FirstCommand());
    }
}