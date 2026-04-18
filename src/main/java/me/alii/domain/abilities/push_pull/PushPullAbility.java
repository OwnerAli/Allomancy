package me.alii.domain.abilities.push_pull;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.alii.AllomancyPlugin;
import me.alii.components.allomancer.AllomancerComponent;
import me.alii.cooldowns.ScheduledCooldownManager;
import me.alii.domain.ItemWeight;
import me.alii.domain.Metal;
import me.alii.domain.abilities.Force;
import me.alii.domain.abilities.ForceDirection;
import me.alii.domain.abilities.MetalAbility;
import me.alii.managers.WeightManager;
import me.alii.streaks.StreakManager;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.concurrent.TimeUnit;

public class PushPullAbility implements MetalAbility {
    private static final WeightManager weightManager = AllomancyPlugin.getInstance().getWeightManager();

    private static final ScheduledCooldownManager cooldownManager =
            new ScheduledCooldownManager(
                    500, TimeUnit.MILLISECONDS,
                    30, TimeUnit.SECONDS
            );

    protected static final VelocityConfig ALLOMANCY_CONFIG;

    protected static final double ANTI_GRAVITY_FORCE = 0.5;

    protected final ForceDirection direction;

    public PushPullAbility(ForceDirection direction) {
        this.direction = direction;
    }

    @Override
    public void apply(Entity entity, ComponentAccessor<EntityStore> accessor) {
        Ref<EntityStore> ref = entity.getReference();
        if (ref == null) return;

        Vector3i targetBlock = TargetUtil.getTargetBlock(ref, 5, accessor);
        if (targetBlock == null) return;

        applyForceTowardBlock(ref, accessor, new Vector3d(targetBlock).add(0.5, 0.5, 0.5));
    }

    @Override
    public Force getForce() {
        return direction.getForce();
    }

    @Override
    public Metal getMetal() {
        return Metal.IRON;
    }

    // Force is stronger at range, weaker up close — pulls you in then lets you arrive naturally
    private double calculateForce(double weight, double distance) {
        return (distance * weight) * 0.0001; // multiplier to keep weight values sane
    }

    private void applyForceTowardBlock(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor,
                                       Vector3d targetBlock) {
        TransformComponent transform = accessor.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) return;

        PhysicsValues physics = accessor.getComponent(ref, PhysicsValues.getComponentType());
        if (physics == null) return;

        Player player = accessor.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        World world = accessor.getExternalData().getWorld();

        Vector3i targetBlockInt = Vector3dUtil.toVector3i(targetBlock);

        BlockType blockType = world.getBlockType(targetBlockInt);
        if (blockType == null) return;

        ItemWeight itemWeight = weightManager.getWeight(blockType.getId()).orElse(null);
        if (itemWeight == null) return;

        Vector3d position = new Vector3d(transform.getPosition()).add(0, 0.5, 0);

        double dx = targetBlock.x() - position.x();
        double dy = direction.getForce().equals(Force.PUSH) ? targetBlock.y() - position.y() : targetBlock.y() + 2.0 - position.y();
        double dz = targetBlock.z() - position.z();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance == 0) return;

        Vector3d toBlock = new Vector3d(dx, dy, dz).normalize();

        double massRatio = (itemWeight.getWeight()) / physics.getMass();
        double force = calculateForce(itemWeight.getWeight(), distance) * massRatio;
        double finalForce = force * direction.getMultiplier();

        AllomancerComponent allomancer = accessor.getComponent(ref, AllomancerComponent.getComponentType());
        if (allomancer == null) return;

        StreakManager streakManager = allomancer.getAllomancerState()
                .getStreakManager();

        if (!streakManager.isInteractionPos(targetBlock)) {
            streakManager.startOrIncrementStreak(ref, accessor, transform, world);
        }
        streakManager.addInteractedPos(targetBlock);

        applyForceToAllomancer(accessor, ref, toBlock, finalForce);
        SoundUtil.playSoundEvent2d(
                ref,
                direction.getForce().equals(Force.PUSH) ? SoundEvent.getAssetMap().getIndex("SFX_Push") :
                        SoundEvent.getAssetMap().getIndex("SFX_Pull"),
                SoundCategory.SFX,
                5,
                0.5f,
                accessor);
    }

    private void applyForceToAllomancer(ComponentAccessor<EntityStore> accessor, Ref<EntityStore> ref,
                                        Vector3d direction, double forceMagnitude) {
        if (!cooldownManager.tryUse(ref)) return;

        Velocity velocity = accessor.getComponent(ref, Velocity.getComponentType());
        PhysicsValues physics = accessor.getComponent(ref, PhysicsValues.getComponentType());
        if (velocity == null || physics == null) return;

        Player player = accessor.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        double acceleration = (forceMagnitude / physics.getMass()) * 50000;

        Vector3d force = new Vector3d(
                direction.x() * acceleration,
                direction.y() * acceleration,
                direction.z() * acceleration
        );

        force = new Vector3d(force.x(), force.y() + ANTI_GRAVITY_FORCE, force.z());

        velocity.addInstruction(force, ALLOMANCY_CONFIG, ChangeVelocityType.Add);
    }

    static {
        ALLOMANCY_CONFIG = new VelocityConfig();
    }
}

//@Override
//public void apply(Entity entity, ComponentAccessor<EntityStore> accessor) {
//    World world = accessor.getExternalData().getWorld();
//
//    Ref<EntityStore> reference = entity.getReference();
//    if (reference == null || !reference.isValid()) return;
//
//    TransformComponent transform = accessor.getComponent(reference, TransformComponent.getComponentType());
//    if (transform == null) return;
//
//    Vector3d position = transform.getPosition();
//    int originX = (int) position.getX();
//    int originY = (int) position.getY();
//    int originZ = (int) position.getZ();
//
//    Long2ObjectMap<ItemWeight> candidates = new Long2ObjectOpenHashMap<>();
//    int radius = (int) SCAN_RADIUS;
//    for (int dx = -radius; dx <= radius; dx++) {
//        for (int dy = -radius; dy <= radius; dy++) {
//            for (int dz = -radius; dz <= radius; dz++) {
//                candidates.put(BlockUtil.pack(dx, dy, dz), null);
//            }
//        }
//    }
//
//    world.getBlockBulkRelative(
//            candidates,
//            localX -> localX + originX,
//            localY -> localY + originY,
//            localZ -> localZ + originZ,
//            (_, _, _, chunk, x, y, z, _, _, _) -> {
//                BlockType blockType = chunk.getBlockType(x, y, z);
//                if (blockType == null || blockType.getId().equalsIgnoreCase("empty")) return;
//
//                // Skip blocks that aren't metallic
//                ItemWeight itemWeight = weightManager.getWeight(blockType.getId()).orElse(null);
//                if (itemWeight == null) return;
//
//                double distance = position.distanceTo(x, y, z);
//                if (distance == 0 || distance > SCAN_RADIUS) return;
//
//
//                // Draw a particle at the metallic block to visualize what's being targeted
//                world.execute(() -> {
//                    Vector3d particlePos = new Vector3d(x, y + 1, z);
//                    System.out.println("FOUND METAL! SPAWNING PARTICLES! " + particlePos);
//                    ParticleUtil.spawnParticleEffect(
//                            "Alerted",
//                            particlePos,
//                            accessor
//                    );
//                });
//            }
//    );
//}