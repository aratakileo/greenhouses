package io.github.aratakileo.greenhouses;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class FuelController {
    public static Map<Item, Integer> getFuel() {
        return AbstractFurnaceBlockEntity.getFuel();
    }

    public static boolean isFuel(@NotNull ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.getFuel().containsKey(itemStack.getItem());
    }

    public static void addFuel(@NotNull ItemLike item, int cookTime) {
        FuelRegistry.INSTANCE.add(item, cookTime);
    }
}
