package io.github.aratakileo.greenhouses;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreenhousesInitializer implements ModInitializer {
    public static final String NAMESPACE_OR_MODID = "greenhouses";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE_OR_MODID);

    @Override
    public void onInitialize() {
        // Initialization must take place in this order, otherwise the universe will collapse
        Items.init();
        Blocks.init();
        CreativeModeTab.init();
    }
}
