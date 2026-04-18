package me.alii.domain.packages;

import java.util.Random;

public record DeliveryPackage(PackageRarity packageRarity) {
    private static final Random random = new Random();

    public static DeliveryPackage createPackage() {
        int randomRarity = random.nextInt(PackageRarity.VALUES.length);
        return new DeliveryPackage(PackageRarity.VALUES[randomRarity]);
    }
}
