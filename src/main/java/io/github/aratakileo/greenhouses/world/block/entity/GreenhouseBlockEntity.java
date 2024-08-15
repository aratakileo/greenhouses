package io.github.aratakileo.greenhouses.world.block.entity;

import io.github.aratakileo.elegantia.world.container.AbstractContainerBlockEntity;
import io.github.aratakileo.greenhouses.world.block.ModBlocks;
import io.github.aratakileo.greenhouses.world.container.GreenhouseContainerMenu;
import io.github.aratakileo.greenhouses.world.recipe.GreenhouseRecipe;
import io.github.aratakileo.greenhouses.world.recipe.RecipeTypes;
import io.github.aratakileo.greenhouses.world.recipe.GreenhouseRecipeInput;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

public class GreenhouseBlockEntity extends AbstractContainerBlockEntity {
    @CompoundDataField
    private final GreenhouseUtil.GreenhouseContainerData data = new GreenhouseUtil.GreenhouseContainerData();

    public GreenhouseBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super(
                BlockEntityTypes.GREENHOUSE_BLOCK_ENTITY_TYPE,
                blockPos,
                blockState,
                ModBlocks.GREENHOUSE,
                GreenhouseUtil.TOTAL_SLOTS
        );
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inventory) {
        return new GreenhouseContainerMenu(inventory, this, data, syncId);
    }

    @Override
    public void setChanged() {
        assert level != null;

        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        super.setChanged();
    }

    public void tick(@NotNull Level lvl, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        if (lvl.isClientSide) return;

        if (!canInsertItemIntoOutputStacks()) {
            data.interruptProgress(GreenhouseUtil.GrowFail.NOT_ENOUGH_OUTPUT_SPACE);
            setChanged();
            return;
        }

        if (getGroundItemStack().isEmpty() || getPlantItemStack().isEmpty()) {
            data.interruptProgress(GreenhouseUtil.GrowFail.NONE);
            setChanged();
            return;
        }

        final var recipeOptional = getCurrentRecipe(false);

        if (recipeOptional.isEmpty()) {
            if (getCurrentRecipe(true).isEmpty()) {
                data.interruptProgress(GreenhouseUtil.GrowFail.INVALID_RECIPE);
                setChanged();
                return;
            }

            data.interruptProgress(hasWater()
                    ? GreenhouseUtil.GrowFail.DOES_NOT_NEED_WATER
                    : GreenhouseUtil.GrowFail.NEEDS_WATER
            );
            setChanged();

            return;
        }

        final var recipe = recipeOptional.orElseThrow();

        if (!canInsertItemIntoOutputStacks(recipe.value().getResultItems())) {
            data.interruptProgress(GreenhouseUtil.GrowFail.NOT_ENOUGH_OUTPUT_SPACE);
            setChanged();
            return;
        }

        data.progress++;
        data.maxProgress = recipeOptional.orElseThrow().value().getDuration();
        data.growFail = GreenhouseUtil.GrowFail.NONE;

        if (data.progress > data.maxProgress) {
            addToOutputStacks(recipeOptional.orElseThrow().value().getResultItems());
            data.progress = 0;
        }

        setChanged();
    }

    public boolean hasWater() {
        return data.hasWater;
    }

    public float getProgressScale() {
        return data.getProgressScale();
    }

    public boolean growFailedByInvalidRecipe() {
        return data.growFail == GreenhouseUtil.GrowFail.INVALID_RECIPE;
    }

    private @NotNull Optional<RecipeHolder<GreenhouseRecipe>> getCurrentRecipe(boolean invertWaterAvailability) {
        return Objects.requireNonNull(getLevel()).getRecipeManager().getRecipeFor(
                RecipeTypes.GREENHOUSE_RECIPE_TYPE,
                new GreenhouseRecipeInput(
                        getPlantItemStack(),
                        getGroundItemStack(),
                        invertWaterAvailability != hasWater()
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

    public @NotNull ItemStack getGroundItemStack() {
        return getItem(GreenhouseUtil.GROUND_INPUT_SLOT);
    }

    public @NotNull ItemStack getPlantItemStack() {
        return getItem(GreenhouseUtil.PLANT_INPUT_SLOT);
    }

    private @NotNull List<@NotNull ItemStack> getOutputStacks() {
        return getItems().stream().skip(GreenhouseUtil.INPUT_SLOTS).toList();
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
    public int @NotNull[] getSlotsForFace(@NotNull Direction direction) {
        if (direction == Direction.DOWN) {
            final var slots = new int[GreenhouseUtil.OUTPUT_SLOTS];

            for (var i = 0; i < GreenhouseUtil.OUTPUT_SLOTS; i++)
                slots[i] = i + GreenhouseUtil.INPUT_SLOTS;

            return slots;
        }

        return new int[0];
    }

    @Override
    public boolean canTakeItemThroughFace(int slotIndex, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return true;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotIndex, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithFullMetadata(provider);
    }
}
