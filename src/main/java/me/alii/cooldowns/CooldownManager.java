package me.alii.cooldowns;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {
    private final Map<Ref<EntityStore>, Long> cooldowns = new ConcurrentHashMap<>();
    private final long cooldownMs;

    public CooldownManager(long duration, TimeUnit unit) {
        this.cooldownMs = unit.toMillis(duration);
    }

    public boolean tryUse(Ref<EntityStore> ref) {
        long now = System.currentTimeMillis();
        Long lastUsed = cooldowns.get(ref);
        if (lastUsed != null && now - lastUsed < cooldownMs) return false;
        cooldowns.put(ref, now);
        return true;
    }

    public boolean isOnCooldown(Ref<EntityStore> ref) {
        Long lastUsed = cooldowns.get(ref);
        return lastUsed != null && System.currentTimeMillis() - lastUsed < cooldownMs;
    }

    public long getRemainingMs(Ref<EntityStore> ref) {
        Long lastUsed = cooldowns.get(ref);
        if (lastUsed == null) return 0;
        return Math.max(0, cooldownMs - (System.currentTimeMillis() - lastUsed));
    }

    public void reset(Ref<EntityStore> ref) {
        cooldowns.remove(ref);
    }

    public void resetAll() {
        cooldowns.clear();
    }
}