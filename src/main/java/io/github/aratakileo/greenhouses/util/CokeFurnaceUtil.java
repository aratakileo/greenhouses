package io.github.aratakileo.greenhouses.util;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.core.BuiltinTextures;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.core.math.Vector2iInterface;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.elegantia.world.container.ContainerAutoData;
import io.github.aratakileo.elegantia.world.item.recipe.Recipes;
import io.github.aratakileo.greenhouses.world.item.recipe.RecipeTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class CokeFurnaceUtil {
    public final static int INGREDIENT_SLOT = 0,
            RESULT_SLOT = 1,
            CREOSOTE_SLOT = 2,
            TOTAL_SLOTS = 3,
            CONTAINER_DATA_SIZE = new CokeFurnaceContainerData().getCount(),
            MAX_PRODUCED_CREOSOTE = 12_000,
            CREOSOTE_PER_BUCKET = 3_000;

    public final static @NotNull Vector2ic INGREDIENT_SLOT_POS = new Vector2ic(47, 35),
            RESULT_SLOT_POS = new Vector2ic(87, 35),
            CREOSOTE_SLOT_POS = new Vector2ic(129, 59),
            PROGRESSBAR_POS = new Vector2ic(67, 35);

    public final static @NotNull Rect2i CREOSOTEBAR = new Rect2i(149, 27, 14, 49);
    
    public static boolean isIngredient(@NotNull ItemStack itemStack) {
        return isIngredient(itemStack.getItem());
    }

    public static boolean isIngredient(@NotNull Item item) {
        return Recipes.isIngredient(RecipeTypes.COKE_FURNACE_RECIPE_TYPE, INGREDIENT_SLOT, item);
    }

    public static void renderCreosotebar(
            @NotNull ElGuiGraphics guiGraphics,
            @NotNull Vector2iInterface startPos,
            float producesCreosoteScale
    ) {
        final var creosotebar = CREOSOTEBAR.copy().move(startPos);

        BuiltinTextures.ELASTIC_SLOT_BACKGROUND.get().render(guiGraphics.rect(creosotebar));

        final var innerArea = creosotebar.move(1, 1).shrink(2);

        final var innerProgress = innerArea.copy().cutTop((int) (
                (float)innerArea.height * (1f - producesCreosoteScale)
        ));

        guiGraphics.rect(innerProgress).drawRgbGradient(
                0xffff00,
                0xff0000,
                RectDrawer.GradientDirection.DIAGONAL
        );

        for (var iy = 0; iy < CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE / 1000 - 1; iy++) {
            final var segmentY = innerArea.y + 3 + (iy * 4);

            guiGraphics.rect(
                    innerArea.x,
                    segmentY,
                    3,
                    1
            ).drawRgb(0x373737);
        }
    }

    public static @NotNull Component getProducedCreosoteTooltip(int producedCreosote) {
        return Component.translatable(
                "gui.greenhouses.tooltip.produced_creosote",
                "%s/%s mB".formatted(producedCreosote, CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE)
        );
    }

    public static float getProducedCreosoteScale(int producedCreosote) {
        return (float) producedCreosote / (float) CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE;
    }

    public static final class CokeFurnaceContainerData extends ContainerAutoData {
        public int progress = 0;
        public int maxProgress = 1;
        public int producedCreosote = 0;

        public boolean canInsertCreosote() {
            return producedCreosote + CokeFurnaceUtil.CREOSOTE_PER_BUCKET <= CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE;
        }

        public boolean canTakeCreosote() {
            return producedCreosote >= CokeFurnaceUtil.CREOSOTE_PER_BUCKET;
        }
    }
}
