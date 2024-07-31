package io.github.aratakileo.greenhouses.screen.container.slot;

import io.github.aratakileo.greenhouses.util.SoundUtil;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FluidSlot extends SingleItemSlot {
    private final FluidSlotController fluidSlotController;

    public FluidSlot(
            @NotNull FluidSlotController fluidSlotController,
            @NotNull Container container,
            int index,
            int xPos,
            int yPos
    ) {
        super(container, index, xPos, yPos);

        this.fluidSlotController = fluidSlotController;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack insertableStack) {
        return fluidSlotController.mayInsertFluid() && insertableStack.is(fluidSlotController.getContainerWithFluid())
                || fluidSlotController.mayTakeFluid() && insertableStack.is(fluidSlotController.getEmptyFluidContainer());
    }

    @Override
    public @NotNull ItemStack safeInsert(@NotNull ItemStack insertableStack, int count) {
        if (insertableStack.isEmpty()) return insertableStack;

        if (fluidSlotController.mayTakeFluid() && insertableStack.is(fluidSlotController.getEmptyFluidContainer())) {
            SoundUtil.playGuiSound(fluidSlotController.getFluidTakeSound());
            fluidSlotController.onTakeFluid();
            return new ItemStack(fluidSlotController.getContainerWithFluid());
        }

        if (fluidSlotController.mayInsertFluid() && insertableStack.is(fluidSlotController.getContainerWithFluid())) {
            SoundUtil.playGuiSound(fluidSlotController.getFluidInsertSound());
            fluidSlotController.onInsertFluid();
            return new ItemStack(fluidSlotController.getEmptyFluidContainer());
        }

        return insertableStack;
    }
}
