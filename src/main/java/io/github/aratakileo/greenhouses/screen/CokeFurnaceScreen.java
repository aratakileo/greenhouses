package io.github.aratakileo.greenhouses.screen;

import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawable.TexturedProgressDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.client.gui.screen.AbstractContainerScreen;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.greenhouses.world.container.CokeFurnaceContainerMenu;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CokeFurnaceScreen extends AbstractContainerScreen<CokeFurnaceContainerMenu> {
    private final TexturedProgressDrawable progressDrawable;

    private final static Rect2i PROGRESS_OFFSET_RECT = Rect2i.ofSquare(62, 35, 16),
            PRODUCED_CREOSOTE_BAR = new Rect2i(162, 27, 9, 49);

    public CokeFurnaceScreen(
            @NotNull CokeFurnaceContainerMenu abstractContainerMenu,
            @NotNull Inventory inventory,
            @NotNull Component component
    ) {
        super(abstractContainerMenu, inventory, component);

        this.progressDrawable = TexturedProgressDrawable.of(
                CokeFurnaceUtil.GUI_TEXTURE,
                TexturedProgressDrawable.Direction.TOP,
                menu::getProgress
        ).setUV(176, 0);
        this.backgroundPanel = TextureDrawable.of(CokeFurnaceUtil.GUI_TEXTURE);
    }

    @Override
    public void renderForeground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        final var mouseOffset = new Vector2ic(mouseX, mouseY).sub(getPanelPos());

        if (PROGRESS_OFFSET_RECT.contains(mouseOffset))
            showTooltip(Component.translatable(
                    "gui.greenhouses.tooltip.progress",
                    "%.2f%%".formatted(menu.getProgress() * 100f)
            ));

        if (PRODUCED_CREOSOTE_BAR.contains(mouseOffset))
            showTooltip(Component.translatable(
                    "gui.greenhouses.tooltip.produced_creosote",
                    "%s/%s mB".formatted(menu.getProducedCreosote(), CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE)
            ));

        super.renderForeground(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackgroundContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        progressDrawable.render(new RectDrawer(
                guiGraphics,
                PROGRESS_OFFSET_RECT.copy().move(getPanelPos())
        ));

        renderProducedCreosoteBar(guiGraphics);
    }

    private void renderProducedCreosoteBar(@NotNull GuiGraphics guiGraphics) {
        final var innerArea = PRODUCED_CREOSOTE_BAR.copy()
                .move(getPanelPos().add(1))
                .expand(-2, -2);

        final var innerProgress = innerArea.copy().cutTop((int) (
                (float)innerArea.height * (1f - menu.getProducedCreosoteScale())
        ));

        new RectDrawer(guiGraphics, innerProgress).drawRgbGradient(
                0xffff00,
                0xff0000,
                RectDrawer.GradientDirection.DIAGONAL
        );

        for (var iy = 0; iy < CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE / 1000 - 1; iy++) {
            final var segmentY = innerArea.y + 3 + (iy * 4);

            RectDrawer.of(
                    guiGraphics,
                    innerArea.x,
                    segmentY,
                    3,
                    1
            ).drawRgb(0x373737);
        }
    }
}
