package io.github.aratakileo.greenhouses.world.block;

import com.mojang.serialization.MapCodec;
import io.github.aratakileo.greenhouses.world.block.entity.BlockEntityTypes;
import io.github.aratakileo.greenhouses.world.block.entity.GreenhouseBlockEntity;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import io.github.aratakileo.greenhouses.world.container.GroundSlotController;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GreenhouseBlock extends BaseEntityBlock {
    private final static MapCodec<GreenhouseBlock> CODEC = simpleCodec(GreenhouseBlock::new);

    protected GreenhouseBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GreenhouseBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState blockState,
            @NotNull Level level,
            @NotNull BlockPos blockPos,
            @NotNull Player player,
            @NotNull BlockHitResult blockHitResult
    ) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        player.openMenu(blockState.getMenuProvider(level, blockPos));
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(
                blockEntityType,
                BlockEntityTypes.GREENHOUSE_BLOCK_ENTITY_TYPE,
                ((lvl, blockPos, _blockState, blockEntity) -> blockEntity.tick(lvl, blockPos, _blockState))
        );
    }

    @Override
    protected void onRemove(
            @NotNull BlockState oldState,
            @NotNull Level level,
            @NotNull BlockPos blockPos,
            @NotNull BlockState newState,
            boolean moved
    ) {
        if (level.getBlockEntity(blockPos) instanceof GreenhouseBlockEntity blockEntity && blockEntity.hasWater())
            level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 2);

        if (level.getBlockEntity(blockPos) instanceof GreenhouseBlockEntity blockEntity) {
            final var itemStack = blockEntity.getItem(GreenhouseUtil.GROUND_INPUT_SLOT);

            GroundSlotController.prepareBeforePickup(
                    itemStack,
                    item -> blockEntity.setItem(GreenhouseUtil.GROUND_INPUT_SLOT, item)
            );
        }

        Containers.dropContentsOnDestroy(oldState, newState, level, blockPos);
        super.onRemove(oldState, level, blockPos, newState, moved);
    }
}
