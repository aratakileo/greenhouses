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
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GreenhouseBlockEntityRenderer implements BlockEntityRenderer<GreenhouseBlockEntity> {
    private static final float GROUND_HEIGHT = 0.22f,
            PLANT_SCALE = 1f - GROUND_HEIGHT,
            GROUND_POS_OFFSET = 0.06f,
            PLANT_POS_OFFSET = GROUND_HEIGHT / 2f;

    private final BlockEntityRendererProvider.Context context;

    public GreenhouseBlockEntityRenderer(@NotNull BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(
            @NotNull GreenhouseBlockEntity greenhouseBlock,
            float dt,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int light,
            int overlay
    ) {
        final var groundItemStack = greenhouseBlock.getGroundItemStack();

        if (groundItemStack.isEmpty()) return;

        final var groundItem = asBlockItemOrThrow(groundItemStack.getItem());
        final var groundBlock = groundItem.getBlock();

        final var plantItemStack = greenhouseBlock.getPlantItemStack();
        final var blockRenderer = context.getBlockRenderDispatcher();

        poseStack.pushPose();
        poseStack.translate(GROUND_POS_OFFSET, GROUND_HEIGHT, GROUND_POS_OFFSET);
        poseStack.scale(1 - GROUND_POS_OFFSET, 0, 1 - GROUND_POS_OFFSET);

        blockRenderer.renderSingleBlock(groundBlock.defaultBlockState(), poseStack, multiBufferSource, light, overlay);

        poseStack.popPose();

        if (plantItemStack.isEmpty() || greenhouseBlock.growFailedByInvalidRecipe()) return;

        final var plantBlock = asBlockItemOrThrow(plantItemStack.getItem()).getBlock();

        final Optional<CropBlock> cropBlockContainer = plantBlock instanceof CropBlock cropBlock
                ? Optional.of(cropBlock)
                : Optional.empty();

        final var growScale = cropBlockContainer.isEmpty() ? greenhouseBlock.getProgress() : 1f;
        final var plantScale = PLANT_SCALE * growScale;
        final var posOffset = PLANT_POS_OFFSET + (0.5f - PLANT_POS_OFFSET) * (1f - growScale);

        poseStack.pushPose();
        poseStack.translate(posOffset, GROUND_HEIGHT, posOffset);
        poseStack.scale(plantScale, plantScale, plantScale);

        blockRenderer.renderSingleBlock(
                cropBlockContainer.map(
                        cropBlock -> getCropBlockState(cropBlock, greenhouseBlock.getProgress())
                ).orElseGet(
                        plantBlock::defaultBlockState
                ),
                poseStack,
                multiBufferSource,
                light,
                overlay
        );

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

    private static @NotNull BlockState getCropBlockState(@NotNull CropBlock cropBlock, float growProgress) {
        return cropBlock.defaultBlockState().setValue(
                getAgeProperty(cropBlock),
                (int)(((float)cropBlock.getMaxAge()) * growProgress)
        );
    }

    private static @NotNull IntegerProperty getAgeProperty(@NotNull CropBlock block) {
        /*
        *
        * It is necessary to obtain parameter keys in this way
        * because the block state parameters are checked not by the actual contents of the parameter keys,
        * but by the reference of the key instance.
        *
        * WARN: there may be problems because non-vanilla crops
        *
        */

        return switch (block.getMaxAge()) {
            case 1 -> BlockStateProperties.AGE_1;
            case 2 -> BlockStateProperties.AGE_2;
            case 3 -> BlockStateProperties.AGE_3;
            case 4 -> BlockStateProperties.AGE_4;
            case 5 -> BlockStateProperties.AGE_5;
            case 7 -> BlockStateProperties.AGE_7;
            case 15 -> BlockStateProperties.AGE_15;
            case 25 -> BlockStateProperties.AGE_25;
            default -> throw new IllegalStateException("Unexpected max age value: " + block.getMaxAge());
        };
    }
}
