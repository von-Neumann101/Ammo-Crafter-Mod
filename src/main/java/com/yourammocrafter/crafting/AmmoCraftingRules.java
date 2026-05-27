package com.yourammocrafter.crafting;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public final class AmmoCraftingRules {
    private static volatile Map<ResourceLocation, AmmoCraftingRule> rules = Map.of();

    private AmmoCraftingRules() {
    }

    public static Optional<AmmoCraftingRule> find(ResourceLocation ammoId) {
        if (ammoId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(rules.get(ammoId));
    }

    public static void replaceAll(Map<ResourceLocation, AmmoCraftingRule> loadedRules) {
        rules = Map.copyOf(loadedRules);
    }
}
