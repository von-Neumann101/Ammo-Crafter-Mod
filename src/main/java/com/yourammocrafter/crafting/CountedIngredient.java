package com.yourammocrafter.crafting;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public record CountedIngredient(Ingredient ingredient, int count) {
    public CountedIngredient {
        Objects.requireNonNull(ingredient, "ingredient");
        if (ingredient.isEmpty()) {
            throw new IllegalArgumentException("ingredient must not be empty");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
    }
}
