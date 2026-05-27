package com.yourammocrafter.tacz;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Objects;
import java.util.Optional;

public record AmmoTemplateData(ResourceLocation ammoItemId, ResourceLocation ammoId) {
    public static final ResourceLocation TACZ_AMMO_ITEM_ID = ResourceLocation.fromNamespaceAndPath("tacz", "ammo");
    public static final String BLOCK_ENTITY_TAG = "AmmoTemplate";
    public static final String AMMO_ITEM_ID_TAG = "AmmoItemId";
    public static final String AMMO_ID_TAG = "AmmoId";

    public AmmoTemplateData {
        Objects.requireNonNull(ammoItemId, "ammoItemId");
        Objects.requireNonNull(ammoId, "ammoId");
    }

    public static Optional<AmmoTemplateData> fromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!TACZ_AMMO_ITEM_ID.equals(itemId)) {
            return Optional.empty();
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return Optional.empty();
        }

        CompoundTag tag = customData.copyTag();
        if (!tag.contains(AMMO_ID_TAG, Tag.TAG_STRING)) {
            return Optional.empty();
        }

        ResourceLocation ammoId = parseResourceLocation(tag.getString(AMMO_ID_TAG)).orElse(null);
        if (ammoId == null) {
            return Optional.empty();
        }

        return Optional.of(new AmmoTemplateData(itemId, ammoId));
    }

    public ItemStack createStack(int count) {
        return AmmoStackFactory.create(this, count);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString(AMMO_ITEM_ID_TAG, this.ammoItemId.toString());
        tag.putString(AMMO_ID_TAG, this.ammoId.toString());
        return tag;
    }

    public static Optional<AmmoTemplateData> load(CompoundTag tag) {
        if (tag == null) {
            return Optional.empty();
        }
        if (!tag.contains(AMMO_ITEM_ID_TAG, Tag.TAG_STRING) || !tag.contains(AMMO_ID_TAG, Tag.TAG_STRING)) {
            return Optional.empty();
        }

        Optional<ResourceLocation> ammoItemId = parseResourceLocation(tag.getString(AMMO_ITEM_ID_TAG));
        Optional<ResourceLocation> ammoId = parseResourceLocation(tag.getString(AMMO_ID_TAG));
        if (ammoItemId.isEmpty() || ammoId.isEmpty()) {
            return Optional.empty();
        }

        AmmoTemplateData data = new AmmoTemplateData(ammoItemId.get(), ammoId.get());
        return data.isValid() ? Optional.of(data) : Optional.empty();
    }

    public boolean isValid() {
        return TACZ_AMMO_ITEM_ID.equals(this.ammoItemId);
    }

    private static Optional<ResourceLocation> parseResourceLocation(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ResourceLocation.tryParse(value));
    }
}
