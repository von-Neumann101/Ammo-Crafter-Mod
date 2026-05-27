package com.yourammocrafter.block;

import com.mojang.serialization.MapCodec;
import com.yourammocrafter.blockentity.AmmoCrafterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AmmoCrafterBlock extends BaseEntityBlock {
    public static final MapCodec<AmmoCrafterBlock> CODEC = simpleCodec(AmmoCrafterBlock::new);

    public AmmoCrafterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<AmmoCrafterBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AmmoCrafterBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AmmoCrafterBlockEntity ammoCrafterBlockEntity && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(ammoCrafterBlockEntity, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
