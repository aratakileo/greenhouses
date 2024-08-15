package io.github.aratakileo.greenhouses.util;

import io.github.aratakileo.elegantia.core.math.Vector2iInterface;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.elegantia.world.container.ContainerAutoData;
import io.github.aratakileo.elegantia.world.item.recipe.Recipes;
import io.github.aratakileo.greenhouses.world.recipe.RecipeTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

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

    public final static Vector2ic PLANT_SLOT_POS = new Vector2ic(70, 24),
            GROUND_SLOT_POS = new Vector2ic(70, 45),
            WATER_SLOT_POS = new Vector2ic(50, 35),
            DROPS_POS = new Vector2ic(29, 35),
            PROGRESSBAR_POS = new Vector2ic(97, 35);

    public static boolean isGround(@NotNull ItemStack item) {
        return isGround(item.getItem());
    }

    public static boolean isGround(@NotNull Item item) {
        return Recipes.isIngredient(RecipeTypes.GREENHOUSE_RECIPE_TYPE, GROUND_INPUT_SLOT, item);
    }
    
    public static boolean isPlant(@NotNull ItemStack itemStack) {
        return isPlant(itemStack.getItem());
    }

    public static boolean isPlant(@NotNull Item item) {
        return Recipes.isIngredient(RecipeTypes.GREENHOUSE_RECIPE_TYPE, PLANT_INPUT_SLOT, item);
    }

    public static @NotNull ArrayList<Vector2ic> getResultSlotPositions() {
        return getResultSlotPositions(null);
    }

    public static @NotNull ArrayList<Vector2ic> getResultSlotPositions(@Nullable Vector2iInterface startPos) {
        final var positions = new ArrayList<Vector2ic>();

        for (var i = 0; i < INPUT_SLOTS; i++) {
            final var position = new Vector2ic(124, 17 * (i + 1) + i);

            if (startPos == null)
                positions.add(position);
            else positions.add(position.add(startPos));
        }

        return positions;
    }

    public static float getProgressScale(int progress, int maxProgress) {
        return (float) progress / (float) maxProgress;
    }

    public static final class GreenhouseContainerData extends ContainerAutoData {
        public int progress = 0;
        public int maxProgress = 1;
        public boolean hasWater = false;
        public GrowFail growFail = GrowFail.NONE;

        public void interruptProgress(@NotNull GreenhouseUtil.GrowFail reason) {
            progress = 0;
            growFail = reason;
        }

        public float getProgressScale() {
            return GreenhouseUtil.getProgressScale(progress, maxProgress);
        }
    }

    public enum GrowFail {
        NONE,
        INVALID_RECIPE,
        DOES_NOT_NEED_WATER,
        NEEDS_WATER,
        NOT_ENOUGH_OUTPUT_SPACE
    }
}
