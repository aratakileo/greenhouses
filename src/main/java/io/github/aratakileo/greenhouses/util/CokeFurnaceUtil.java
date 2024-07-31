package io.github.aratakileo.greenhouses.util;

import io.github.aratakileo.greenhouses.ContainerAutoData;
import io.github.aratakileo.greenhouses.recipe.RecipeTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

public final class CokeFurnaceUtil {
    public final static int INGREDIENT_SLOT = 0,
            RESULT_SLOT = 1,
            CREOSOTE_SLOT = 2,
            TOTAL_SLOTS = 3,
            CONTAINER_DATA_SIZE = new CokeFurnaceContainerData().getCount(),
            MAX_PRODUCED_CREOSOTE = 12_000,
            CREOSOTE_PER_BUCKET = 3_000;

    public final static int INGREDIENT_SLOT_X_OFFSET = 42,
            INGREDIENT_SLOT_Y_OFFSET = 35,
            RESULT_SLOT_X_OFFSET = INGREDIENT_SLOT_X_OFFSET + (GreenhouseUtil.SLOT_ICON_SIZE + 4) * 2,
            RESULT_SLOT_Y_OFFSET = INGREDIENT_SLOT_Y_OFFSET,
            CREOSOTE_SLOT_X_OFFSET = 139,
            CREOSOTE_SLOT_Y_OFFSET = 57,
            PROGRESS_X_OFFSET = INGREDIENT_SLOT_X_OFFSET + GreenhouseUtil.SLOT_ICON_SIZE + 4,
            PROGRESS_Y_OFFSET = INGREDIENT_SLOT_Y_OFFSET;

    private static HashSet<Item> INGREDIENTS = null;

    public static void init(@NotNull Level level) {
        if (INGREDIENTS != null) return;

        INGREDIENTS = new HashSet<>();

        for (final var recipe: level.getRecipeManager().getAllRecipesFor(RecipeTypes.COKE_FURNACE_RECIPE_TYPE))
            INGREDIENTS.addAll(Arrays.stream(recipe.value().getIngredient().getItems()).map(ItemStack::getItem).toList());
    }

    public static @NotNull HashSet<Item> getIngredients() {
        if (INGREDIENTS == null) throw new RuntimeException("Something went wrong!");

        return INGREDIENTS;
    }
    
    public static boolean isIngredient(@NotNull ItemStack itemStack) {
        return isIngredient(itemStack.getItem());
    }

    public static boolean isIngredient(@NotNull Item item) {
        return getIngredients().contains(item);
    }

    public static final class CokeFurnaceContainerData extends ContainerAutoData {
        @DataField
        public int progress = 0;

        @DataField
        public int maxProgress = 1;

        @DataField
        public int producedCreosote = 0;

        public boolean canInsertCreosote() {
            return producedCreosote + CokeFurnaceUtil.CREOSOTE_PER_BUCKET <= CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE;
        }

        public boolean canTakeCreosote() {
            return producedCreosote >= CokeFurnaceUtil.CREOSOTE_PER_BUCKET;
        }
    }
}
