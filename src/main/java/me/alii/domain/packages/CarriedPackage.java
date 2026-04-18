package me.alii.domain.packages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3d;

import java.util.Random;

@Getter
@Setter
@RequiredArgsConstructor
public class CarriedPackage {
    private static final Random random = new Random();

    private final DeliveryPackage parentDeliveryPackage;
    private final Vector3d pickupPosition;

    // Can be updated multiple times to account for changing destination
    private Vector3d destination;

    public void pickup() {
    }

    public static CarriedPackage createRandomPackage(DeliveryPackage parentDeliveryPackage, Vector3d pickupPosition) {
        return new CarriedPackage(parentDeliveryPackage, pickupPosition);
    }
}
