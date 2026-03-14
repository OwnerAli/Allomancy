package me.alii.components.allomancer;

import com.hypixel.hytale.math.vector.Vector3d;
import lombok.Getter;
import lombok.Setter;
import me.alii.cooldowns.ScheduledCooldownManager;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class AllomancerState {
    public boolean lastPullPressed = false;
    public boolean lastPushPressed = false;
    public Vector3d currentPullTarget = null;
    public Vector3d currentPushTarget = null;
    public boolean isFalling = false;

    public enum CushionState {READY, CUSHIONED, RELAUNCHED, COOLDOWN}

    public CushionState cushionState = CushionState.READY;
    public double fallSpeedAtCushion = 0.0;

    public final ScheduledCooldownManager cooldowns = new ScheduledCooldownManager(
            500, TimeUnit.MILLISECONDS, 30, TimeUnit.SECONDS
    );
}
