package com.yourammocrafter;

import com.yourammocrafter.registry.ModBlockEntities;
import com.yourammocrafter.registry.ModBlocks;
import com.yourammocrafter.registry.ModItems;
import com.yourammocrafter.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(YourAmmoCrafterMod.MOD_ID)
public final class YourAmmoCrafterMod {
    public static final String MOD_ID = "yourammocrafter";

    public YourAmmoCrafterMod(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenus.MENU_TYPES.register(modEventBus);
    }
}
