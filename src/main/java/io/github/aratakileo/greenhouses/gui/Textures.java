package io.github.aratakileo.greenhouses.gui;

import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.core.TextureProvider;
import io.github.aratakileo.elegantia.util.type.InitOnGet;
import io.github.aratakileo.greenhouses.Greenhouses;

public interface Textures {
    TextureProvider PROVIDER = Greenhouses.NAMESPACE.getTextureProvider();

    InitOnGet<TextureDrawable> DROPS_ICON = PROVIDER.defineGuiIcon("state/drops.png"),
            DROPS_BACKGROUND_ICON = PROVIDER.defineGuiIcon("state/drops_bg.png"),
            FIRE_ICON = PROVIDER.defineGuiIcon("state/fire.png"),
            FIRE_BACKGROUND_ICON = PROVIDER.defineGuiIcon("state/fire_bg.png"),
            LEAF_ICON = PROVIDER.defineGuiIcon("state/leaf.png"),
            LEAF_BACKGROUND_ICON = PROVIDER.defineGuiIcon("state/leaf_bg.png");
}
