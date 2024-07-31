package io.github.aratakileo.greenhouses.screen.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class FilteredSlot extends Slot {
    private final Function<ItemStack, Boolean> mayPlace;

    public FilteredSlot(
            @NotNull Container container,
            @NotNull Function<ItemStack, Boolean> mayPlace,
            int index,
            int xPos,
            int yPos
    ) {
        super(container, index, xPos, yPos);
        this.mayPlace = mayPlace;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return mayPlace.apply(itemStack);
    }
}
