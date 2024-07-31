package io.github.aratakileo.greenhouses.block;

import com.mojang.serialization.MapCodec;
import io.github.aratakileo.greenhouses.block.entity.BlockEntitiTypes;
import io.github.aratakileo.greenhouses.block.entity.CokeFurnaceBlockEntity;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CokeFurnaceBlock extends EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private final static MapCodec<CokeFurnaceBlock> CODEC = simpleCodec(CokeFurnaceBlock::new);

    protected CokeFurnaceBlock(@NotNull Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new CokeFurnaceBlockEntity(blockPos, blockState);
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

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull BlockState rotate(@NotNull BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(@NotNull BlockState blockState, @NotNull Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @NotNull Level level,
            @NotNull BlockState blockState,
            @NotNull BlockEntityType<T> blockEntityType
    ) {
        CokeFurnaceUtil.init(level);

        return createTickerHelper(
                blockEntityType,
                BlockEntitiTypes.COKE_FURNACE_BLOCK_ENTITY_TYPE,
                ((lvl, blockPos, _blockState, blockEntity) -> blockEntity.tick(lvl, blockPos, _blockState))
        );
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (!blockState.getValue(LIT)) return;

        final var x = (double) blockPos.getX() + 0.5;
        final var y = (double) blockPos.getY();
        final var z = (double) blockPos.getZ() + 0.5;

        if (randomSource.nextDouble() < 0.1)
            level.playLocalSound(x, y, z, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0f, 1.0f, false);

        level.addParticle(ParticleTypes.SMOKE, x, y + 1.1, z, 0.0, 0.0, 0.0);
    }
}
