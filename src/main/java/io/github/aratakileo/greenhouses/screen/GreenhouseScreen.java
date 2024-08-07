package io.github.aratakileo.greenhouses.screen;

import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawable.TexturedProgressDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.client.gui.screen.AbstractContainerScreen;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.greenhouses.world.container.GreenhouseContainerMenu;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GreenhouseScreen extends AbstractContainerScreen<GreenhouseContainerMenu> {
    private final TexturedProgressDrawable progressDrawable;

    private final static Rect2i PROGRESS_OFFSET_RECT = Rect2i.ofSquare(97, 35, 16);

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
    public void renderForeground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (PROGRESS_OFFSET_RECT.contains(new Vector2ic(mouseX, mouseY).sub(getPanelPos())))
            showTooltip(menu.isInvalidRecipe()
                    ? Component.translatable(
                            "gui.greenhouses.tooltip.growing_failed_%s".formatted(menu.getFailCode().ordinal())
                    )
                    : Component.translatable(
                            "gui.greenhouses.tooltip.progress",
                            "%.2f%%".formatted(menu.getProgress() * 100f)
                    )
            );

        super.renderForeground(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackgroundContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        final var progressRectDrawer = new RectDrawer(
                guiGraphics,
                PROGRESS_OFFSET_RECT.copy().move(getPanelPos())
        );

        progressDrawable.render(progressRectDrawer);

        if (menu.isInvalidRecipe())
            PROGRESS_FAILED_ICON.render(progressRectDrawer);

        if (menu.isGroundWet())
            WATER_ICON.render(RectDrawer.ofSquare(guiGraphics, getPanelPos().add(29, 35), 16));
    }
}
