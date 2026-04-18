package me.alii.components.allomancer;

import lombok.Getter;
import lombok.Setter;
import me.alii.streaks.StreakManager;
import me.alii.cooldowns.ScheduledCooldownManager;
import org.joml.Vector3d;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class AllomancerState {
    public enum CushionState {READY, CUSHIONED, RELAUNCHED, COOLDOWN}

    private final ScheduledCooldownManager cooldowns = new ScheduledCooldownManager(
            500, TimeUnit.MILLISECONDS, 30, TimeUnit.SECONDS
    );

    private boolean lastPullPressed = false;
    private boolean lastPushPressed = false;
    private Vector3d currentPullTarget = null;
    private Vector3d currentPushTarget = null;
    private boolean isFalling = false;

    private StreakManager streakManager = new StreakManager();

    private CushionState cushionState = CushionState.READY;
    private double fallSpeedAtCushion = 0.0;
}
