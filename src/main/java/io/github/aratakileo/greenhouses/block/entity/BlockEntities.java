package io.github.aratakileo.greenhouses.block.entity;

import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.block.Blocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntities {
    public static final BlockEntityType<GreenhouseBlockEntity> GREENHOUSE_BLOCK_ENTITY_TYPE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Greenhouses.NAMESPACE.getIdentifier("greenhouse_entity"),
            BlockEntityType.Builder.of(GreenhouseBlockEntity::new, Blocks.GREENHOUSE).build()
    );

    public static void init() {

    }
}
