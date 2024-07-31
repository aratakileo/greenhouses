package io.github.aratakileo.greenhouses.util;

import io.github.aratakileo.greenhouses.ContainerAutoData;
import io.github.aratakileo.greenhouses.recipe.RecipeTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.HashSet;

public class GreenhouseUtil {
    public final static int PLANT_INPUT_SLOT = 0,
            GROUND_INPUT_SLOT = 1,
            WATER_INPUT_SLOT = 2,
            INPUT_SLOTS = 3,
            OUTPUT_SLOTS = INPUT_SLOTS,
            TOTAL_SLOTS = INPUT_SLOTS + OUTPUT_SLOTS,
            CONTAINER_DATA_SIZE = new GreenhouseContainerData().getCount(),
            RECIPE_INPUT_SLOTS = 2,
            RECIPE_DEFAULT_GROWTH_RATE = 1000;

    public final static int PROGRESS_X_OFFSET = 97,
            PROGRESS_Y_OFFSET = 35,
            WET_STATE_X_OFFSET = 29,
            WET_STATE_Y_OFFSET = PROGRESS_Y_OFFSET,
            WATER_SLOT_X_OFFSET = 50,
            WATER_SLOT_Y_OFFSET = PROGRESS_Y_OFFSET,
            PLANT_SLOT_X_OFFSET = 70,
            PLANT_SLOT_Y_OFFSET = 24,
            GROUND_SLOT_X_OFFSET = PLANT_SLOT_X_OFFSET,
            GROUND_SLOT_Y_OFFSET = 45,
            SLOT_ICON_SIZE = 16;

    // Progress fail codes that allow to specify some of the reasons why to craft the current recipe is impossible
    public final static int NO_FAILS_CODE = 0,
            INVALID_RECIPE_CODE = 1,
            DOES_NOT_NEED_WATER_CODE = 2,
            NEEDS_WATER_CODE = 3,
            NOT_ENOUGH_OUTPUT_SPACE_CODE = 4;

    private static HashSet<Item> GROUNDS = null, PLANTS = null;

    public static void init(@NotNull Level level) {
        if (GROUNDS != null && PLANTS != null) return;

        GROUNDS = new HashSet<>();
        PLANTS = new HashSet<>();

        for (final var recipe: level.getRecipeManager().getAllRecipesFor(RecipeTypes.GREENHOUSE_RECIPE_TYPE)) {
            GROUNDS.addAll(Arrays.stream(recipe.value().getGround().getItems()).map(ItemStack::getItem).toList());
            PLANTS.addAll(Arrays.stream(recipe.value().getPlant().getItems()).map(ItemStack::getItem).toList());
        }
    }

    public static @NotNull HashSet<Item> getGrounds() {
        if (GROUNDS == null) throw new RuntimeException("Something went wrong!");

        return GROUNDS;
    }

    public static boolean isGround(@NotNull Item item) {
        return getGrounds().contains(item);
    }

    public static @NotNull HashSet<Item> getPlants() {
        if (PLANTS == null) throw new RuntimeException("Something went wrong!");

        return PLANTS;
    }
    
    public static boolean isPlant(@NotNull ItemStack itemStack) {
        return isPlant(itemStack.getItem());
    }

    public static boolean isPlant(@NotNull Item item) {
        return getPlants().contains(item);
    }

    public static final class GreenhouseContainerData extends ContainerAutoData {
        @DataField
        public int progress = 0;

        @DataField
        public int maxProgress = 1;

        @DataField
        public boolean hasWater = false;

        @DataField
        public int failCode = GreenhouseUtil.NO_FAILS_CODE;
    }
}
