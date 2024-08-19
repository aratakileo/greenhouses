package io.github.aratakileo.greenhouses.gui.rei;

import io.github.aratakileo.elegantia.world.item.ItemRandomRange;
import io.github.aratakileo.greenhouses.world.recipe.GreenhouseRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class GreenhouseDisplay extends BasicDisplay {
    public final boolean needsWater;
    public final boolean isGroundFarm;

    private GreenhouseDisplay(
            @NotNull List<EntryIngredient> inputs,
            @NotNull List<EntryIngredient> outputs,
            boolean needsWater,
            boolean isGroundFarm) {
        super(inputs, outputs);
        this.needsWater = needsWater;
        this.isGroundFarm = isGroundFarm;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return GreenhouseCategory.IDENTIFIER;
    }

    public static @NotNull GreenhouseDisplay of(@NotNull RecipeHolder<GreenhouseRecipe> recipeHolder) {
        final var recipe = recipeHolder.value();

        return new GreenhouseDisplay(
                EntryIngredients.ofIngredients(recipe.getIngredients()),
                recipe.getRawResultItems()
                        .stream()
                        .map(ItemRandomRange::getMaxItemStack)
                        .map(EntryIngredients::of)
                        .collect(Collectors.toList()),
                recipe.needsWater(),
                recipe.getGround().getItems()[0].is(Blocks.FARMLAND.asItem())
        );
    }
}
