package io.github.aratakileo.greenhouses.world.block.entity;

import io.github.aratakileo.elegantia.world.container.AbstractContainerBlockEntity;
import io.github.aratakileo.greenhouses.world.block.CokeFurnaceBlock;
import io.github.aratakileo.greenhouses.world.block.ModBlocks;
import io.github.aratakileo.greenhouses.world.item.recipe.CokeFurnaceRecipe;
import io.github.aratakileo.greenhouses.world.item.recipe.RecipeTypes;
import io.github.aratakileo.greenhouses.world.container.CokeFurnaceContainerMenu;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class CokeFurnaceBlockEntity extends AbstractContainerBlockEntity {
    @CompoundDataField
    private final CokeFurnaceUtil.CokeFurnaceContainerData data = new CokeFurnaceUtil.CokeFurnaceContainerData();

    public CokeFurnaceBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super(
                BlockEntityTypes.COKE_FURNACE_BLOCK_ENTITY_TYPE,
                blockPos,
                blockState,
                ModBlocks.COKE_FURNACE,
                CokeFurnaceUtil.TOTAL_SLOTS
        );
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inventory) {
        return new CokeFurnaceContainerMenu(inventory, this, data, syncId);
    }

    public @NotNull ItemStack getIngredientItemStack() {
        return getItem(CokeFurnaceUtil.INGREDIENT_SLOT);
    }

    public @NotNull ItemStack getResultItemStack() {
        return getItem(CokeFurnaceUtil.RESULT_SLOT);
    }

    public void tick(@NotNull Level lvl, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        if (lvl.isClientSide) return;

        if (getIngredientItemStack().isEmpty() || !canInsertResult()) {
            data.progress = 0;
            applyState(blockState);
            return;
        }

        final var optionalRecipe = getCurrentRecipe();

        if (optionalRecipe.isEmpty()) {
            data.progress = 0;
            applyState(blockState);
            return;
        }

        final var recipe = optionalRecipe.orElseThrow().value();
        final var result = recipe.getResult();

        if (!canInsertResult(result, recipe.getProducedCreosote())) {
            data.progress = 0;
            applyState(blockState);
            return;
        }

        data.progress++;
        data.maxProgress = recipe.getDuration();

        applyState(blockState);

        if (data.progress > data.maxProgress) {
            setItem(CokeFurnaceUtil.RESULT_SLOT, new ItemStack(
                    result.getItem(),
                    getResultItemStack().getCount() + result.getCount()
            ));
            getItem(CokeFurnaceUtil.INGREDIENT_SLOT).shrink(1);
            data.producedCreosote += recipe.getProducedCreosote();
            data.progress = 0;
        }
    }

    private void applyState(@NotNull BlockState blockState) {
        blockState = blockState.setValue(CokeFurnaceBlock.LIT, data.progress > 0);
        assert level != null;
        level.setBlock(worldPosition, blockState, 3);
        setChanged();
    }

    private @NotNull Optional<RecipeHolder<CokeFurnaceRecipe>> getCurrentRecipe() {
        return Objects.requireNonNull(getLevel()).getRecipeManager().getRecipeFor(
                RecipeTypes.COKE_FURNACE_RECIPE_TYPE,
                new SingleRecipeInput(getIngredientItemStack()),
                getLevel()
        );
    }

    private boolean canInsertResult() {
        if (data.producedCreosote >= CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE)
            return false;

        final var resultItemStack = getResultItemStack();

        return resultItemStack.isEmpty() || resultItemStack.getCount() < resultItemStack.getMaxStackSize();
    }

    private boolean canInsertResult(@NotNull ItemStack itemStack, int producedCreosote) {
        if (producedCreosote + data.producedCreosote > CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE)
            return false;

        final var resultItemStack = getResultItemStack();

        return resultItemStack.isEmpty() || resultItemStack.is(itemStack.getItem())
                && resultItemStack.getCount() + itemStack.getCount() <= resultItemStack.getMaxStackSize();
    }

    @Override
    public int @NotNull[] getSlotsForFace(@NotNull Direction direction) {
        if (direction == Direction.UP) return new int[] {CokeFurnaceUtil.INGREDIENT_SLOT};
        if (direction == Direction.DOWN) return new int[] {CokeFurnaceUtil.RESULT_SLOT};

        return new int[0];
    }

    @Override
    public boolean canTakeItemThroughFace(int slotIndex, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return slotIndex == CokeFurnaceUtil.RESULT_SLOT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotIndex, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return slotIndex == CokeFurnaceUtil.INGREDIENT_SLOT && CokeFurnaceUtil.isIngredient(itemStack);
    }
}
