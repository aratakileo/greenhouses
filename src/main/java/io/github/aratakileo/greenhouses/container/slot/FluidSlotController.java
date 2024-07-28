package io.github.aratakileo.greenhouses.container.slot;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public interface FluidSlotController {
    @NotNull Item getContainerWithFluid();
    default @NotNull Item getEmptyFluidContainer() {
        return Objects.requireNonNull(getContainerWithFluid().getCraftingRemainingItem());
    }

    boolean mayInsertFluid();
    default boolean mayTakeFluid() {
        return !mayInsertFluid();
    }

    default @NotNull SoundEvent getFluidInsertSound() {
        return getContainerWithFluid() == Items.LAVA_BUCKET ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
    }

    default @NotNull SoundEvent getFluidTakeSound() {
        return getContainerWithFluid() == Items.LAVA_BUCKET ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
    }

    void onInsertFluid();
    void onTakeFluid();

    class Builder {
        private @NotNull Item containerWithFluid;
        private @Nullable Item emptyFluidContainer;
        private @NotNull Supplier<Boolean> mayInsertFluid;
        private @Nullable Supplier<Boolean> mayTakeFluid;
        private @Nullable SoundEvent fluidInsertSound;
        private @Nullable SoundEvent fluidTakeSound;
        private @NotNull Runnable onInsertFluid;
        private @NotNull Runnable onTakeFluid;

        public Builder(@NotNull Item containerWithFluid) {
            this.containerWithFluid = containerWithFluid;
        }

        public @NotNull Builder setContainerWithFluid(@NotNull Item containerWithFluid) {
            this.containerWithFluid = containerWithFluid;
            return this;
        }

        public @NotNull Builder setEmptyFluidContainer(@Nullable Item emptyFluidContainer) {
            this.emptyFluidContainer = emptyFluidContainer;
            return this;
        }

        public @NotNull Builder setMayInsertFluid(@NotNull Supplier<Boolean> mayInsertFluid) {
            this.mayInsertFluid = mayInsertFluid;
            return this;
        }

        public @NotNull Builder setMayTakeFluid(@Nullable Supplier<Boolean> mayTakeFluid) {
            this.mayTakeFluid = mayTakeFluid;
            return this;
        }

        public @NotNull Builder setFluidInsertSound(@Nullable SoundEvent fluidInsertSound) {
            this.fluidInsertSound = fluidInsertSound;
            return this;
        }

        public @NotNull Builder setFluidTakeSound(@Nullable SoundEvent fluidTakeSound) {
            this.fluidTakeSound = fluidTakeSound;
            return this;
        }

        public @NotNull Builder setOnInsertFluid(@NotNull Runnable onInsertFluid) {
            this.onInsertFluid = onInsertFluid;
            return this;
        }

        public @NotNull Builder setOnTakeFluid(@NotNull Runnable onTakeFluid) {
            this.onTakeFluid = onTakeFluid;
            return this;
        }

        public @NotNull FluidSlotController build() {
            return new FluidSlotController() {
                @Override
                public @NotNull Item getContainerWithFluid() {
                    return containerWithFluid;
                }

                @Override
                public @NotNull Item getEmptyFluidContainer() {
                    return emptyFluidContainer == null ? FluidSlotController.super.getEmptyFluidContainer() : emptyFluidContainer;
                }

                @Override
                public boolean mayInsertFluid() {
                    return mayInsertFluid.get();
                }

                @Override
                public boolean mayTakeFluid() {
                    return mayTakeFluid == null ? FluidSlotController.super.mayTakeFluid() : mayInsertFluid.get();
                }

                @Override
                public @NotNull SoundEvent getFluidInsertSound() {
                    return fluidInsertSound == null ? FluidSlotController.super.getFluidInsertSound() : fluidInsertSound;
                }

                @Override
                public @NotNull SoundEvent getFluidTakeSound() {
                    return fluidTakeSound == null ? FluidSlotController.super.getFluidTakeSound() : fluidTakeSound;
                }

                @Override
                public void onInsertFluid() {
                    onInsertFluid.run();
                }

                @Override
                public void onTakeFluid() {
                    onTakeFluid.run();
                }
            };
        }
    }
}
