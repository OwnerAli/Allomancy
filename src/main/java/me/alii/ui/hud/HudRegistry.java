package me.alii.ui.hud;

import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.alii.components.allomancer.AllomancerComponent;
import me.alii.ui.hud.impl.MetalHud;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HudRegistry {
    private final Map<PlayerRef, MetalHud> playerHuds = new ConcurrentHashMap<>();

    public void register(PlayerRef playerRef, MetalHud simpleHud) {
        playerHuds.put(playerRef, simpleHud);
    }

    public void updateMetalHud(PlayerRef playerRef, AllomancerComponent allomancer) {
        MetalHud metalHud = playerHuds.get(playerRef);
        if (metalHud == null) return;
        UICommandBuilder commandBuilder = MetalHud.buildMetalGrid(new UICommandBuilder(), allomancer);
        metalHud.update(false, commandBuilder);
    }
}
