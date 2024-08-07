package io.github.aratakileo.greenhouses.world.container;

import io.github.aratakileo.elegantia.client.graphics.GuiGraphicsUtil;
import io.github.aratakileo.elegantia.world.slot.SlotController;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class GroundSlotController implements SlotController {
    @Override
    public int getMaxStackSize(@NotNull ItemStack insertable) {
        return 1;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack slot, @NotNull ItemStack insertable) {
        return GreenhouseUtil.isGround(insertable) || mayUseHoe(slot, insertable);
    }

    @Override
    public @NotNull ItemStack safeInsert(
            @NotNull ItemStack slot,
            @NotNull ItemStack insertable,
            int insertableCount,
            @NotNull Consumer<ItemStack> slotSetter
    ) {
        if (insertable.isEmpty()) return insertable;

        if (GreenhouseUtil.isGround(insertable))
            return SlotController.super.safeInsert(slot, insertable, insertableCount, slotSetter);

        if (mayUseHoe(slot, insertable))
            useHoe(insertable, slotSetter);

        return insertable;
    }

    public static boolean mayUseHoe(@NotNull ItemStack slot, @NotNull ItemStack hoe) {
        return !slot.isEmpty() && List.of(
                Items.DIRT,
                Items.GRASS_BLOCK
        ).contains(slot.getItem()) && hoe.getTags().toList().contains(ItemTags.HOES);
    }

    public static void useHoe(
            @NotNull ItemStack hoe,
            @NotNull Consumer<ItemStack> slotSetter
    ) {
        hoe.setDamageValue(hoe.getDamageValue() + 1);
        slotSetter.accept(new ItemStack(Items.FARMLAND));
        GuiGraphicsUtil.playSound(SoundEvents.HOE_TILL);
    }

    public static void prepareBeforePickup(@NotNull ItemStack slot, @NotNull Consumer<ItemStack> slotSetter) {
        if (!slot.is(Items.FARMLAND)) return;

        slotSetter.accept(new ItemStack(Items.DIRT, slot.getCount()));
        GuiGraphicsUtil.playSound(SoundEvents.HOE_TILL);
    }
}
