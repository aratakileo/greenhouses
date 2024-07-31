package io.github.aratakileo.greenhouses.screen.container;

import io.github.aratakileo.greenhouses.ContainerAutoData;
import io.github.aratakileo.greenhouses.block.entity.ContainerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleContainerMenu<T extends ContainerAutoData> extends AbstractContainerMenu {
    protected final T data;
    protected final Container container;

    protected SimpleContainerMenu(
            @Nullable MenuType<?> menuType,
            @NotNull Container container,
            @NotNull T data,
            int syncId
    ) {
        super(menuType, syncId);
        this.container = container;
        this.data = data;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
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

    protected void addPlayerInventorySlots(@NotNull Inventory inventory) {
        for (int i = 0; i < 3; ++i)
            for (int l = 0; l < 9; ++l)
                this.addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
    }

    protected void addPlayerHotbarSlots(@NotNull Inventory inventory) {
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
    }
}
