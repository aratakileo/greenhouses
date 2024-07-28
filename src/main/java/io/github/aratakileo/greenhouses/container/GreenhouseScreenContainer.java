package io.github.aratakileo.greenhouses.container;

import io.github.aratakileo.greenhouses.block.entity.GreenhouseBlockEntity;
import io.github.aratakileo.greenhouses.container.slot.*;
import io.github.aratakileo.greenhouses.container.slot.ResultSlot;
import io.github.aratakileo.greenhouses.screen.ScreenMenus;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class GreenhouseScreenContainer extends AbstractContainerMenu {

    private final Container container;
    private final GreenhouseBlockEntity.GreenhouseContainerData data;

    public GreenhouseScreenContainer(int syncId, @NotNull Inventory inventory) {
        this(
                syncId,
                inventory,
                new SimpleContainer(GreenhouseUtil.TOTAL_SLOTS),
                new GreenhouseBlockEntity.GreenhouseContainerData()
        );
    }

    public GreenhouseScreenContainer(
            int syncId,
            @NotNull Inventory playerInventory,
            @NotNull Container container,
            @NotNull GreenhouseBlockEntity.GreenhouseContainerData containerData
    ) {
        super(ScreenMenus.GREENHOUSE_SCREEN_MENU, syncId);
        this.container = container;
        this.data = containerData;

        checkContainerSize(container, GreenhouseUtil.TOTAL_SLOTS);
        checkContainerDataCount(data, GreenhouseUtil.CONTAINER_DATA_SIZE);

        addSlot(new PlantSlot(container, GreenhouseUtil.PLANT_INPUT_SLOT, 70, 24));

        addSlot(new GroundSlot(container, GreenhouseUtil.GROUND_INPUT_SLOT, 70, 45));

        final var FLUID_SLOT_CONTROLLER = new FluidSlotController.Builder(
                Items.WATER_BUCKET
        ).setMayTakeFluid(this::isGroundWet)
                .setOnTakeFluid(() -> data.isGroundWet = false)
                .setOnInsertFluid(() -> data.isGroundWet = true)
                .build();

        addSlot(new FluidSlot(
                FLUID_SLOT_CONTROLLER,
                container,
                GreenhouseUtil.WATER_INPUT_SLOT,
                50,
                35
        ));

        for (var i = GreenhouseUtil.INPUT_SLOTS; i < GreenhouseUtil.TOTAL_SLOTS; i++) {
            final var localIndex = i - GreenhouseUtil.INPUT_SLOTS;
            addSlot(new ResultSlot(container, i, 124, 17 * (localIndex + 1) + localIndex));
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addDataSlots(containerData);
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

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotIndex) {
        var newStack = ItemStack.EMPTY;
        final var slot = slots.get(slotIndex);

        if (slot.hasItem()) {
            final var itemStack = slot.getItem();
            newStack = itemStack.copy();

            if (slotIndex < container.getContainerSize()) {
                if (!this.moveItemStackTo(itemStack, this.container.getContainerSize(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(newStack, 0, this.container.getContainerSize(), false))
                return ItemStack.EMPTY;

            if (itemStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }

        return newStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return container.stillValid(player);
    }

    public boolean isGroundWet() {
        return data.isGroundWet;
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

    private void addPlayerInventory(@NotNull Inventory inventory) {
        for (int i = 0; i < 3; ++i)
            for (int l = 0; l < 9; ++l)
                this.addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
    }

    private void addPlayerHotbar(@NotNull Inventory inventory) {
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
    }
}
