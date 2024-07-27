package io.github.aratakileo.greenhouses;

import io.github.aratakileo.greenhouses.block.Blocks;
import io.github.aratakileo.greenhouses.block.entity.BlockEntities;
import io.github.aratakileo.greenhouses.item.CreativeModeTab;
import io.github.aratakileo.greenhouses.item.Items;
import io.github.aratakileo.greenhouses.recipe.RecipeTypes;
import io.github.aratakileo.greenhouses.screen.ScreenMenus;
import io.github.aratakileo.greenhouses.util.Namespace;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Greenhouses implements ModInitializer {
    public static final Namespace NAMESPACE = Namespace.of("greenhouses");
    public static final Logger LOGGER = NAMESPACE.getLogger();

    @Override
    public void onInitialize() {
        // Initialization must take place in this order, otherwise the universe will collapse
        Items.init();
        Blocks.init();
        CreativeModeTab.init();
        BlockEntities.init();
        ScreenMenus.init();
        RecipeTypes.init();
    }
}
