package io.github.aratakileo.greenhouses.container.slot;

import io.github.aratakileo.greenhouses.container.GreenhouseScreenContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class WaterBucketSlot extends Slot {
    private final GreenhouseScreenContainer greenhouseScreenContainer;

    public WaterBucketSlot(
            @NotNull GreenhouseScreenContainer greenhouseScreenContainer,
            @NotNull Container container,
            int index,
            int xPos,
            int yPos
    ) {
        super(container, index, xPos, yPos);
        this.greenhouseScreenContainer = greenhouseScreenContainer;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return itemStack.is(Items.WATER_BUCKET) && !greenhouseScreenContainer.isGroundWet();
    }
}
