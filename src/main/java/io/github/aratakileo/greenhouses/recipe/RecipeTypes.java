package io.github.aratakileo.greenhouses.recipe;

import io.github.aratakileo.greenhouses.Greenhouses;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public final class RecipeTypes {
    public final static RecipeType<GreenhouseRecipe> GREENHOUSE_RECIPE_TYPE = createRecipeType(
            "greenhouse_growing"
    );
    public final static RecipeType<CokeFurnaceRecipe> COKE_FURNACE_RECIPE_TYPE = createRecipeType(
            "coking"
    );

    private static <T extends Recipe<?>> RecipeType<T> createRecipeType(
            @NotNull String name
    ) {
        return Registry.register(
                BuiltInRegistries.RECIPE_TYPE,
                Greenhouses.NAMESPACE.getIdentifier(name),
                new RecipeType<T>() {
                    @Override
                    public String toString() {
                        return name;
                    }
                }
        );
    }

    public static void init() {
        RecipeSerializers.init();
    }
}
