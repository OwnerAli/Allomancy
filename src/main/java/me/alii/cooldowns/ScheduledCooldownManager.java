package me.alii.cooldowns;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledCooldownManager extends CooldownManager {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ScheduledCooldownManager(long duration, TimeUnit unit,
                                    long cleanupInterval, TimeUnit cleanupUnit) {
        super(duration, unit);
        scheduler.scheduleAtFixedRate(this::resetAll,
                cleanupInterval, cleanupInterval, cleanupUnit);
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}