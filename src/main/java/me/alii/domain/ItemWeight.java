package me.alii.domain;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;

@Getter
public class ItemWeight {
    public static final BuilderCodec<ItemWeight> CODEC;

    private String itemId = "";
    private double weight = 0.0;

    public ItemWeight() {

    }

    public ItemWeight(String itemId, double weight) {
        this.itemId = itemId;
        this.weight = weight;
    }

    static {
        CODEC = BuilderCodec.builder(
                        ItemWeight.class,
                        ItemWeight::new
                )
                .append(new KeyedCodec<>("ItemId", Codec.STRING),
                        (type, value) -> type.itemId = value,
                        (type) -> type.itemId).add()
                .append(new KeyedCodec<>("Weight", Codec.DOUBLE),
                        (type, value) -> type.weight = value,
                        (type) -> type.weight).add()
                .build();
    }
}
