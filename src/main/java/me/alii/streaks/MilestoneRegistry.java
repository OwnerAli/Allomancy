package me.alii.streaks;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Map;
import java.util.TreeMap;

public class MilestoneRegistry {
    private final TreeMap<Integer, StreakMilestone> milestones = new TreeMap<>();

    public MilestoneRegistry register(StreakMilestone milestone) {
        milestones.put(milestone.getThreshold(), milestone);
        return this;
    }

    /**
     * Called on every streak increment. Fires the highest milestone
     * whose threshold is less than or equal currentStreak, but only exactly when crossed.
     */
    public void fire(int currentStreak, Ref<EntityStore> ref,
                     ComponentAccessor<EntityStore> accessor, World world) {
        StreakMilestone milestone = milestones.get(currentStreak);
        if (milestone != null) {
            milestone.onTrigger(ref, accessor, world);
        }
    }

    /**
     * Optional: fires the closest milestone at or below the streak.
     * Useful for "every N streaks" patterns.
     */
    public void fireFloor(int currentStreak, Ref<EntityStore> ref,
                          ComponentAccessor<EntityStore> accessor, World world) {
        Map.Entry<Integer, StreakMilestone> entry = milestones.floorEntry(currentStreak);
        if (entry != null && currentStreak % entry.getKey() == 0) {
            entry.getValue().onTrigger(ref, accessor, world);
        }
    }
}