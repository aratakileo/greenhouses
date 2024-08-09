package io.github.aratakileo.greenhouses.world.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;

public class GreenhouseBlockEntityRenderer implements BlockEntityRenderer<GreenhouseBlockEntity> {
    private static final float GROUND_HEIGHT = 0.22f;

    private final BlockEntityRendererProvider.Context context;

    public GreenhouseBlockEntityRenderer(@NotNull BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(
            @NotNull GreenhouseBlockEntity block,
            float dt,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int light,
            int overlay
    ) {
        final var groundItemStack = block.getGroundItemStack();

        if (groundItemStack.isEmpty()) return;

        final var groundItem = asBlockItemOrThrow(groundItemStack.getItem());
        final var groundBlock = groundItem.getBlock();

        final var plantItemStack = block.getPlantItemStack();
        final var blockRenderer = context.getBlockRenderDispatcher();

        poseStack.pushPose();
        poseStack.translate(0.06f, GROUND_HEIGHT, 0.06f);
        poseStack.scale(0.94f, 0, 0.94f);

        blockRenderer.renderSingleBlock(groundBlock.defaultBlockState(), poseStack, multiBufferSource, light, overlay);

        poseStack.popPose();

        if (plantItemStack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(GROUND_HEIGHT / 2f, 0.22f, GROUND_HEIGHT / 2f);
        poseStack.scale(1f - GROUND_HEIGHT, 1f - GROUND_HEIGHT, 1f - GROUND_HEIGHT);

        final var plantBlock = asBlockItemOrThrow(plantItemStack.getItem()).getBlock();

        blockRenderer.renderSingleBlock(plantBlock.defaultBlockState(), poseStack, multiBufferSource, light, overlay);

        poseStack.popPose();
    }

    private int getLight(@NotNull Level level, @NotNull BlockPos blockPos) {
        return LightTexture.pack(
                level.getBrightness(LightLayer.BLOCK, blockPos),
                level.getBrightness(LightLayer.SKY, blockPos)
        );
    }

    private static @NotNull BlockItem asBlockItemOrThrow(@NotNull Item item) {
        if (item instanceof BlockItem blockItem)
            return blockItem;

        throw new IllegalStateException("item %s is not a block item".formatted(
                BuiltInRegistries.ITEM.getKey(item)
        ));
    }
}
