package com.yourammocrafter.registry;

import com.yourammocrafter.YourAmmoCrafterMod;
import com.yourammocrafter.block.AmmoCrafterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(YourAmmoCrafterMod.MOD_ID);

    public static final DeferredBlock<AmmoCrafterBlock> AMMO_CRAFTER = BLOCKS.register(
            "ammo_crafter",
            () -> new AmmoCrafterBlock(BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL))
    );

    private ModBlocks() {
    }
}
