package io.github.aratakileo.greenhouses.gui.screen;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawable.TexturedProgressDrawable;
import io.github.aratakileo.elegantia.client.gui.screen.AbstractContainerScreen;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.greenhouses.gui.Textures;
import io.github.aratakileo.greenhouses.world.container.CokeFurnaceContainerMenu;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CokeFurnaceScreen extends AbstractContainerScreen<CokeFurnaceContainerMenu> {
    private final TexturedProgressDrawable fireProgressDrawable;

    private final static Rect2i PROGRESSBAR = Rect2i.square(CokeFurnaceUtil.PROGRESSBAR_POS, 16);

    public CokeFurnaceScreen(
            @NotNull CokeFurnaceContainerMenu abstractContainerMenu,
            @NotNull Inventory inventory,
            @NotNull Component component
    ) {
        super(abstractContainerMenu, inventory, component);

        this.fireProgressDrawable = TexturedProgressDrawable.of(
                Textures.FIRE_ICON.get(),
                TexturedProgressDrawable.Direction.TOP,
                menu::getProgress
        );
    }

    @Override
    public void renderForeground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        final var mouseOffset = mousePos.sub(getPanelPos());

        if (PROGRESSBAR.contains(mouseOffset))
            showTooltip(Component.translatable(
                    "gui.greenhouses.tooltip.progress",
                    "%.2f%%".formatted(menu.getProgress() * 100f)
            ));

        if (CokeFurnaceUtil.CREOSOTEBAR.contains(mouseOffset))
            showTooltip(CokeFurnaceUtil.getProducedCreosoteTooltip(menu.getProducedCreosote()));

        super.renderForeground(guiGraphics, mousePos, dt);
    }

    @Override
    public void renderBackgroundContent(@NotNull ElGuiGraphics elGuiGraphics, @NotNull Vector2ic vector2ic, float dt) {
        final var progressRectDrawer = elGuiGraphics.rect(PROGRESSBAR.copy().move(getPanelPos()));

        Textures.FIRE_BACKGROUND_ICON.get().render(progressRectDrawer);
        fireProgressDrawable.render(progressRectDrawer);

        CokeFurnaceUtil.renderCreosotebar(elGuiGraphics, getPanelPos(), menu.getProducedCreosoteScale());
    }
}
