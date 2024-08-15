package io.github.aratakileo.greenhouses.world.rei;

import io.github.aratakileo.elegantia.client.graphics.drawable.TexturedProgressDrawable;
import io.github.aratakileo.elegantia.core.BuiltinTextures;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import io.github.aratakileo.greenhouses.util.ReiWidgets;
import io.github.aratakileo.greenhouses.util.Textures;
import io.github.aratakileo.greenhouses.world.block.ModBlocks;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GreenhouseCategory implements DisplayCategory<GreenhouseDisplay> {
    public final static CategoryIdentifier<GreenhouseDisplay> IDENTIFIER
            = CategoryIdentifier.of(Greenhouses.NAMESPACE.getLocation("greenhouse_category"));

    @Override
    public CategoryIdentifier<GreenhouseDisplay> getCategoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.GREENHOUSE.getName();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.GREENHOUSE.asItem().getDefaultInstance());
    }

    @Override
    public @NotNull List<Widget> setupDisplay(@NotNull GreenhouseDisplay display, @NotNull Rectangle bounds) {
        final var startPos = new Vector2ic(bounds.getCenterX(), bounds.getCenterY()).sub(87, 35);
        final var widgets = new ArrayList<Widget>();

        widgets.add(ReiWidgets.recipePanel(startPos, 176, getDisplayHeight()));

        widgets.add(
                ReiWidgets.slot(GreenhouseUtil.PLANT_SLOT_POS.add(startPos))
                        .markInput()
                        .entries(display.getInputEntries().get(GreenhouseUtil.PLANT_INPUT_SLOT))
        );

        widgets.add(
                ReiWidgets.slot(GreenhouseUtil.GROUND_SLOT_POS.add(startPos))
                        .markInput()
                        .entries(display.getInputEntries().get(GreenhouseUtil.GROUND_INPUT_SLOT))
        );

        final var dropsPos = GreenhouseUtil.DROPS_POS.add(startPos);

        widgets.add(ReiWidgets.squareTextureWidget(
                Textures.DROPS_BACKGROUND_ICON.get(),
                dropsPos,
                16
        ));

        if (display.needsWater) {
            widgets.add(ReiWidgets.squareTextureWidget(
                    Textures.DROPS_ICON.get(),
                    dropsPos,
                    16
            ));
        }

        final var waterSlotPos = GreenhouseUtil.WATER_SLOT_POS.add(startPos);

        widgets.add(ReiWidgets.slotBackground(waterSlotPos));
        widgets.add(ReiWidgets.squareTextureWidget(
                BuiltinTextures.BUCKET_SLOT_ICON.get(),
                waterSlotPos,
                16
        ));

        final var slotPositions = GreenhouseUtil.getResultSlotPositions(startPos);

        for (var i = 0; i < GreenhouseUtil.OUTPUT_SLOTS; i++) {
            final var slotPos = slotPositions.get(i);

            widgets.add(
                    i < display.getOutputEntries().size()
                            ? ReiWidgets.slot(slotPos).markOutput().entries(display.getOutputEntries().get(i))
                            : ReiWidgets.slotBackground(slotPos)
            );
        }

        final var progressbarPos = GreenhouseUtil.PROGRESSBAR_POS.add(startPos);

        widgets.add(ReiWidgets.squareTextureWidget(
                Textures.LEAF_BACKGROUND_ICON.get(),
                progressbarPos,
                16
        ));

        widgets.add(ReiWidgets.progressSquareWidget(
                TexturedProgressDrawable.of(
                        Textures.LEAF_ICON.get(),
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
