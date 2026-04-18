package me.alii.streaks;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public interface StreakMilestone {
    int getThreshold();
    String pickMessage();
    void onTrigger(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor, World world);
}