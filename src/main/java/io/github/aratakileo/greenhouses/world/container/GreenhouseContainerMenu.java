package io.github.aratakileo.greenhouses.world.container;

import io.github.aratakileo.elegantia.core.BuiltinTextures;
import io.github.aratakileo.elegantia.world.container.SimpleContainerMenu;
import io.github.aratakileo.elegantia.world.slot.ElegantedSlot;
import io.github.aratakileo.elegantia.world.slot.FluidSlotController;
import io.github.aratakileo.elegantia.world.slot.SlotController;
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

        addSlot(new ElegantedSlot(
                container,
                new SlotController.Builder().setSingeItemMaxStackSize().setMayPlace(GreenhouseUtil::isPlant).build(),
                GreenhouseUtil.PLANT_INPUT_SLOT,
                GreenhouseUtil.PLANT_SLOT_POS
        ).setIcon(BuiltinTextures.LEAF_SLOT_ICON));

        addSlot(new ElegantedSlot(
                container,
                new GroundSlotController(),
                GreenhouseUtil.GROUND_INPUT_SLOT,
                GreenhouseUtil.GROUND_SLOT_POS
        ).setIcon(BuiltinTextures.BLOCK_SLOT_ICON));

        final var FLUID_SLOT_CONTROLLER = new FluidSlotController.Builder(
                Items.WATER_BUCKET,
                () -> data.hasWater = true,
                () -> data.hasWater = false
        ).setMayTakeFluid(this::isGroundWet).build();

        addSlot(new ElegantedSlot(
                container,
                FLUID_SLOT_CONTROLLER,
                GreenhouseUtil.WATER_INPUT_SLOT,
                GreenhouseUtil.WATER_SLOT_POS
        ).setIcon(BuiltinTextures.BUCKET_SLOT_ICON));

        final var resultSlotPositions = GreenhouseUtil.getResultSlotPositions();

        for (var i = GreenhouseUtil.INPUT_SLOTS; i < GreenhouseUtil.TOTAL_SLOTS; i++)
            addSlot(new ElegantedSlot(
                    container,
                    SlotController.RESULT,
                    i,
                    resultSlotPositions.get(i - GreenhouseUtil.INPUT_SLOTS)
            ));

        addPlayerInventorySlots(playerInventory);
        addPlayerHotbarSlots(playerInventory);

        addDataSlots(data);
    }

    @Override
    public void clicked(int slotIndex, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotIndex < 0) {
            super.clicked(slotIndex, button, clickType, player);
            return;
        }

        final var sourceSlot = slots.get(slotIndex);

        if (
                slotIndex == GreenhouseUtil.GROUND_INPUT_SLOT
                        && sourceSlot instanceof ElegantedSlot slot
                        && slot.controller instanceof GroundSlotController controller
        ) {
            final var carriedItem = getCarried();

            if (clickType == ClickType.PICKUP) {
                if (!carriedItem.isEmpty() && GroundSlotController.mayUseHoe(slot.getItem(), carriedItem)) {
                    GroundSlotController.useHoe(carriedItem, slot::set);
                    return;
                }

                GroundSlotController.prepareBeforePickup(slot.getItem(), slot::set);
            }

            if (clickType == ClickType.QUICK_MOVE)
                GroundSlotController.prepareBeforePickup(slot.getItem(), slot::set);
        }

        if (slotIndex >= GreenhouseUtil.TOTAL_SLOTS && clickType == ClickType.QUICK_MOVE) {
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

    public float getProgressScale() {
        return data.getProgressScale();
    }

    public @NotNull GreenhouseUtil.GrowFail getGrowFailState() {
        return data.growFail;
    }

    public boolean isProgressFailed() {
        return data.growFail != GreenhouseUtil.GrowFail.NONE;
    }
}
