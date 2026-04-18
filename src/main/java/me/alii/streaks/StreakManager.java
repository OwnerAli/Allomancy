package me.alii.streaks;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import me.alii.AllomancyPlugin;
import org.joml.Vector3d;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StreakManager {
    private static MilestoneRegistry milestoneRegistry = null;

    private final AtomicInteger currentStreak = new AtomicInteger();
    private final Set<Vector3d> interactedPosSet = new HashSet<>();
    private ScheduledFuture<?> streakTimer;

    // How long (ms) the player can be on the ground before streak resets
    private static final long GRACE_PERIOD_MS = 1200;
    // Tracks when the player first touched the ground
    private long groundedSinceMs = -1;

    public void startOrIncrementStreak(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor,
                                       TransformComponent transformComponent, World world) {
        if (milestoneRegistry == null) {
            milestoneRegistry = AllomancyPlugin.getInstance().getMilestoneRegistry();
        }

        incrementStreak(ref, accessor, world);

        if (streakTimer != null) return;
        this.streakTimer = HytaleServer.SCHEDULED_EXECUTOR
                .scheduleAtFixedRate(() -> {
                    if (currentStreak.get() <= 1) return;

                    BlockType blockType = world.getBlockType(
                            Vector3dUtil.toVector3i(transformComponent.getPosition()).sub(0, 1, 0)
                    );

                    boolean isOnGround = blockType != BlockType.EMPTY;

                    if (isOnGround) {
                        if (groundedSinceMs == -1) {
                            // Just landed — start the grace period clock
                            groundedSinceMs = System.currentTimeMillis();
                        } else {
                            long timeOnGround = System.currentTimeMillis() - groundedSinceMs;
                            if (timeOnGround > GRACE_PERIOD_MS) {
                                // Overstayed on the ground — reset
                                groundedSinceMs = -1;
                                resetStreak(ref, world);
                            }
                            // else: still within grace period, streak lives
                        }
                    } else {
                        // In the air — clear the grounded timer
                        groundedSinceMs = -1;
                    }

                }, 0, 33, TimeUnit.MILLISECONDS);
    }

    public void incrementStreak(Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor, World world) {
        int streak = currentStreak.incrementAndGet();
        milestoneRegistry.fire(streak, ref, componentAccessor, world);
    }

    public void addInteractedPos(Vector3d interactedPos) {
        interactedPosSet.add(interactedPos);
    }

    public boolean isInteractionPos(Vector3d interactedPos) {
        return interactedPosSet.contains(interactedPos);
    }

    public boolean isRunning() {
        return streakTimer != null;
    }

    private void resetStreak(Ref<EntityStore> ref, World world) {
        this.currentStreak.set(0);
        this.interactedPosSet.clear();
        world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

            NotificationUtil.sendNotification(playerRef.getPacketHandler(), "Streak Expired!", NotificationStyle.Danger);
            SoundUtil.playSoundEvent2d(
                    ref,
                    SoundEvent.getAssetMap().getIndex("SFX_Generic_Crafting_Failed"),
                    SoundCategory.SFX,
                    1,
                    Math.min((float) (0.5 * currentStreak.get()), 10),
                    store);
        });
    }
}
