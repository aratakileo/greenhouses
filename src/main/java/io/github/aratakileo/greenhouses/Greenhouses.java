package io.github.aratakileo.greenhouses;

import io.github.aratakileo.greenhouses.block.ModBlocks;
import io.github.aratakileo.greenhouses.block.entity.BlockEntitiTypes;
import io.github.aratakileo.greenhouses.item.CreativeModeTab;
import io.github.aratakileo.greenhouses.item.ModItems;
import io.github.aratakileo.greenhouses.recipe.RecipeTypes;
import io.github.aratakileo.greenhouses.screen.container.ContainerMenus;
import io.github.aratakileo.greenhouses.util.Namespace;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Greenhouses implements ModInitializer {
    public static final Namespace NAMESPACE = Namespace.of("greenhouses");
    public static final Logger LOGGER = NAMESPACE.getLogger();

    @Override
    public void onInitialize() {
        // Initialization must take place in this order, otherwise the universe will collapse
        ModItems.init();
        ModBlocks.init();
        CreativeModeTab.init();
        BlockEntitiTypes.init();
        ContainerMenus.init();
        RecipeTypes.init();
    }
}
