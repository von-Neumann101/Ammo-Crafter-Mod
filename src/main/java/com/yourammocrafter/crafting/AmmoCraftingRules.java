package com.yourammocrafter.crafting;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
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

    public static int count() {
        return rules.size();
    }

    public static List<String> ammoIds() {
        return rules.keySet().stream()
                .map(ResourceLocation::toString)
                .sorted()
                .toList();
    }

    public static void replaceAll(Map<ResourceLocation, AmmoCraftingRule> loadedRules) {
        rules = Map.copyOf(loadedRules);
    }
}
