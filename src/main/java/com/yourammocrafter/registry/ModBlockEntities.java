package com.yourammocrafter.registry;

import com.yourammocrafter.YourAmmoCrafterMod;
import com.yourammocrafter.blockentity.AmmoCrafterBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, YourAmmoCrafterMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AmmoCrafterBlockEntity>> AMMO_CRAFTER =
            BLOCK_ENTITY_TYPES.register(
                    "ammo_crafter",
                    () -> BlockEntityType.Builder.of(AmmoCrafterBlockEntity::new, ModBlocks.AMMO_CRAFTER.get()).build(null)
            );

    private ModBlockEntities() {
    }
}
