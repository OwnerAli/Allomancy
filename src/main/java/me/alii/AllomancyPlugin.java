package me.alii;

import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import lombok.Getter;
import me.alii.commands.AdminCommands;
import me.alii.interactions.CoinInteraction;
import me.alii.managers.WeightConfig;
import me.alii.managers.WeightManager;
import me.alii.modules.WeightModule;
import me.alii.packets.BurnKeyPressPacket;
import me.alii.packets.SwitchKeyPressPacket;
import me.alii.ui.hud.HudRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

@Getter
public class AllomancyPlugin extends JavaPlugin {

    @Getter
    public static AllomancyPlugin instance;

    private WeightModule weightModule;
    private WeightManager weightManager;
    private HudRegistry hudRegistry;

    private final Config<WeightConfig> weightConfig;

    public AllomancyPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
        instance = this;
        this.weightConfig = withConfig(WeightConfig.CODEC);
    }

    @Override
    protected void setup() {
        this.weightConfig.save();

        var interactionRegistry = getCodecRegistry(Interaction.CODEC);
        interactionRegistry.register("CoinBag", CoinInteraction.class,
                CoinInteraction.CODEC);
        this.hudRegistry = new HudRegistry();
        this.weightManager = new WeightManager(weightConfig);
        this.weightModule = new WeightModule();

        PacketAdapters.registerInbound(new BurnKeyPressPacket());
        PacketAdapters.registerInbound(new SwitchKeyPressPacket(hudRegistry));
        getCommandRegistry().registerCommand(new AdminCommands());
    }
}
