package io.github.aratakileo.greenhouses.recipe;

import io.github.aratakileo.greenhouses.Greenhouses;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public final class RecipeSerializers {
    public static final GreenhouseRecipe.Serializer GREENHOUSE_RECIPE_SERIALIZER = createRecipeSerializer(
            "greenhouse_growing",
            new GreenhouseRecipe.Serializer()
    );

    public static <T extends RecipeSerializer<S>, S extends Recipe<?>> T createRecipeSerializer(
            @NotNull String name,
            @NotNull T serializer
    ) {
        return Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                Greenhouses.NAMESPACE.getIdentifier(name),
                RecipeSerializer.register(name, serializer)
        );
    }

    public static void init() {

    }
}
