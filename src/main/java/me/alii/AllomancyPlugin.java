package me.alii;

import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import lombok.Getter;
import me.alii.clients.ApiClient;
import me.alii.commands.AdminCommands;
import me.alii.interactions.CoinInteraction;
import me.alii.managers.WeightConfig;
import me.alii.managers.WeightManager;
import me.alii.modules.DeliveryModule;
import me.alii.modules.WeightModule;
import me.alii.packets.BurnKeyPressPacket;
import me.alii.packets.SwitchKeyPressPacket;
import me.alii.streaks.DefaultStreakMilestone;
import me.alii.streaks.MilestoneRegistry;
import me.alii.ui.hud.HudRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

@Getter
public class AllomancyPlugin extends JavaPlugin {

    @Getter
    public static AllomancyPlugin instance;

    private ApiClient apiClient;
    private WeightModule weightModule;
    private DeliveryModule deliveryModule;
    private WeightManager weightManager;
    private HudRegistry hudRegistry;
    private MilestoneRegistry milestoneRegistry;

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
        this.apiClient = new ApiClient();
        this.hudRegistry = new HudRegistry();
        this.weightManager = new WeightManager(weightConfig);
        this.weightModule = new WeightModule();
        this.milestoneRegistry = new MilestoneRegistry();

        this.milestoneRegistry.register(new DefaultStreakMilestone(
                        3,
                        List.of("Warming up!", "Not bad!", "Keep it moving!"),
                        "SFX_Streak",
                        NotificationStyle.Default
                ))
                .register(new DefaultStreakMilestone(
                        5,
                        List.of("On a roll!", "Getting spicy!", "Flow state!"),
                        "SFX_Streak",
                        NotificationStyle.Success
                ))
                .register(new DefaultStreakMilestone(
                        10,
                        List.of("SICK!", "UNSTOPPABLE!", "You're cooking!", "BEAST MODE!"),
                        "SFX_Streak",
                        NotificationStyle.Success
                ))
                .register(new DefaultStreakMilestone(
                        25,
                        List.of("LEGENDARY!", "Are you even human?!", "Chat is losing it!"),
                        "SFX_Streak",
                        NotificationStyle.Warning
                ));

        PacketAdapters.registerInbound(new BurnKeyPressPacket());
        PacketAdapters.registerInbound(new SwitchKeyPressPacket(hudRegistry));
        getCommandRegistry().registerCommand(new AdminCommands(this));
    }

    public DeliveryModule getDeliveryModule() {
        if (deliveryModule == null) {
            deliveryModule = new DeliveryModule();
            deliveryModule.registerEntityStoreItems();
        }
        return deliveryModule;
    }
}
