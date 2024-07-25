package io.github.aratakileo.greenhouses.container.slot;

import io.github.aratakileo.greenhouses.container.GreenhouseScreenContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class GroundSlot extends Slot {
    public GroundSlot(@NotNull Container container, int index, int xPos, int yPos) {
        super(container, index, xPos, yPos);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return itemStack.is(Items.DIRT);
    }
}
