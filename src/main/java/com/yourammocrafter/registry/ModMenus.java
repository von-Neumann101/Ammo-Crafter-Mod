package com.yourammocrafter.registry;

import com.yourammocrafter.YourAmmoCrafterMod;
import com.yourammocrafter.menu.AmmoCrafterMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, YourAmmoCrafterMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AmmoCrafterMenu>> AMMO_CRAFTER =
            MENU_TYPES.register(
                    "ammo_crafter",
                    () -> new MenuType<>(
                            (IContainerFactory<AmmoCrafterMenu>) (containerId, playerInventory, buffer) ->
                                    new AmmoCrafterMenu(containerId, playerInventory, buffer.readBlockPos()),
                            FeatureFlags.DEFAULT_FLAGS
                    )
            );

    private ModMenus() {
    }
}
