package com.yourammocrafter.registry;

import com.yourammocrafter.YourAmmoCrafterMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(YourAmmoCrafterMod.MOD_ID);

    public static final DeferredItem<BlockItem> AMMO_CRAFTER = ITEMS.register(
            "ammo_crafter",
            () -> new BlockItem(ModBlocks.AMMO_CRAFTER.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
