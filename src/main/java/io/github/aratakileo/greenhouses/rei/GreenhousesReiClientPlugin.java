package io.github.aratakileo.greenhouses.rei;

import io.github.aratakileo.greenhouses.screen.CokeFurnaceScreen;
import io.github.aratakileo.greenhouses.screen.GreenhouseScreen;
import io.github.aratakileo.greenhouses.world.block.ModBlocks;
import io.github.aratakileo.greenhouses.world.recipe.CokeFurnaceRecipe;
import io.github.aratakileo.greenhouses.world.recipe.GreenhouseRecipe;
import io.github.aratakileo.greenhouses.world.recipe.RecipeTypes;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import org.jetbrains.annotations.NotNull;

public class GreenhousesReiClientPlugin implements REIClientPlugin {
    private static final Rectangle CLICKABLE_AREA = new Rectangle(75, 30, 20, 30);

    @Override
    public void registerCategories(@NotNull CategoryRegistry registry) {
        registry.add(new GreenhouseCategory());
        registry.add(new CokeFurnaceCategory());
        registry.addWorkstations(GreenhouseCategory.IDENTIFIER, EntryStacks.of(ModBlocks.GREENHOUSE));
        registry.addWorkstations(CokeFurnaceCategory.IDENTIFIER, EntryStacks.of(ModBlocks.COKE_FURNACE));
    }

    @Override
    public void registerDisplays(@NotNull DisplayRegistry registry) {
        registry.registerRecipeFiller(
                GreenhouseRecipe.class,
                RecipeTypes.GREENHOUSE_RECIPE_TYPE,
                GreenhouseDisplay::of
        );

        registry.registerRecipeFiller(
                CokeFurnaceRecipe.class,
                RecipeTypes.COKE_FURNACE_RECIPE_TYPE,
                CokeFurnaceDisplay::of
        );
    }

    @Override
    public void registerScreens(@NotNull ScreenRegistry registry) {
        registry.registerClickArea(
                screen -> CLICKABLE_AREA,
                GreenhouseScreen.class,
                GreenhouseCategory.IDENTIFIER
        );

        registry.registerClickArea(
                screen -> CLICKABLE_AREA,
                CokeFurnaceScreen.class,
                CokeFurnaceCategory.IDENTIFIER
        );
    }
}
