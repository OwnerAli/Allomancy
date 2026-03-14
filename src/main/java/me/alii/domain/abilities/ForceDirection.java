package me.alii.domain.abilities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForceDirection {
    private final Force force;
    private final float multiplier;

    ForceDirection(Force force, float multiplier) {
        this.force = force;
        this.multiplier = multiplier;
    }

    public static final ForceDirection PUSH = new ForceDirection(Force.PUSH, -1f);
    public static final ForceDirection PULL = new ForceDirection(Force.PULL, 1f);
}