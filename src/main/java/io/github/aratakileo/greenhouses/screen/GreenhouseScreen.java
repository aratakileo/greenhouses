package io.github.aratakileo.greenhouses.screen;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawable.TexturedProgressDrawable;
import io.github.aratakileo.elegantia.client.gui.screen.AbstractContainerScreen;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.greenhouses.world.container.GreenhouseContainerMenu;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GreenhouseScreen extends AbstractContainerScreen<GreenhouseContainerMenu> {
    private final TexturedProgressDrawable progressDrawable;

    private final static Rect2i PROGRESS_OFFSET_RECT = Rect2i.square(97, 35, 16);

    private final static TextureDrawable PROGRESS_FAILED_ICON = TextureDrawable.of(GreenhouseUtil.GUI_TEXTURE).setUV(
            176, 16
    ), WATER_ICON = TextureDrawable.of(GreenhouseUtil.GUI_TEXTURE).setUV(176, 32);

    public GreenhouseScreen(
            @NotNull GreenhouseContainerMenu abstractContainerMenu,
            @NotNull Inventory inventory,
            @NotNull Component component
    ) {
        super(abstractContainerMenu, inventory, component);

        this.progressDrawable = TexturedProgressDrawable.of(
                GreenhouseUtil.GUI_TEXTURE,
                TexturedProgressDrawable.Direction.TOP,
                menu::getProgress
        ).setUV(176, 0);
        this.backgroundPanel = TextureDrawable.of(GreenhouseUtil.GUI_TEXTURE);
    }

    @Override
    public void renderForeground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        if (PROGRESS_OFFSET_RECT.contains(mousePos.sub(getPanelPos())))
            showTooltip(menu.isProgressFailed()
                    ? Component.translatable(
                            "gui.greenhouses.tooltip.growing_failed_%s".formatted(menu.getGrowFailState().ordinal())
                    )
                    : Component.translatable(
                            "gui.greenhouses.tooltip.progress",
                            "%.2f%%".formatted(menu.getProgress() * 100f)
                    )
            );

        super.renderForeground(guiGraphics, mousePos, dt);
    }

    @Override
    public void renderBackgroundContent(@NotNull ElGuiGraphics elGuiGraphics, @NotNull Vector2ic vector2ic, float v) {
        final var progressRectDrawer = elGuiGraphics.rect(PROGRESS_OFFSET_RECT.copy().move(getPanelPos()));

        progressDrawable.render(progressRectDrawer);

        if (menu.isProgressFailed())
            PROGRESS_FAILED_ICON.render(progressRectDrawer);

        if (menu.isGroundWet())
            WATER_ICON.render(elGuiGraphics.square(getPanelPos().add(29, 35), 16));
    }
}
