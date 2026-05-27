package com.yourammocrafter.crafting;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

public record AmmoCraftingRule(ResourceLocation ammoId, int outputCount, List<CountedIngredient> ingredients) {
    public AmmoCraftingRule {
        Objects.requireNonNull(ammoId, "ammoId");
        Objects.requireNonNull(ingredients, "ingredients");
        if (outputCount <= 0) {
            throw new IllegalArgumentException("outputCount must be positive");
        }
        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("ingredients must not be empty");
        }

        ingredients = List.copyOf(ingredients);
    }
}
