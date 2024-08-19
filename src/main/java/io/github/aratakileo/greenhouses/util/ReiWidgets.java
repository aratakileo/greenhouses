package io.github.aratakileo.greenhouses.util;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawable.TexturedProgressDrawable;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.core.math.Size2iInterface;
import io.github.aratakileo.elegantia.core.math.Vector2iInterface;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.*;
import org.jetbrains.annotations.NotNull;

public interface ReiWidgets {
    static @NotNull Widget squareTextureWidget(
            @NotNull TextureDrawable textureDrawable,
            @NotNull Vector2iInterface pos,
            int size
    ) {
        return squareTextureWidget(textureDrawable, pos.x(), pos.y(), size);
    }

    static @NotNull Widget squareTextureWidget(@NotNull TextureDrawable textureDrawable, int x, int y, int size) {
        return textureWidget(textureDrawable, x, y, size, size);
    }

    static @NotNull Widget textureWidget(
            @NotNull TextureDrawable textureDrawable,
            @NotNull Vector2iInterface pos,
            int width,
            int height
    ) {
        return textureWidget(textureDrawable, pos.x(), pos.y(), width, height);
    }

    static @NotNull Widget textureWidget(
            @NotNull TextureDrawable textureDrawable,
            int x,
            int y,
            int width,
            int height
    ) {
        return Widgets.createTexturedWidget(
                textureDrawable.texture,
                x,
                y,
                textureDrawable.uv.x(),
                textureDrawable.uv.y(),
                width,
                height,
                textureDrawable.textureSize.width,
                textureDrawable.textureSize.height
        );
    }

    static @NotNull Slot squareSlot(@NotNull Vector2iInterface pos, int size) {
        return Widgets.createSlot(new Rectangle(pos.x(), pos.y(), size, size));
    }

    static @NotNull Slot slot(@NotNull Vector2iInterface pos) {
        return Widgets.createSlot(new Point(pos.x(), pos.y()));
    }

    static @NotNull Panel slotBackground(@NotNull Vector2iInterface pos) {
        return slotBackground(pos.sub(1), 18, 18);
    }

    static @NotNull Panel slotBackground(@NotNull Rect2i bounds) {
        return Widgets.createSlotBase(new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height));
    }

    static @NotNull Panel slotBackground(@NotNull Vector2iInterface pos, @NotNull Size2iInterface size) {
        return Widgets.createSlotBase(new Rectangle(pos.x(), pos.y(), size.width(), size.height()));
    }

    static @NotNull Panel slotBackground(@NotNull Vector2iInterface pos, int width, int height) {
        return Widgets.createSlotBase(new Rectangle(pos.x(), pos.y(), width, height));
    }

    static @NotNull Panel slotBackground(int x, int y, int width, int height) {
        return Widgets.createSlotBase(new Rectangle(x, y, width, height));
    }

    static @NotNull Panel recipePanel(@NotNull Rect2i rect2i) {
        return Widgets.createRecipeBase(new Rectangle(rect2i.x, rect2i.y, rect2i.width, rect2i.height));
    }

    static @NotNull Panel recipePanel(@NotNull Vector2iInterface pos, int width, int height) {
        return Widgets.createRecipeBase(new Rectangle(pos.x(), pos.y(), width, height));
    }

    static @NotNull Panel recipePanel(int x, int y, int width, int height) {
        return Widgets.createRecipeBase(new Rectangle(x, y, width, height));
    }

    static @NotNull WidgetWithBounds progressSquareWidget(
            @NotNull TexturedProgressDrawable progressDrawable,
            @NotNull Vector2iInterface pos,
            int size
    ) {
        return progressWidget(progressDrawable, pos.x(), pos.y(), size, size);
    }

    static @NotNull WidgetWithBounds progressSquareWidget(
            @NotNull TexturedProgressDrawable progressDrawable,
            int x,
            int y,
            int size
    ) {
        return progressWidget(progressDrawable, x, y, size, size);
    }

    static @NotNull WidgetWithBounds progressWidget(
            @NotNull TexturedProgressDrawable progressDrawable,
            @NotNull Vector2iInterface pos,
            int width,
            int height
    ) {
        return progressWidget(progressDrawable, pos.x(), pos.y(), width, height);
    }

    static @NotNull WidgetWithBounds progressWidget(
            @NotNull TexturedProgressDrawable progressDrawable,
            int x,
            int y,
            int width,
            int height
    ) {
        return rendererWidget(
                (guiGraphics) -> progressDrawable.render(guiGraphics.rect(x, y, width, height)),
                x,
                y,
                width,
                height
        );
    }

    static @NotNull WidgetWithBounds rendererWidget(
            @NotNull Renderer renderer,
            @NotNull Rect2i bounds
    ) {
        return rendererWidget(renderer, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    static @NotNull WidgetWithBounds rendererWidget(
            @NotNull Renderer renderer,
            @NotNull Vector2iInterface pos,
            int width,
            int height
    ) {
        return rendererWidget(renderer, pos.x(), pos.y(), width, height);
    }

    static @NotNull WidgetWithBounds rendererWidget(
            @NotNull Renderer renderer,
            @NotNull Vector2iInterface pos,
            @NotNull Size2iInterface size
    ) {
        return rendererWidget(renderer, pos.x(), pos.y(), size.width(), size.height());
    }

    static @NotNull WidgetWithBounds rendererWidget(
            @NotNull Renderer renderer,
            int x,
            int y,
            @NotNull Size2iInterface size
    ) {
        return rendererWidget(renderer, x, y, size.width(), size.height());
    }

    static @NotNull WidgetWithBounds rendererWidget(
            @NotNull Renderer renderer,
            int x,
            int y,
            int width,
            int height
    ) {
        return Widgets.wrapRenderer(new Rectangle(x, y, width, height), (graphics, bounds, mouseX, mouseY, delta) -> {
            renderer.render(ElGuiGraphics.of(graphics));
        });
    }

    interface Renderer {
        void render(@NotNull ElGuiGraphics guiGraphics);
    }
}
