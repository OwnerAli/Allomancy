package me.alii.components.allomancer;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import me.alii.AllomancyPlugin;
import me.alii.domain.Metal;
import me.alii.domain.abilities.push_pull.PushPullAbility;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3d;

import java.util.*;

@Getter
@Setter
public class AllomancerComponent implements Component<EntityStore> {
    public static final BuilderCodec<AllomancerComponent> CODEC;

    private final Map<Metal, Float> metalReserves = new EnumMap<>(Metal.class);
    private final Set<Metal> burningMetals = EnumSet.noneOf(Metal.class);

    private transient final AllomancerState allomancerState = new AllomancerState();

    private boolean buildMode;

    public boolean isMistborn() {
        return metalReserves.size() == Metal.VALUES.length;
    }

    public boolean isMisting() {
        return metalReserves.size() == 1;
    }

    public boolean hasAccess(Metal metal) {
        return metalReserves.containsKey(metal);
    }

    public float getReserve(Metal metal) {
        return metalReserves.getOrDefault(metal, 0f);
    }

    public boolean hasReserve(Metal metal) {
        return getReserve(metal) > 0f;
    }

    public boolean isBurning(Metal metal) {
        return burningMetals.contains(metal);
    }

    public Set<Metal> getBurningMetals() {
        return burningMetals.isEmpty() ? Collections.emptySet() : EnumSet.copyOf(burningMetals);
    }

    public void grantMetal(Metal metal, float initialReserve) {
        metalReserves.putIfAbsent(metal, Math.max(0f, initialReserve));
    }

    public boolean startBurning(Metal metal) {
        if (!hasAccess(metal) || !hasReserve(metal)) return false;
        burningMetals.add(metal);
        return true;
    }

    public void startBurningInfinite(Metal metal) {
        burningMetals.add(metal);
    }

    public void swapPushAndPull() {
        ArrayList<Metal> metals = new ArrayList<>(burningMetals);
        metals.forEach(metal -> {
            Metal complementary = Metal.valueOf(metal.getComplementaryMetal());
            burningMetals.remove(metal);
            burningMetals.add(complementary);
        });
    }

    public void applyMetalAbilities(Entity entity, ComponentAccessor<EntityStore> accessor) {
        burningMetals.forEach(metal -> metal.getMetalAbility().apply(entity, accessor));
    }

    public void stopBurning(Metal metal) {
        burningMetals.remove(metal);
    }

    public void stopBurningAll() {
        burningMetals.clear();
    }

    public void addReserve(Metal metal, float amount) {
        if (!hasAccess(metal) || amount <= 0f) return;
        metalReserves.merge(metal, amount, Float::sum);
    }

    public boolean drainReserve(Metal metal, float amount) {
        if (!hasAccess(metal)) return false;
        float remaining = Math.max(0f, getReserve(metal) - amount);
        metalReserves.put(metal, remaining);
        if (remaining == 0f) {
            burningMetals.remove(metal);
            return false;
        }
        return true;
    }

    public static ComponentType<EntityStore, AllomancerComponent> getComponentType() {
        return AllomancyPlugin.getInstance().getWeightModule().getAllomancerComponentType();
    }

    static {
        CODEC = BuilderCodec.builder(AllomancerComponent.class, AllomancerComponent::new)
                .append(new KeyedCodec<>("MetalReserves",
                                new MapCodec<>(Codec.FLOAT, HashMap::new, false)),
                        (type, value) -> value.forEach((k, v) ->
                                type.metalReserves.put(Metal.valueOf(k), Math.max(0f, v))),
                        (type) -> {
                            Map<String, Float> stringMap = new HashMap<>();
                            type.metalReserves.forEach((k, v) -> stringMap.put(k.name(), v));
                            return stringMap;
                        }).add()
                .append(new KeyedCodec<>("BurningMetals",
                                new SetCodec<>(Codec.STRING, HashSet::new, false)),
                        (type, value) -> value.forEach(k -> {
                            Metal metal = Metal.valueOf(k);
                            // Integrity check on load — don't restore burning state if no reserve
                            if (type.hasReserve(metal)) type.burningMetals.add(metal);
                        }),
                        (type) -> {
                            Set<String> stringSet = new HashSet<>();
                            type.burningMetals.forEach(m -> stringSet.add(m.name()));
                            return stringSet;
                        }).add()
                .build();
    }

    @NullableDecl
    @Override
    public AllomancerComponent clone() {
        AllomancerComponent copy = new AllomancerComponent();
        copy.metalReserves.putAll(metalReserves);
        copy.burningMetals.addAll(burningMetals);
        return copy;
    }
}
