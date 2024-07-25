package io.github.aratakileo.greenhouses.block.entity;

import io.github.aratakileo.greenhouses.block.Blocks;
import io.github.aratakileo.greenhouses.container.GreenhouseScreenContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GreenhouseBlockEntity extends ContainerBlockEntity {
    private final ContainerData data;

    public final static int GROUND_INPUT = 0,
            WATER_INPUT = 1,
            PLANT_INPUT = 2,
            INPUT_SLOTS = 3,
            OUTPUT_SLOTS = INPUT_SLOTS,
            TOTAL_SLOTS = INPUT_SLOTS + OUTPUT_SLOTS,
            MAX_PROGRESS = 100;


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

        if (isGroundWet == 0 && getWaterInputStack().is(Items.WATER_BUCKET)) {
            setItem(WATER_INPUT, new ItemStack(Items.BUCKET, 1));
            isGroundWet = 1;
        }

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

    private void addToOutputStacks(@NotNull List<ItemStack> itemStacks) {
        for (var outputStackIndex = INPUT_SLOTS; outputStackIndex < TOTAL_SLOTS; outputStackIndex++)
            for (final var itemStack: itemStacks) {
                final var outputStack = getItem(outputStackIndex);
                if (!itemStack.is(outputStack.getItem()) && !outputStack.isEmpty()) continue;

                setItem(
                        outputStackIndex,
                        new ItemStack(
                                itemStack.getItem(),
                                outputStack.getCount() + itemStack.getCount()
                        )
                );

                break;
            }
    }

    public @NotNull ItemStack getGroundInputStack() {
        return getItem(GROUND_INPUT);
    }

    public @NotNull ItemStack getWaterInputStack() {
        return getItem(WATER_INPUT);
    }

    public @NotNull ItemStack getPlantInputStack() {
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

        for (final var itemStack: itemStacks)
            for (final var outputStack: getOutputStacks())
                if (
                        outputStack.is(itemStack.getItem())
                                && outputStack.getCount() + itemStack.getCount() <= outputStack.getMaxStackSize()
                                || outputStack.isEmpty()
                ) slotsScore++;

        return slotsScore >= itemStacks.size();
    }
}
