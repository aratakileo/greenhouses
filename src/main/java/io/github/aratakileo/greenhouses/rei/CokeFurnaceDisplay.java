package io.github.aratakileo.greenhouses.rei;

import io.github.aratakileo.greenhouses.world.recipe.CokeFurnaceRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CokeFurnaceDisplay extends BasicDisplay {
    public final int producedCreosote;

    public CokeFurnaceDisplay(
            @NotNull List<EntryIngredient> inputs,
            @NotNull List<EntryIngredient> outputs,
            int producedCreosote
    ) {
        super(inputs, outputs);
        this.producedCreosote = producedCreosote;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CokeFurnaceCategory.IDENTIFIER;
    }

    public @NotNull EntryIngredient getIngredient() {
        return inputs.get(0);
    }

    public @NotNull EntryIngredient getResult() {
        return outputs.get(0);
    }

    public static @NotNull CokeFurnaceDisplay of(@NotNull RecipeHolder<CokeFurnaceRecipe> recipeHolder) {
        final var recipe = recipeHolder.value();

        return new CokeFurnaceDisplay(
                List.of(EntryIngredients.ofIngredient(recipe.getIngredient())),
                List.of(EntryIngredients.of(recipe.getResult())),
                recipe.getProducedCreosote()
        );
    }
}
