package io.github.aratakileo.greenhouses.world.recipe;

import io.github.aratakileo.elegantia.util.RegistriesUtil;
import io.github.aratakileo.greenhouses.Greenhouses;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public final class RecipeTypes {
    public final static RecipeType<GreenhouseRecipe> GREENHOUSE_RECIPE_TYPE = registerRecipeType(
            "greenhouse_growing"
    );
    public final static RecipeType<CokeFurnaceRecipe> COKE_FURNACE_RECIPE_TYPE = registerRecipeType(
            "coking"
    );

    private static <I extends RecipeInput, T extends Recipe<I>> RecipeType<T> registerRecipeType(
            @NotNull String name
    ) {
        return RegistriesUtil.registerRecipeType(Greenhouses.NAMESPACE.getLocation(name));
    }

    public static void init() {
        RecipeSerializers.init();
    }
}
