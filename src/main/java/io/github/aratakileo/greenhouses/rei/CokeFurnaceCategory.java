package io.github.aratakileo.greenhouses.rei;

import io.github.aratakileo.elegantia.client.graphics.drawable.TexturedProgressDrawable;
import io.github.aratakileo.elegantia.core.BuiltinTextures;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import io.github.aratakileo.greenhouses.util.ReiWidgets;
import io.github.aratakileo.greenhouses.util.Textures;
import io.github.aratakileo.greenhouses.world.block.ModBlocks;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CokeFurnaceCategory implements DisplayCategory<CokeFurnaceDisplay> {
    public final static CategoryIdentifier<CokeFurnaceDisplay> IDENTIFIER
            = CategoryIdentifier.of(Greenhouses.NAMESPACE.getLocation("coke_furnace_category"));

    @Override
    public CategoryIdentifier<CokeFurnaceDisplay> getCategoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.COKE_FURNACE.getName();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.COKE_FURNACE.asItem().getDefaultInstance());
    }

    @Override
    public @NotNull List<Widget> setupDisplay(@NotNull CokeFurnaceDisplay display, @NotNull Rectangle bounds) {
        final var startPos = new Vector2ic(bounds.getCenterX(), bounds.getCenterY()).sub(87, 35);
        final var widgets = new ArrayList<Widget>();

        widgets.add(ReiWidgets.recipePanel(startPos, 176, getDisplayHeight()));

        widgets.add(
                ReiWidgets.slot(CokeFurnaceUtil.INGREDIENT_SLOT_POS.add(startPos))
                        .markInput()
                        .entries(display.getIngredient())
        );

        widgets.add(
                ReiWidgets.slot(CokeFurnaceUtil.RESULT_SLOT_POS.add(startPos))
                        .markOutput()
                        .entries(display.getResult())
        );

        final var creosoteSlotPos = CokeFurnaceUtil.CREOSOTE_SLOT_POS.add(startPos);

        widgets.add(ReiWidgets.slotBackground(creosoteSlotPos));
        widgets.add(ReiWidgets.squareTextureWidget(
                BuiltinTextures.BUCKET_SLOT_ICON.get(),
                creosoteSlotPos,
                16
        ));

        final var creosotebar = CokeFurnaceUtil.CREOSOTEBAR.copy().move(startPos);

        widgets.add(Widgets.withTooltip(
                ReiWidgets.rendererWidget(
                        guiGraphics -> CokeFurnaceUtil.renderCreosotebar(
                                guiGraphics,
                                startPos,
                                CokeFurnaceUtil.getProducedCreosoteScale(display.producedCreosote)
                        ),
                        creosotebar
                ),
                CokeFurnaceUtil.getProducedCreosoteTooltip(display.producedCreosote)
        ));

        final var progressbarPos = CokeFurnaceUtil.PROGRESSBAR_POS.add(startPos);

        widgets.add(ReiWidgets.squareTextureWidget(
                Textures.FIRE_BACKGROUND_ICON.get(),
                progressbarPos,
                16
        ));

        widgets.add(ReiWidgets.progressSquareWidget(
                TexturedProgressDrawable.of(
                        Textures.FIRE_ICON.get(),
                        TexturedProgressDrawable.Direction.TOP,
                        TexturedProgressDrawable.demoProgressAnimation(1)
                ),
                progressbarPos,
                16
        ));

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 88;
    }
}
