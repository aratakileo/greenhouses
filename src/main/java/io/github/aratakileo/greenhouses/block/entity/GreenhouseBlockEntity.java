package io.github.aratakileo.greenhouses.block.entity;

import io.github.aratakileo.greenhouses.block.Blocks;
import io.github.aratakileo.greenhouses.container.GreenhouseScreenContainer;
import io.github.aratakileo.greenhouses.recipe.GreenhouseRecipe;
import io.github.aratakileo.greenhouses.recipe.RecipeTypes;
import io.github.aratakileo.greenhouses.recipe.GreenhouseRecipeInput;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GreenhouseBlockEntity extends ContainerBlockEntity {
    private final ContainerData data;

    @CompoundDataField
    protected int progress = 0;
    @CompoundDataField
    protected int maxProgress = 1;
    @CompoundDataField
    protected int isGroundWet = 0;
    @CompoundDataField
    protected int failCode = GreenhouseUtil.NO_FAILS_CODE;

    public GreenhouseBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super(
                BlockEntities.GREENHOUSE_BLOCK_ENTITY_TYPE,
                blockPos,
                blockState,
                Blocks.GREENHOUSE,
                GreenhouseUtil.TOTAL_SLOTS,
                GreenhouseUtil.INPUT_SLOTS
        );

        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch(i) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> isGroundWet;
                    case 3 -> failCode;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch(i) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> isGroundWet = value;
                    case 3 -> failCode = value;
                };
            }

            @Override
            public int getCount() {
                return GreenhouseUtil.CONTAINER_DATA_SIZE;
            }
        };
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inventory) {
        return new GreenhouseScreenContainer(syncId, inventory, this, data);
    }

    public void tick(@NotNull Level lvl, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        if (lvl.isClientSide) return;

        if (!canInsertItemIntoOutputStacks()) {
            progress = 0;
            failCode = GreenhouseUtil.NOT_ENOUGH_OUTPUT_SPACE_CODE;
            setChanged(lvl, blockPos, blockState);
            return;
        }

        if (getGroundInputStack().isEmpty() || getPlantInputStack().isEmpty()) {
            progress = 0;
            failCode = GreenhouseUtil.NO_FAILS_CODE;
            setChanged(lvl, blockPos, blockState);
            return;
        }

        final var recipeOptional = getCurrentRecipe(false);

        if (recipeOptional.isEmpty()) {
            progress = 0;
            setChanged(lvl, blockPos, blockState);

            if (getCurrentRecipe(true).isEmpty()) {
                failCode = GreenhouseUtil.INVALID_RECIPE_CODE;
                return;
            }

            failCode = isGroundWet() ? GreenhouseUtil.DOES_NOT_NEED_WATER_CODE : GreenhouseUtil.NEEDS_WATER_CODE;
            return;
        }

        final var recipe = recipeOptional.orElseThrow();

        if (!canInsertItemIntoOutputStacks(recipe.value().getResultItems())) {
            progress = 0;
            failCode = GreenhouseUtil.NOT_ENOUGH_OUTPUT_SPACE_CODE;
            setChanged(lvl, blockPos, blockState);
            return;
        }

        progress++;
        maxProgress = recipeOptional.orElseThrow().value().getGrowthRate();
        failCode = GreenhouseUtil.NO_FAILS_CODE;

        setChanged(lvl, blockPos, blockState);

        if (progress > maxProgress) {
            addToOutputStacks(recipeOptional.orElseThrow().value().getResultItems());
            progress = 0;
        }
    }

    public boolean isGroundWet() {
        return isGroundWet == 1;
    }

    private @NotNull Optional<RecipeHolder<GreenhouseRecipe>> getCurrentRecipe(boolean invertWet) {
        return Objects.requireNonNull(getLevel()).getRecipeManager().getRecipeFor(
                RecipeTypes.GREENHOUSE_RECIPE_TYPE,
                new GreenhouseRecipeInput(
                        getPlantInputStack(),
                        getGroundInputStack(),
                        invertWet != isGroundWet()
                ),
                getLevel()
        );
    }

    private void addToOutputStacks(@NotNull List<ItemStack> itemStacks) {
        for (final var itemStack: itemStacks) {
            var firstEmptyStackIndex = -1;

            for (var i = GreenhouseUtil.INPUT_SLOTS; i < GreenhouseUtil.TOTAL_SLOTS; i++) {
                final var outputStack = getItem(i);

                if (outputStack.isEmpty() && firstEmptyStackIndex == -1) {
                    firstEmptyStackIndex = i;
                    continue;
                }

                if (outputStack.is(itemStack.getItem())) {
                    firstEmptyStackIndex = -1;
                    getItem(i).grow(itemStack.getCount());
                    break;
                }
            }

            if (firstEmptyStackIndex != -1) {
                setItem(firstEmptyStackIndex, itemStack.copy());
            }
        }
    }

    private @NotNull ItemStack getGroundInputStack() {
        return getItem(GreenhouseUtil.GROUND_INPUT_SLOT);
    }

    private @NotNull ItemStack getPlantInputStack() {
        return getItem(GreenhouseUtil.PLANT_INPUT_SLOT);
    }

    private @NotNull List<@NotNull ItemStack> getOutputStacks() {
        return getItems().stream().skip(GreenhouseUtil.INPUT_SLOTS).toList();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotIndex, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    private boolean canInsertItemIntoOutputStacks() {
        int slotsScore = 0;

        for (final var outputStack: getOutputStacks())
            if (outputStack.getCount() < outputStack.getMaxStackSize() || outputStack.isEmpty())
                slotsScore++;

        return slotsScore >= GreenhouseUtil.OUTPUT_SLOTS;
    }

    private boolean canInsertItemIntoOutputStacks(@NotNull List<ItemStack> itemStacks) {
        int slotsScore = 0;

        final var outputStacks = getOutputStacks();
        final var reservedOutputIndexes = new ArrayList<Integer>();

        for (final var itemStack: itemStacks) {
            var nearEmptyStackIndex = -1;

            for (var i = 0; i < outputStacks.size(); i++) {
                if (reservedOutputIndexes.contains(i)) continue;

                final var outputStack = outputStacks.get(i);

                if (outputStack.isEmpty()) {
                    nearEmptyStackIndex = i;
                    continue;
                }

                if (
                        outputStack.is(itemStack.getItem())
                                && outputStack.getCount() + itemStack.getCount() <= outputStack.getMaxStackSize()
                ) {
                    nearEmptyStackIndex = -1;
                    slotsScore++;
                    break;
                }
            }

            if (nearEmptyStackIndex != -1) {
                reservedOutputIndexes.add(nearEmptyStackIndex);
                slotsScore++;
            }
        }

        return slotsScore >= itemStacks.size();
    }

    @Override
    public boolean canTakeItemThroughFace(int slotIndex, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return super.canTakeItemThroughFace(slotIndex, itemStack, direction);
    }
}
