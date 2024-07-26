package io.github.aratakileo.greenhouses.container;

import io.github.aratakileo.greenhouses.container.slot.GroundSlot;
import io.github.aratakileo.greenhouses.container.slot.PlantSlot;
import io.github.aratakileo.greenhouses.container.slot.AddWaterSlot;
import io.github.aratakileo.greenhouses.container.slot.ResultSlot;
import io.github.aratakileo.greenhouses.screen.ScreenMenus;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GreenhouseScreenContainer extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public GreenhouseScreenContainer(int syncId, @NotNull Inventory inventory) {
        this(
                syncId,
                inventory,
                new SimpleContainer(GreenhouseUtil.TOTAL_SLOTS),
                new SimpleContainerData(GreenhouseUtil.CONTAINER_DATA_SIZE)
        );
    }

    public GreenhouseScreenContainer(
            int syncId,
            @NotNull Inventory playerInventory,
            @NotNull Container container,
            @NotNull ContainerData containerData
    ) {
        super(ScreenMenus.GREENHOUSE_SCREEN_MENU, syncId);
        this.container = container;
        this.data = containerData;

        checkContainerSize(container, GreenhouseUtil.TOTAL_SLOTS);
        checkContainerDataCount(data, GreenhouseUtil.CONTAINER_DATA_SIZE);

        addSlot(new PlantSlot(container, GreenhouseUtil.PLANT_INPUT_SLOT, 70, 24));

        addSlot(new GroundSlot(container, GreenhouseUtil.GROUND_INPUT_SLOT, 70, 45));

        addSlot(new AddWaterSlot(
                this,
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
    public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
        var newStack = ItemStack.EMPTY;
        final var slot = slots.get(invSlot);

        if (slot.hasItem()) {
            final var itemStack = slot.getItem();
            newStack = itemStack.copy();

            if (invSlot < container.getContainerSize()) {
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
        return data.get(2) == 1;
    }
    public void setGroundWet(boolean isWet) {
        data.set(2, isWet ? 1 : 0);
    }

    public boolean isGrowing() {
        return data.get(0) > 0;
    }

    public float getProgress() {
        return (float) data.get(0) / (float) data.get(1);
    }

    public int getFailCode() {
        return data.get(3);
    }

    public boolean isInvalidRecipe() {
        return data.get(3) >= GreenhouseUtil.INVALID_RECIPE_CODE;
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
