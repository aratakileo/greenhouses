package io.github.aratakileo.greenhouses.block.entity;

import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public final class BlockEntitiTypes {
    public static final BlockEntityType<GreenhouseBlockEntity> GREENHOUSE_BLOCK_ENTITY_TYPE = create(
            "greenhouse_entity",
            ModBlocks.GREENHOUSE,
            GreenhouseBlockEntity::new
    );
    public final static BlockEntityType<CokeFurnaceBlockEntity> COKE_FURNACE_BLOCK_ENTITY_TYPE = create(
            "coke_furnace_entity",
            ModBlocks.COKE_FURNACE,
            CokeFurnaceBlockEntity::new
    );

    private static  <T extends BlockEntity> @NotNull BlockEntityType<T> create(
            @NotNull String name,
            @NotNull Block block,
            @NotNull BlockEntityType.BlockEntitySupplier<T> constructor
    ) {
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Greenhouses.NAMESPACE.getIdentifier(name),
                BlockEntityType.Builder.of(constructor, block).build()
        );
    }

    public static void init() {

    }
}
