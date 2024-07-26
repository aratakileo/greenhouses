package io.github.aratakileo.greenhouses.recipe.greenhouse;


import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record GreenhouseRecipeInput(
        @NotNull ItemStack plantInput,
        @NotNull ItemStack groundInput,
        boolean isGroundWet
) implements RecipeInput {
    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> plantInput;
            case 1 -> groundInput;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public int size() {
        return GreenhouseUtil.RECIPE_INPUT_SLOTS;
    }

    @Override
    public @NotNull ItemStack plantInput() {
        return plantInput;
    }

    @Override
    public @NotNull ItemStack groundInput() {
        return groundInput;
    }
}
