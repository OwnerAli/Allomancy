package me.alii.domain.abilities.push_pull;

public class PushPullConfig {
    // Cushion
    public double cushionAbsorption = 0.8;   // fraction of fall speed absorbed
    public double relaunchFraction = 0.6;    // fraction converted to relaunch
    public int cushionCooldownTicks = 20;    // ticks before reset

    // Pull
    public double pullForceBase = 0.05;      // base pull force per tick
    public double maxPullVelocity = 5.0;     // velocity cap during pull
}