package io.github.aratakileo.greenhouses.screen.container;

import io.github.aratakileo.greenhouses.screen.container.slot.*;
import io.github.aratakileo.greenhouses.screen.container.slot.ResultSlot;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class GreenhouseContainerMenu extends SimpleContainerMenu<GreenhouseUtil.GreenhouseContainerData> {
    public GreenhouseContainerMenu(int syncId, @NotNull Inventory inventory) {
        this(
                inventory,
                new SimpleContainer(GreenhouseUtil.TOTAL_SLOTS),
                new GreenhouseUtil.GreenhouseContainerData(),
                syncId
        );
    }

    public GreenhouseContainerMenu(
            @NotNull Inventory playerInventory,
            @NotNull Container container,
            @NotNull GreenhouseUtil.GreenhouseContainerData data,
            int syncId
    ) {
        super(ContainerMenus.GREENHOUSE_CONTAINER_MENU, container, data, syncId);

        checkContainerSize(container, GreenhouseUtil.TOTAL_SLOTS);
        checkContainerDataCount(data, GreenhouseUtil.CONTAINER_DATA_SIZE);

        addSlot(new SingleItemFilteredSlot(
                container,
                GreenhouseUtil::isPlant,
                GreenhouseUtil.PLANT_INPUT_SLOT,
                GreenhouseUtil.PLANT_SLOT_X_OFFSET,
                GreenhouseUtil.PLANT_SLOT_Y_OFFSET
        ));

        addSlot(new GroundSlot(container, GreenhouseUtil.GROUND_INPUT_SLOT, 70, 45));

        final var FLUID_SLOT_CONTROLLER = new FluidSlotController.Builder(
                Items.WATER_BUCKET
        ).setMayTakeFluid(this::isGroundWet)
                .setOnTakeFluid(() -> data.hasWater = false)
                .setOnInsertFluid(() -> data.hasWater = true)
                .build();

        addSlot(new FluidSlot(
                FLUID_SLOT_CONTROLLER,
                container,
                GreenhouseUtil.WATER_INPUT_SLOT,
                GreenhouseUtil.WATER_SLOT_X_OFFSET,
                GreenhouseUtil.WATER_SLOT_Y_OFFSET
        ));

        for (var i = GreenhouseUtil.INPUT_SLOTS; i < GreenhouseUtil.TOTAL_SLOTS; i++) {
            final var localIndex = i - GreenhouseUtil.INPUT_SLOTS;
            addSlot(new ResultSlot(container, i, 124, 17 * (localIndex + 1) + localIndex));
        }

        addPlayerInventorySlots(playerInventory);
        addPlayerHotbarSlots(playerInventory);

        addDataSlots(data);
    }

    @Override
    public void clicked(int slotIndex, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotIndex == GreenhouseUtil.GROUND_INPUT_SLOT && getSlot(slotIndex) instanceof GroundSlot slot) {
            if (clickType == ClickType.PICKUP) {
                if (!getCarried().isEmpty() && slot.mayUseHoeOn(getCarried())) {
                    slot.useHoe(getCarried());
                    return;
                }

                if (slot.shouldPrepareBeforePickup())
                    slot.prepareBeforePickup();
            }

            if (clickType == ClickType.QUICK_MOVE && slot.shouldPrepareBeforePickup())
                slot.prepareBeforePickup();
        }

        if (slotIndex >= GreenhouseUtil.TOTAL_SLOTS && clickType == ClickType.QUICK_MOVE) {
            final var sourceSlot = slots.get(slotIndex);

            if (!sourceSlot.hasItem() || !sourceSlot.mayPickup(player)) return;

            final var sourceSlotItem = sourceSlot.getItem();

            for (var greenhouseSlotIndex = 0; greenhouseSlotIndex < GreenhouseUtil.TOTAL_SLOTS; greenhouseSlotIndex++) {
                final var currentSlot = getSlot(greenhouseSlotIndex);

                if (!currentSlot.mayPlace(sourceSlotItem)) continue;

                final var remainingSlotStack = currentSlot.safeInsert(sourceSlotItem);
                sourceSlot.set(remainingSlotStack);
                sourceSlot.setChanged();
                return;
            }
            return;
        }

        super.clicked(slotIndex, button, clickType, player);
    }

    public boolean isGroundWet() {
        return data.hasWater;
    }

    public float getProgress() {
        return (float) data.progress / (float) data.maxProgress;
    }

    public int getFailCode() {
        return data.failCode;
    }

    public boolean isInvalidRecipe() {
        return data.failCode >= GreenhouseUtil.INVALID_RECIPE_CODE;
    }

    public boolean isBucketSlotEmpty() {
        return !getSlot(GreenhouseUtil.WATER_INPUT_SLOT).hasItem();
    }

    public boolean isPlantSlotEmpty() {
        return !getSlot(GreenhouseUtil.PLANT_INPUT_SLOT).hasItem();
    }

    public boolean isGroundSlotEmpty() {
        return !getSlot(GreenhouseUtil.GROUND_INPUT_SLOT).hasItem();
    }
}
