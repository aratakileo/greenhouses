package io.github.aratakileo.greenhouses.screen;

import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.screen.container.CokeFurnaceContainerMenu;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CokeFurnaceScreen extends AbstractContainerScreen<CokeFurnaceContainerMenu> {
    private final static ResourceLocation TEXTURE = Greenhouses.NAMESPACE.getIdentifier("textures/gui/coke_furnace.png");

    private final static Rect2i PROGRESS_OFFSET_RECT = new Rect2i(
            CokeFurnaceUtil.PROGRESS_X_OFFSET,
            CokeFurnaceUtil.PROGRESS_Y_OFFSET,
            GreenhouseUtil.SLOT_ICON_SIZE,
            GreenhouseUtil.SLOT_ICON_SIZE
    ), PRODUCED_CREOSOTE_BAR = new Rect2i(
            CokeFurnaceUtil.CREOSOTE_SLOT_X_OFFSET + GreenhouseUtil.SLOT_ICON_SIZE + 5,
            25,
            9,
            49
    );

    public CokeFurnaceScreen(
            @NotNull CokeFurnaceContainerMenu abstractContainerMenu,
            @NotNull Inventory inventory,
            @NotNull Component component
    ) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderForeground(guiGraphics, mouseX, mouseY, delta);
    }

    protected void renderForeground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderTooltip(guiGraphics, mouseX, mouseY);

        final var bgX = (width - imageWidth) / 2;
        final var bgY = (height - imageHeight) / 2;

        final var mouseOffsetX = mouseX - bgX;
        final var mouseOffsetY = mouseY - bgY;

        if (PROGRESS_OFFSET_RECT.contains(mouseOffsetX, mouseOffsetY))
            guiGraphics.renderTooltip(
                    Minecraft.getInstance().font,
                    Component.translatable(
                            "gui.greenhouses.tooltip.progress",
                            "%.2f%%".formatted(menu.getProgress() * 100f)
                    ),
                    mouseX,
                    mouseY
            );

        if (PRODUCED_CREOSOTE_BAR.contains(mouseOffsetX, mouseOffsetY))
            guiGraphics.renderTooltip(
                    Minecraft.getInstance().font,
                    Component.translatable(
                            "gui.greenhouses.tooltip.produced_creosote",
                            "%s/%s mB".formatted(menu.getProducedCreosote(), CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE)
                    ),
                    mouseX,
                    mouseY
            );
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        final var bgX = (width - imageWidth) / 2;
        final var bgY = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, bgX, bgY, 0, 0, imageWidth, imageHeight);

        renderProgress(guiGraphics, bgX, bgY);
        renderSlotIcons(guiGraphics, bgX, bgY);
        renderProducedCreosoteBar(guiGraphics, bgX, bgY);
    }

    private void renderProgress(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (menu.getProgress() == 0f) return;

        guiGraphics.blit(
                TEXTURE,
                x + CokeFurnaceUtil.PROGRESS_X_OFFSET,
                y + CokeFurnaceUtil.PROGRESS_Y_OFFSET + GreenhouseUtil.SLOT_ICON_SIZE - (int)(
                        GreenhouseUtil.SLOT_ICON_SIZE * menu.getProgress()
                ),
                imageWidth,
                GreenhouseUtil.SLOT_ICON_SIZE - (int)(menu.getProgress() * GreenhouseUtil.SLOT_ICON_SIZE),
                GreenhouseUtil.SLOT_ICON_SIZE,
                (int)(menu.getProgress() * GreenhouseUtil.SLOT_ICON_SIZE)
        );
    }

    private void renderSlotIcons(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (menu.isIngredientSlotEmpty()) guiGraphics.blit(
                TEXTURE,
                x + CokeFurnaceUtil.INGREDIENT_SLOT_X_OFFSET,
                y + CokeFurnaceUtil.INGREDIENT_SLOT_Y_OFFSET,
                imageWidth,
                GreenhouseUtil.SLOT_ICON_SIZE,
                GreenhouseUtil.SLOT_ICON_SIZE,
                GreenhouseUtil.SLOT_ICON_SIZE
        );

        if (menu.isCreosoteSlotEmpty()) guiGraphics.blit(
                TEXTURE,
                x + CokeFurnaceUtil.CREOSOTE_SLOT_X_OFFSET,
                y + CokeFurnaceUtil.CREOSOTE_SLOT_Y_OFFSET,
                imageWidth,
                GreenhouseUtil.SLOT_ICON_SIZE * 2,
                GreenhouseUtil.SLOT_ICON_SIZE,
                GreenhouseUtil.SLOT_ICON_SIZE
        );
    }

    private void renderProducedCreosoteBar(@NotNull GuiGraphics guiGraphics, int x, int y) {
        final var innerProgressLeft = PRODUCED_CREOSOTE_BAR.getX() + x + 1;
        final var innerProgressTop = PRODUCED_CREOSOTE_BAR.getY() + y + 1;

        final var innerProgressWidth = PRODUCED_CREOSOTE_BAR.getWidth() - 2;
        final var innerProgressHeight = PRODUCED_CREOSOTE_BAR.getHeight() - 2;

        final var innerProgressBottom = innerProgressTop + innerProgressHeight;

        guiGraphics.fillGradient(
                innerProgressLeft,
                innerProgressBottom - (int)((float)innerProgressHeight * menu.getProducedCreosoteScale()),
                innerProgressLeft + innerProgressWidth,
                innerProgressBottom,
                0xffffff00,
                0xffff0000
        );

        for (var iy = 0; iy < CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE / 1000 - 1; iy++) {
            final var segmentY = innerProgressTop + 3 + (iy * 4);

            guiGraphics.fill(
                    innerProgressLeft,
                    segmentY,
                    innerProgressLeft + 3,
                    segmentY + 1,
                    0xff373737
            );
        }
    }
}
