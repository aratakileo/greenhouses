package io.github.aratakileo.greenhouses.container.slot;

import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import io.github.aratakileo.greenhouses.util.SoundUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GroundSlot extends GreenhouseInputSlot {
    public GroundSlot(@NotNull Container container, int index, int xPos, int yPos) {
        super(container, index, xPos, yPos);
    }

    public boolean mayUseHoeOn(@NotNull ItemStack hoe) {
        return hasItem() && List.of(
                Items.DIRT,
                Items.GRASS_BLOCK
        ).contains(getItem().getItem()) && hoe.getTags().toList().contains(ItemTags.HOES);
    }

    public void useHoe(@NotNull ItemStack hoe) {
        hoe.setDamageValue(hoe.getDamageValue() + 1);

        set(new ItemStack(Items.FARMLAND));
        setChanged();

        SoundUtil.playGuiSound(SoundEvents.HOE_TILL);
    }

    public boolean shouldPrepareBeforePickup() {
        return getItem().is(Items.FARMLAND);
    }

    public void prepareBeforePickup() {
        set(new ItemStack(Items.DIRT, getItem().getCount()));
        SoundUtil.playGuiSound(SoundEvents.HOE_TILL);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return GreenhouseUtil.isGround(itemStack.getItem()) || mayUseHoeOn(itemStack);
    }

    @Override
    public @NotNull ItemStack safeInsert(@NotNull ItemStack insertableStack, int count) {
        if (insertableStack.isEmpty()) return insertableStack;

        if (GreenhouseUtil.isGround(insertableStack.getItem()))
            return super.safeInsert(insertableStack, count);

        if (mayUseHoeOn(insertableStack)) {
            useHoe(insertableStack);
            return insertableStack;
        }

        return insertableStack;
    }
}