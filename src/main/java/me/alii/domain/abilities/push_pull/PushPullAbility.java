package me.alii.domain.abilities.push_pull;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.alii.AllomancyPlugin;
import me.alii.cooldowns.ScheduledCooldownManager;
import me.alii.domain.ItemWeight;
import me.alii.domain.Metal;
import me.alii.domain.abilities.Force;
import me.alii.domain.abilities.ForceDirection;
import me.alii.domain.abilities.MetalAbility;
import me.alii.managers.WeightManager;

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

        Vector3d targetBlock3d = targetBlock.toVector3d();

        applyForceTowardBlock(ref, accessor, targetBlock3d.add(0.5, 0.5, 0.5));
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
        BlockType blockType = world.getBlockType(targetBlock.toVector3i());
        if (blockType == null) return;

        ItemWeight itemWeight = weightManager.getWeight(blockType.getId()).orElse(null);
        if (itemWeight == null) {
            player.sendMessage(Message.raw("No weight for: " + blockType.getId()));
            return;
        }

        Vector3d position = transform.getPosition().clone().add(0, 0.5, 0);

        double dx = targetBlock.getX() - position.getX();
        double dy = direction.getForce().equals(Force.PUSH) ? targetBlock.getY() - position.getY() : targetBlock.getY() + 2.0 - position.getY();
        double dz = targetBlock.getZ() - position.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance == 0) return;

        Vector3d toBlock = new Vector3d(dx, dy, dz).normalize();

        double massRatio = (itemWeight.getWeight()) / physics.getMass();
        double force = calculateForce(itemWeight.getWeight(), distance) * massRatio;
        double finalForce = force * direction.getMultiplier();

        player.sendMessage(Message.raw("Block: " + blockType.getId()
                + " Weight: " + itemWeight.getWeight()
                + " Distance: " + String.format("%.2f", distance)
                + " MassRatio: " + String.format("%.3f", massRatio)
                + " Force: " + String.format("%.6f", finalForce)));

        applyForceToAllomancer(accessor, ref, toBlock, finalForce);
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
                direction.getX() * acceleration,
                direction.getY() * acceleration,
                direction.getZ() * acceleration
        );

        force = new Vector3d(force.getX(), force.getY() + ANTI_GRAVITY_FORCE, force.getZ());

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