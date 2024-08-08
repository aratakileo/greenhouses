package io.github.aratakileo.greenhouses;

import io.github.aratakileo.elegantia.core.Namespace;
import io.github.aratakileo.greenhouses.world.block.ModBlocks;
import io.github.aratakileo.greenhouses.world.block.entity.BlockEntityTypes;
import io.github.aratakileo.greenhouses.world.container.ContainerMenus;
import io.github.aratakileo.greenhouses.world.item.ItemGroup;
import io.github.aratakileo.greenhouses.world.item.ModItems;
import io.github.aratakileo.greenhouses.world.recipe.RecipeTypes;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Greenhouses implements ModInitializer {
    public static final Namespace NAMESPACE = Namespace.of("greenhouses");
    public static final Logger LOGGER = NAMESPACE.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("Running {} initialization", NAMESPACE.getModOrThrow().getName());

        // Initialization must take place in this order, otherwise the universe will collapse
        ModItems.init();
        ModBlocks.init();
        ItemGroup.init();
        BlockEntityTypes.init();
        ContainerMenus.init();
        RecipeTypes.init();
    }
}
