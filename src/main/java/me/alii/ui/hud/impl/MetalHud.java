package me.alii.ui.hud.impl;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.alii.components.allomancer.AllomancerComponent;
import me.alii.domain.Metal;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;
import java.util.Set;

public class MetalHud extends CustomUIHud {

    public MetalHud(@NonNullDecl PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        UICommandBuilder builder = uiCommandBuilder.append("Pages/MetalSelectorHud.ui");
        builder.set("#Grid.Background", "#0f1525A6");

        Holder<EntityStore> holder = getPlayerRef().getHolder();
        if (holder == null) return;

        AllomancerComponent allomancerComponent =
                holder.getComponent(AllomancerComponent.getComponentType());
        if (allomancerComponent == null) return;

        buildMetalGrid(builder, allomancerComponent);
    }

    public static UICommandBuilder buildMetalGrid(UICommandBuilder cmd, AllomancerComponent allomancer) {
        Map<Metal, Float> metalReserves = allomancer.getMetalReserves();
        Set<Metal> burningMetals = allomancer.getBurningMetals();
        Metal[] metals = metalReserves.keySet().toArray(new Metal[]{});
        int size = metalReserves.size();

        for (int i = 0; i < size; i++) {
            Metal metal = metals[i];

            boolean contains = burningMetals.contains(metal);

            String row = "#Row" + i;
            String rowFirstSlot = row + "[0] ";
            cmd.append(row, "Pages/MetalCard.ui");
            cmd.set(rowFirstSlot + "#Icon.ItemId", metal.getItemId());
            cmd.set(rowFirstSlot + "#Name.Text", contains ? "-> " + metal.getFormattedName() + " <-" : metal.getFormattedName());
            cmd.set(rowFirstSlot + "#Count.Text", "100.0");
        }
        return cmd;
    }
}