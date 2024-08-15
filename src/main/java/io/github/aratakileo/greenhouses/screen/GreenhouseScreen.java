package io.github.aratakileo.greenhouses.screen;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawable.TexturedProgressDrawable;
import io.github.aratakileo.elegantia.client.gui.screen.AbstractContainerScreen;
import io.github.aratakileo.elegantia.core.BuiltinTextures;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.greenhouses.util.Textures;
import io.github.aratakileo.greenhouses.world.container.GreenhouseContainerMenu;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GreenhouseScreen extends AbstractContainerScreen<GreenhouseContainerMenu> {
    private final TexturedProgressDrawable leafProgressDrawable;

    private final static Rect2i PROGRESSBAR_RECT = Rect2i.square(GreenhouseUtil.PROGRESSBAR_POS, 16);

    public GreenhouseScreen(
            @NotNull GreenhouseContainerMenu abstractContainerMenu,
            @NotNull Inventory inventory,
            @NotNull Component component
    ) {
        super(abstractContainerMenu, inventory, component);

        this.leafProgressDrawable = TexturedProgressDrawable.of(
                Textures.LEAF_ICON,
                TexturedProgressDrawable.Direction.TOP,
                menu::getProgressScale
        );
    }

    @Override
    public void renderForeground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        if (PROGRESSBAR_RECT.contains(mousePos.sub(getPanelPos())))
            showTooltip(menu.isProgressFailed()
                    ? Component.translatable("gui.greenhouses.tooltip.growing_failed_%s".formatted(
                            menu.getGrowFailState().ordinal()
                    ))
                    : Component.translatable(
                            "gui.greenhouses.tooltip.progress",
                            "%.2f%%".formatted(menu.getProgressScale() * 100f)
                    )
            );

        super.renderForeground(guiGraphics, mousePos, dt);
    }

    @Override
    public void renderBackgroundContent(@NotNull ElGuiGraphics elGuiGraphics, @NotNull Vector2ic vector2ic, float v) {
        final var progressRectDrawer = elGuiGraphics.rect(PROGRESSBAR_RECT.copy().move(getPanelPos()));

        Textures.LEAF_BACKGROUND_ICON.get().render(progressRectDrawer);
        leafProgressDrawable.render(progressRectDrawer);

        progressRectDrawer.bounds.shrink(1);

        if (menu.isProgressFailed())
            BuiltinTextures.RED_CROSS_ICON.get().render(progressRectDrawer);

        final var dropsRectDrawer = elGuiGraphics.square(
                getPanelPos().add(GreenhouseUtil.DROPS_POS),
                16
        );

        Textures.DROPS_BACKGROUND_ICON.get().render(dropsRectDrawer);

        if (menu.isGroundWet())
            Textures.DROPS_ICON.get().render(dropsRectDrawer);
    }
}
