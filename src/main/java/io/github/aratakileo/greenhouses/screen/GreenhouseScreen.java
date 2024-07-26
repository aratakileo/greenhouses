package io.github.aratakileo.greenhouses.screen;

import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.container.GreenhouseScreenContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GreenhouseScreen extends AbstractContainerScreen<GreenhouseScreenContainer> {
    private final static ResourceLocation TEXTURE = Greenhouses.NAMESPACE.getIdentifier("textures/gui/greenhouse.png");

    public final static int PROGRESS_X_OFFSET = 97,
            PROGRESS_Y_OFFSET = 35,
            WET_STATE_X_OFFSET = 29,
            WET_STATE_Y_OFFSET = PROGRESS_Y_OFFSET,
            BUCKET_ICON_X_OFFSET = 50,
            BUCKET_ICON_Y_OFFSET = PROGRESS_Y_OFFSET,
            PLANT_ICON_X_OFFSET = 70,
            PLANT_ICON_Y_OFFSET = 24,
            GROUND_ICON_X_OFFSET = PLANT_ICON_X_OFFSET,
            GROUND_ICON_Y_OFFSET = 45,
            ICON_SIZE = 16;

    private final static Rect2i PROGRESS_OFFSET_RECT = new Rect2i(PROGRESS_X_OFFSET, PROGRESS_Y_OFFSET, ICON_SIZE, ICON_SIZE);

    public GreenhouseScreen(
            @NotNull GreenhouseScreenContainer abstractContainerMenu,
            @NotNull Inventory inventory,
            @NotNull Component component
    ) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        int bgX = (width - imageWidth) / 2;
        int bgY = (height - imageHeight) / 2;

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
                            "gui.greenhouses.tooltip.grow_progress",
                            "%.2f%%".formatted(menu.getProgress() * 100f)
                    ),
                    mouseX,
                    mouseY
            );
    }

    private void renderGrowingProgress(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (menu.isGrowing()) guiGraphics.blit(
                TEXTURE,
                x + PROGRESS_X_OFFSET,
                y + PROGRESS_Y_OFFSET + ICON_SIZE - (int)(menu.getProgress() * ICON_SIZE),
                imageWidth,
                ICON_SIZE - (int)(menu.getProgress() * ICON_SIZE),
                ICON_SIZE,
                (int)(menu.getProgress() * ICON_SIZE)
        );
        else if (menu.isInvalidRecipe())
            guiGraphics.blit(
                    TEXTURE,
                    x + PROGRESS_X_OFFSET,
                    y + PROGRESS_Y_OFFSET,
                    imageWidth,
                    ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE
            );
    }

    private void renderWetState(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (menu.isGroundWet()) guiGraphics.blit(
                TEXTURE,
                x + WET_STATE_X_OFFSET,
                y + WET_STATE_Y_OFFSET,
                imageWidth,
                ICON_SIZE * 2,
                ICON_SIZE,
                ICON_SIZE
        );
    }

    private void renderSlotIcons(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (menu.isBucketSlotEmpty()) guiGraphics.blit(
                TEXTURE,
                x + BUCKET_ICON_X_OFFSET,
                y + BUCKET_ICON_Y_OFFSET,
                imageWidth,
                ICON_SIZE * 3,
                ICON_SIZE,
                ICON_SIZE
        );

        if (menu.isPlantSlotEmpty()) guiGraphics.blit(
                TEXTURE,
                x + PLANT_ICON_X_OFFSET,
                y + PLANT_ICON_Y_OFFSET,
                imageWidth,
                ICON_SIZE * 4,
                ICON_SIZE,
                ICON_SIZE
        );

        if (menu.isGroundSlotEmpty()) guiGraphics.blit(
                TEXTURE,
                x + GROUND_ICON_X_OFFSET,
                y + GROUND_ICON_Y_OFFSET,
                imageWidth,
                ICON_SIZE * 5,
                ICON_SIZE,
                ICON_SIZE
        );
    }
}
