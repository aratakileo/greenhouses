package io.github.aratakileo.greenhouses.container.slot;

import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.container.GreenhouseScreenContainer;
import io.github.aratakileo.greenhouses.util.SoundUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class WaterBucketSlot extends Slot {
    private final GreenhouseScreenContainer menu;

    public WaterBucketSlot(
            @NotNull GreenhouseScreenContainer menu,
            @NotNull Container container,
            int index,
            int xPos,
            int yPos
    ) {
        super(container, index, xPos, yPos);
        this.menu = menu;
    }

    @Override
    public @NotNull ItemStack safeInsert(@NotNull ItemStack insertableStack, int count) {
        if (insertableStack.isEmpty()) return insertableStack;

        if (menu.isGroundWet() && insertableStack.is(Items.BUCKET)) {
            SoundUtil.playGuiSound(SoundEvents.BUCKET_FILL);
            menu.setGroundWet(false);
            return new ItemStack(Items.WATER_BUCKET, 1);
        }

        if (!menu.isGroundWet() && insertableStack.is(Items.WATER_BUCKET)) {
            SoundUtil.playGuiSound(SoundEvents.BUCKET_EMPTY);
            menu.setGroundWet(true);
            return new ItemStack(Items.BUCKET, 1);
        }

        return insertableStack;
    }
}
