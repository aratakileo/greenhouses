package io.github.aratakileo.greenhouses.screen.container;

import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.screen.CokeFurnaceScreen;
import io.github.aratakileo.greenhouses.screen.GreenhouseScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public final class ContainerMenus {
    public static final MenuType<GreenhouseContainerMenu> GREENHOUSE_CONTAINER_MENU = create(
            "greenhouse_menu",
            GreenhouseContainerMenu::new,
            GreenhouseScreen::new
    );
    public static final MenuType<CokeFurnaceContainerMenu> COKE_FURNACE_CONTAINER_MENU = create(
            "coke_furnace_menu",
            CokeFurnaceContainerMenu::new,
            CokeFurnaceScreen::new
    );

    private static <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> MenuType<T> create(
            @NotNull String name,
            @NotNull MenuType.MenuSupplier<T> menuSupplier,
            @NotNull MenuScreens.ScreenConstructor<T, S> screenConstructor
    ) {
        final var resourcePath = Greenhouses.NAMESPACE.getIdentifier(name);
        final var menuType = Registry.register(
                BuiltInRegistries.MENU,
                resourcePath,
                new MenuType<>(menuSupplier, FeatureFlags.DEFAULT_FLAGS)
        );

        MenuScreens.register(menuType, screenConstructor);

        Greenhouses.LOGGER.info("Register screen menu: {}", resourcePath);

        return menuType;
    }

    public static void init() {

    }
}
