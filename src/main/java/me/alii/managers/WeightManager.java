package me.alii.managers;

import com.hypixel.hytale.server.core.util.Config;
import lombok.Getter;
import me.alii.domain.ItemWeight;

import java.util.Map;
import java.util.Optional;

@Getter
public class WeightManager {
    private final Map<String, ItemWeight> weightsByItem;

    public WeightManager(Config<WeightConfig> weightConfig) {
        this.weightsByItem = loadWeights(weightConfig);
    }

    public Optional<ItemWeight> getWeight(String itemId) {
        return Optional.ofNullable(weightsByItem.get(itemId));
    }

    public boolean isMetallic(String itemId) {
        return weightsByItem.containsKey(itemId);
    }

    public Map<String, ItemWeight> loadWeights(Config<WeightConfig> weightConfig) {
        return weightConfig.get().buildWeightMap();
    }
}