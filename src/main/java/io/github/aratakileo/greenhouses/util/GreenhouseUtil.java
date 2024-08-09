package io.github.aratakileo.greenhouses.util;

import io.github.aratakileo.elegantia.world.container.ContainerAutoData;
import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.world.recipe.RecipeTypes;
import net.minecraft.resources.ResourceLocation;
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


    public final static ResourceLocation GUI_TEXTURE = Greenhouses.NAMESPACE.getLocation("textures/gui/greenhouse.png");

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

    public static boolean isGround(@NotNull ItemStack item) {
        return isGround(item.getItem());
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
        public int progress = 0;
        public int maxProgress = 1;
        public boolean hasWater = false;
        public GrowFail growFail = GrowFail.NONE;

        public void interruptProgress(@NotNull GreenhouseUtil.GrowFail reason) {
            progress = 0;
            growFail = reason;
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
