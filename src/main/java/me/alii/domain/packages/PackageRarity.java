package me.alii.domain.packages;

import lombok.Getter;

@Getter
public enum PackageRarity {
    COMMON("Common_Package"),
    UNCOMMON("Uncommon_Package"),
    RARE("Rare_Package");

    public static final PackageRarity[] VALUES = values();

    private final String itemId;

    PackageRarity(String itemId) {
        this.itemId = itemId;
    }
}
