package io.github.aratakileo.greenhouses.container.slot;

import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GroundSlot extends GreenhouseInputSlot {
    public GroundSlot(@NotNull Container container, int index, int xPos, int yPos) {
        super(container, index, xPos, yPos);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return GreenhouseUtil.isGround(itemStack.getItem());
    }
}
