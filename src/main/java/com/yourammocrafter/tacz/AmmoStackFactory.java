package com.yourammocrafter.tacz;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public final class AmmoStackFactory {
    private AmmoStackFactory() {
    }

    public static ItemStack create(AmmoTemplateData template, int count) {
        if (template == null || !template.isValid() || count <= 0) {
            return ItemStack.EMPTY;
        }

        Item item = BuiltInRegistries.ITEM.get(template.ammoItemId());
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(item, count);
        CompoundTag customData = new CompoundTag();
        customData.putString(AmmoTemplateData.AMMO_ID_TAG, template.ammoId().toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
        return stack;
    }
}
