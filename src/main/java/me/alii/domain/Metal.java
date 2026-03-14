package me.alii.domain;

import lombok.Getter;
import me.alii.domain.abilities.ForceDirection;
import me.alii.domain.abilities.MetalAbility;
import me.alii.domain.abilities.push_pull.PushPullAbility;

@Getter
public enum Metal {
    IRON("STEEL", new PushPullAbility(ForceDirection.PULL), 0.5f, "Ingredient_Bar_Iron"),
    STEEL("IRON", new PushPullAbility(ForceDirection.PUSH), 0.5f, "Ingredient_Bar_Silver");

    private final String complementaryMetal;
    private final MetalAbility metalAbility;
    private final float burnRate;
    private final String itemId;

    public static final Metal[] VALUES = values();

    Metal(String complementaryMetal, MetalAbility metalAbility, float burnRate, String itemId) {
        this.complementaryMetal = complementaryMetal;
        this.metalAbility = metalAbility;
        this.burnRate = burnRate;
        this.itemId = itemId;
    }

    public String getFormattedName() {
        return name() + " (" + metalAbility.getForce().name() + ")";
    }
}