package com.yourammocrafter.crafting;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.yourammocrafter.YourAmmoCrafterMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmmoCraftingRuleReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "ammo_crafting";

    public AmmoCraftingRuleReloadListener() {
        super(GSON, DIRECTORY);
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new AmmoCraftingRuleReloadListener());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElements, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, AmmoCraftingRule> loadedRules = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonElements.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            if (!YourAmmoCrafterMod.MOD_ID.equals(fileId.getNamespace())) {
                continue;
            }

            try {
                AmmoCraftingRule rule = parseRule(entry.getValue());
                AmmoCraftingRule previous = loadedRules.put(rule.ammoId(), rule);
                if (previous != null) {
                    LOGGER.warn("Duplicate ammo crafting rule for ammo id {}; {} overrides an earlier rule", rule.ammoId(), fileId);
                }
            } catch (RuntimeException exception) {
                LOGGER.error("Failed to parse ammo crafting rule {}", fileId, exception);
            }
        }

        AmmoCraftingRules.replaceAll(loadedRules);
        LOGGER.info("Loaded {} ammo crafting rules: {}", loadedRules.size(), AmmoCraftingRules.ammoIds());
    }

    private static AmmoCraftingRule parseRule(JsonElement element) {
        JsonObject object = GsonHelper.convertToJsonObject(element, "ammo crafting rule");
        ResourceLocation ammoId = parseResourceLocation(GsonHelper.getAsString(object, "ammo_id"), "ammo_id");
        int outputCount = GsonHelper.getAsInt(object, "output_count");
        JsonArray ingredientArray = GsonHelper.getAsJsonArray(object, "ingredients");
        List<CountedIngredient> ingredients = new ArrayList<>();

        for (JsonElement ingredientElement : ingredientArray) {
            JsonObject ingredientObject = GsonHelper.convertToJsonObject(ingredientElement, "ingredient");
            int count = GsonHelper.getAsInt(ingredientObject, "count");
            JsonObject ingredientData = ingredientObject.deepCopy();
            ingredientData.remove("count");
            Ingredient ingredient = Ingredient.CODEC_NONEMPTY
                    .parse(JsonOps.INSTANCE, ingredientData)
                    .getOrThrow(JsonParseException::new);
            ingredients.add(new CountedIngredient(ingredient, count));
        }

        return new AmmoCraftingRule(ammoId, outputCount, ingredients);
    }

    private static ResourceLocation parseResourceLocation(String value, String fieldName) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(value);
        if (resourceLocation == null) {
            throw new JsonParseException("Invalid ResourceLocation in " + fieldName + ": " + value);
        }
        return resourceLocation;
    }
}
