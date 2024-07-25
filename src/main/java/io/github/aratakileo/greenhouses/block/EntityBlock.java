package io.github.aratakileo.greenhouses.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class EntityBlock extends BaseEntityBlock {
    protected EntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(
            @NotNull BlockState oldState,
            @NotNull Level level,
            @NotNull BlockPos blockPos,
            @NotNull BlockState newState,
            boolean moved
    ) {
        Containers.dropContentsOnDestroy(oldState, newState, level, blockPos);
        super.onRemove(oldState, level, blockPos, newState, moved);
    }
}
