package io.github.aratakileo.greenhouses.screen;

import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.screen.container.GreenhouseContainerMenu;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GreenhouseScreen extends AbstractContainerScreen<GreenhouseContainerMenu> {
    private final static ResourceLocation TEXTURE = Greenhouses.NAMESPACE.getIdentifier("textures/gui/greenhouse.png");

    private final static Rect2i PROGRESS_OFFSET_RECT = new Rect2i(
            GreenhouseUtil.PROGRESS_X_OFFSET,
            GreenhouseUtil.PROGRESS_Y_OFFSET,
            GreenhouseUtil.SLOT_ICON_SIZE,
            GreenhouseUtil.SLOT_ICON_SIZE
    );

    public GreenhouseScreen(
            @NotNull GreenhouseContainerMenu abstractContainerMenu,
            @NotNull Inventory inventory,
            @NotNull Component component
    ) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        final var bgX = (width - imageWidth) / 2;
        final var bgY = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, bgX, bgY, 0, 0, imageWidth, imageHeight);
        renderGrowingProgress(guiGraphics, bgX, bgY);
        renderWetState(guiGraphics, bgX, bgY);
        renderSlotIcons(guiGraphics, bgX, bgY);

        if (PROGRESS_OFFSET_RECT.contains(mouseX - bgX, mouseY - bgY))
            guiGraphics.renderTooltip(
                    Minecraft.getInstance().font,
                    menu.isInvalidRecipe() ? Component.translatable(
                            "gui.greenhouses.tooltip.growing_failed_%s".formatted(menu.getFailCode())
                    ) : Component.translatable(
                            "gui.greenhouses.tooltip.progress",
                            "%.2f%%".formatted(menu.getProgress() * 100f)
                    ),
                    mouseX,
                    mouseY
            );
    }

    private void renderGrowingProgress(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (menu.getProgress() > 0f) guiGraphics.blit(
                TEXTURE,
                x + GreenhouseUtil.PROGRESS_X_OFFSET,
                y + GreenhouseUtil.PROGRESS_Y_OFFSET + GreenhouseUtil.SLOT_ICON_SIZE - (int)(
                        menu.getProgress() * GreenhouseUtil.SLOT_ICON_SIZE
                ),
                imageWidth,
                GreenhouseUtil.SLOT_ICON_SIZE - (int)(menu.getProgress() * GreenhouseUtil.SLOT_ICON_SIZE),
                GreenhouseUtil.SLOT_ICON_SIZE,
                (int)(menu.getProgress() * GreenhouseUtil.SLOT_ICON_SIZE)
        );

        else if (menu.isInvalidRecipe())
            guiGraphics.blit(
                    TEXTURE,
                    x + GreenhouseUtil.PROGRESS_X_OFFSET,
                    y + GreenhouseUtil.PROGRESS_Y_OFFSET,
                    imageWidth,
                    GreenhouseUtil.SLOT_ICON_SIZE,
                    GreenhouseUtil.SLOT_ICON_SIZE,
                    GreenhouseUtil.SLOT_ICON_SIZE
            );
    }

    private void renderWetState(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (menu.isGroundWet()) guiGraphics.blit(
                TEXTURE,
                x + GreenhouseUtil.WET_STATE_X_OFFSET,
                y + GreenhouseUtil.WET_STATE_Y_OFFSET,
                imageWidth,
                GreenhouseUtil.SLOT_ICON_SIZE * 2,
                GreenhouseUtil.SLOT_ICON_SIZE,
                GreenhouseUtil.SLOT_ICON_SIZE
        );
    }

    private void renderSlotIcons(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (menu.isBucketSlotEmpty()) guiGraphics.blit(
                TEXTURE,
                x + GreenhouseUtil.WATER_SLOT_X_OFFSET,
                y + GreenhouseUtil.WATER_SLOT_Y_OFFSET,
                imageWidth,
                GreenhouseUtil.SLOT_ICON_SIZE * 3,
                GreenhouseUtil.SLOT_ICON_SIZE,
                GreenhouseUtil.SLOT_ICON_SIZE
        );

        if (menu.isPlantSlotEmpty()) guiGraphics.blit(
                TEXTURE,
                x + GreenhouseUtil.PLANT_SLOT_X_OFFSET,
                y + GreenhouseUtil.PLANT_SLOT_Y_OFFSET,
                imageWidth,
                GreenhouseUtil.SLOT_ICON_SIZE * 4,
                GreenhouseUtil.SLOT_ICON_SIZE,
                GreenhouseUtil.SLOT_ICON_SIZE
        );

        if (menu.isGroundSlotEmpty()) guiGraphics.blit(
                TEXTURE,
                x + GreenhouseUtil.GROUND_SLOT_X_OFFSET,
                y + GreenhouseUtil.GROUND_SLOT_Y_OFFSET,
                imageWidth,
                GreenhouseUtil.SLOT_ICON_SIZE * 5,
                GreenhouseUtil.SLOT_ICON_SIZE,
                GreenhouseUtil.SLOT_ICON_SIZE
        );
    }
}
