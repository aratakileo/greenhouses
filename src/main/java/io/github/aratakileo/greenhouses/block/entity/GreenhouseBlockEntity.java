package io.github.aratakileo.greenhouses.block.entity;

import io.github.aratakileo.greenhouses.block.Blocks;
import io.github.aratakileo.greenhouses.container.GreenhouseScreenContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GreenhouseBlockEntity extends ContainerBlockEntity {
    public final static int GROUND_INPUT = 0,
            WATER_INPUT = 1,
            PLANT_INPUT = 2,
            INPUT_SLOTS = 3,
            OUTPUT_SLOTS = INPUT_SLOTS,
            TOTAL_SLOTS = INPUT_SLOTS + OUTPUT_SLOTS,
            MAX_PROGRESS = 100;

    private final ContainerData data;

    @CompoundDataField
    protected int progress = 0;
    @CompoundDataField
    protected int isGroundWet = 0;

    private final static List<ItemStack> RECIPE_OUTPUT = List.of(
            new ItemStack(Items.OAK_SAPLING, 3),
            new ItemStack(Items.OAK_LOG, 5),
            new ItemStack(Items.STICK, 5)
    );

    public GreenhouseBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super(BlockEntities.GREENHOUSE_BLOCK_ENTITY_TYPE, blockPos, blockState, Blocks.GREENHOUSE, 6);

        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch(i) {
                    case 0 -> progress;
                    case 1 -> isGroundWet;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch(i) {
                    case 0 -> progress = value;
                    case 1 -> isGroundWet = value;
                };
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inventory) {
        return new GreenhouseScreenContainer(syncId, inventory, this, data);
    }

    public void tick(@NotNull Level lvl, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        if (lvl.isClientSide) return;

        if (!canInsertItemIntoOutputStacks(OUTPUT_SLOTS)) {
            progress = 0;
            setChanged(lvl, blockPos, blockState);
            return;
        }

        if (!hasRecipe()) {
            progress = 0;
            return;
        }

        progress++;
        setChanged(lvl, blockPos, blockState);

        if (progress > MAX_PROGRESS) {
            addToOutputStacks(RECIPE_OUTPUT);
            progress = 0;
        }
    }

    public boolean isGroundWet() {
        return isGroundWet == 1;
    }

    private void addToOutputStacks(@NotNull List<ItemStack> itemStacks) {
        for (final var itemStack: itemStacks) {
            var firstEmptyStackIndex = -1;

            for (var i = INPUT_SLOTS; i < TOTAL_SLOTS; i++) {
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
        return getItem(GROUND_INPUT);
    }

    private @NotNull ItemStack getWaterInputStack() {
        return getItem(WATER_INPUT);
    }

    private @NotNull ItemStack getPlantInputStack() {
        return getItem(PLANT_INPUT);
    }

    private @NotNull List<@NotNull ItemStack> getOutputStacks() {
        return getItems().stream().skip(INPUT_SLOTS).toList();
    }

    private boolean hasRecipe() {
        return isGroundWet == 1 && getGroundInputStack().is(Items.DIRT) && getPlantInputStack().is(Items.OAK_SAPLING) && canInsertItemIntoOutputStacks(RECIPE_OUTPUT);
    }

    private boolean canInsertItemIntoOutputStacks(int slots) {
        int slotsScore = 0;

        for (final var outputStack: getOutputStacks())
            if (outputStack.getCount() < outputStack.getMaxStackSize() || outputStack.isEmpty())
                slotsScore++;

        return slotsScore >= slots;
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
}
