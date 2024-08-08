package io.github.aratakileo.greenhouses.world.block.entity;

import io.github.aratakileo.elegantia.util.RegistriesUtil;
import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.world.block.ModBlocks;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public final class BlockEntityTypes {
    public static final BlockEntityType<GreenhouseBlockEntity> GREENHOUSE_BLOCK_ENTITY_TYPE = regiser(
            "greenhouse_entity",
            ModBlocks.GREENHOUSE,
            GreenhouseBlockEntity::new
    );
    public final static BlockEntityType<CokeFurnaceBlockEntity> COKE_FURNACE_BLOCK_ENTITY_TYPE = regiser(
            "coke_furnace_entity",
            ModBlocks.COKE_FURNACE,
            CokeFurnaceBlockEntity::new
    );

    private static  <T extends BlockEntity> @NotNull BlockEntityType<T> regiser(
            @NotNull String name,
            @NotNull Block block,
            @NotNull BlockEntityType.BlockEntitySupplier<T> constructor
    ) {
        return RegistriesUtil.registerBlockEntityType(Greenhouses.NAMESPACE.getLocation(name), block, constructor);
    }

    public static void init() {
        BlockEntityRenderers.register(GREENHOUSE_BLOCK_ENTITY_TYPE, GreenhouseBlockEntityRenderer::new);
    }
}
