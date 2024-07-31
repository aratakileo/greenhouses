package io.github.aratakileo.greenhouses.screen.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SingleItemFilteredSlot extends FilteredSlot {
    public SingleItemFilteredSlot(
            @NotNull Container container,
            @NotNull Function<ItemStack, Boolean> mayPlace,
            int index,
            int xPos,
            int yPos
    ) {
        super(container, mayPlace, index, xPos, yPos);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
