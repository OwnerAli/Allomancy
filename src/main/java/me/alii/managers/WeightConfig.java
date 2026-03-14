package me.alii.managers;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import me.alii.domain.ItemWeight;

import java.util.*;

public class WeightConfig {
    public static final BuilderCodec<WeightConfig> CODEC;

    private Set<ItemWeight> itemWeights = new HashSet<>(Set.of(
            // Heavy — strong satisfying pull
            new ItemWeight("Deco_Iron_Stack", 10000.0),
            new ItemWeight("Rock_Gold_Brick", 1000.0),
            // Medium — noticeable pull
            new ItemWeight("iron_ore", 80.0),
            // Light — you'd pull these toward you instead
            new ItemWeight("iron_ingot", 15.0),
            new ItemWeight("coin", 5.0)
    ));

    public Map<String, ItemWeight> buildWeightMap() {
        Map<String, ItemWeight> map = new HashMap<>(itemWeights.size(), 1.0f);
        for (ItemWeight itemWeight : itemWeights) {
            map.put(itemWeight.getItemId(), itemWeight);
        }
        return Collections.unmodifiableMap(map);
    }

    static {
        var itemWeightSetCodec = new SetCodec<>(ItemWeight.CODEC, HashSet::new, false);
        CODEC = BuilderCodec.builder(WeightConfig.class, WeightConfig::new)
                .append(new KeyedCodec<>("ItemWeights", itemWeightSetCodec),
                        (type, value) -> type.itemWeights = value,
                        (type) -> type.itemWeights).add()
                .build();
    }
}
