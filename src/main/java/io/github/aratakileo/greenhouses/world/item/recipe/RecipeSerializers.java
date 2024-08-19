package io.github.aratakileo.greenhouses.world.item.recipe;

import io.github.aratakileo.elegantia.util.RegistriesUtil;
import io.github.aratakileo.greenhouses.Greenhouses;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public final class RecipeSerializers {
    public static final GreenhouseRecipe.Serializer GREENHOUSE_RECIPE_SERIALIZER = registerRecipeSerializer(
            "greenhouse_growing",
            new GreenhouseRecipe.Serializer()
    );
    public static final CokeFurnaceRecipe.Serializer COKE_FURNACE_SERIALIZER = registerRecipeSerializer(
            "coking",
            new CokeFurnaceRecipe.Serializer()
    );

    public static <T extends RecipeSerializer<S>, S extends Recipe<?>> T registerRecipeSerializer(
            @NotNull String name,
            @NotNull T serializer
    ) {
        return RegistriesUtil.registerRecipeSerializer(Greenhouses.NAMESPACE.getLocation(name), serializer);
    }

    public static void init() {

    }
}
