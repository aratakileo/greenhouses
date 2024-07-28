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
import java.util.function.Consumer;

public class GroundSlot extends SingleItemSlot {
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
        return shouldPrepareBeforePickup(getItem());
    }

    public void prepareBeforePickup() {
        prepareBeforePickup(getItem(), this::set);
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

    public static boolean shouldPrepareBeforePickup(@NotNull ItemStack itemStack) {
        return itemStack.is(Items.FARMLAND);
    }

    public static void prepareBeforePickup(@NotNull ItemStack itemStack, @NotNull Consumer<@NotNull ItemStack> setter) {
        setter.accept(new ItemStack(Items.DIRT, itemStack.getCount()));
        SoundUtil.playGuiSound(SoundEvents.HOE_TILL);
    }
}
