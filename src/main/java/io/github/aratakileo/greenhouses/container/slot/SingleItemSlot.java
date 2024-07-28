package io.github.aratakileo.greenhouses.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class SingleItemSlot extends Slot {
    public SingleItemSlot(@NotNull Container container, int index, int xPos, int yPos) {
        super(container, index, xPos, yPos);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
