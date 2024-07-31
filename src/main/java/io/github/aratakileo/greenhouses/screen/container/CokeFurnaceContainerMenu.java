package io.github.aratakileo.greenhouses.screen.container;

import io.github.aratakileo.greenhouses.item.ModItems;
import io.github.aratakileo.greenhouses.screen.container.slot.FilteredSlot;
import io.github.aratakileo.greenhouses.screen.container.slot.FluidSlot;
import io.github.aratakileo.greenhouses.screen.container.slot.FluidSlotController;
import io.github.aratakileo.greenhouses.screen.container.slot.ResultSlot;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public class CokeFurnaceContainerMenu extends SimpleContainerMenu<CokeFurnaceUtil.CokeFurnaceContainerData> {
    public CokeFurnaceContainerMenu(int syncId, @NotNull Inventory playerInventory) {
        this(
                playerInventory,
                new SimpleContainer(CokeFurnaceUtil.TOTAL_SLOTS),
                new CokeFurnaceUtil.CokeFurnaceContainerData(),
                syncId
        );
    }

    public CokeFurnaceContainerMenu(
            @NotNull Inventory playerInventory,
            @NotNull Container container,
            @NotNull CokeFurnaceUtil.CokeFurnaceContainerData data,
            int syncId
    ) {
        super(ContainerMenus.COKE_FURNACE_CONTAINER_MENU, container, data, syncId);

        checkContainerSize(container, CokeFurnaceUtil.TOTAL_SLOTS);
        checkContainerDataCount(data, CokeFurnaceUtil.CONTAINER_DATA_SIZE);

        addSlot(new FilteredSlot(
                container,
                CokeFurnaceUtil::isIngredient,
                CokeFurnaceUtil.INGREDIENT_SLOT,
                CokeFurnaceUtil.INGREDIENT_SLOT_X_OFFSET,
                CokeFurnaceUtil.INGREDIENT_SLOT_Y_OFFSET
        ));

        addSlot(new ResultSlot(
                container,
                CokeFurnaceUtil.RESULT_SLOT,
                CokeFurnaceUtil.RESULT_SLOT_X_OFFSET,
                CokeFurnaceUtil.RESULT_SLOT_Y_OFFSET
        ));

        final var FLUID_SLOT_CONTROLLER = new FluidSlotController.Builder(ModItems.CREOSOTE_BUCKET)
                .setMayInsertFluid(data::canInsertCreosote)
                .setMayTakeFluid(data::canTakeCreosote)
                .setOnTakeFluid(() -> data.producedCreosote -= CokeFurnaceUtil.CREOSOTE_PER_BUCKET)
                .setOnInsertFluid(() -> data.producedCreosote += CokeFurnaceUtil.CREOSOTE_PER_BUCKET)
                .build();

        addSlot(new FluidSlot(
                FLUID_SLOT_CONTROLLER,
                container,
                CokeFurnaceUtil.CREOSOTE_SLOT,
                CokeFurnaceUtil.CREOSOTE_SLOT_X_OFFSET,
                CokeFurnaceUtil.CREOSOTE_SLOT_Y_OFFSET
        ));

        addPlayerInventorySlots(playerInventory);
        addPlayerHotbarSlots(playerInventory);

        addDataSlots(data);
    }

    @Override
    public void clicked(int slotIndex, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (clickType != ClickType.QUICK_MOVE) {
            super.clicked(slotIndex, button, clickType, player);
            return;
        }

        final var sourceSlot = slots.get(slotIndex);
        final var sourceSlotItem = sourceSlot.getItem();

        for (var cokeFurnaceSlotIndex = 0; cokeFurnaceSlotIndex < CokeFurnaceUtil.TOTAL_SLOTS; cokeFurnaceSlotIndex++) {
            final var currentSlot = getSlot(cokeFurnaceSlotIndex);

            if (!currentSlot.mayPlace(sourceSlotItem) || slotIndex == CokeFurnaceUtil.RESULT_SLOT) continue;

            final var remainingSlotStack = currentSlot.safeInsert(sourceSlotItem);
            sourceSlot.set(remainingSlotStack);
            sourceSlot.setChanged();
            return;
        }

        super.clicked(slotIndex, button, clickType, player);
    }

    public float getProgress() {
        return (float) data.progress / (float) data.maxProgress;
    }

    public float getProducedCreosoteScale() {
        return (float) data.producedCreosote / (float) CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE;
    }

    public int getProducedCreosote() {
        return data.producedCreosote;
    }

    public boolean isIngredientSlotEmpty() {
        return !getSlot(CokeFurnaceUtil.INGREDIENT_SLOT).hasItem();
    }

    public boolean isCreosoteSlotEmpty() {
        return !getSlot(CokeFurnaceUtil.CREOSOTE_SLOT).hasItem();
    }
}
