package io.github.aratakileo.greenhouses.world.container;

import io.github.aratakileo.elegantia.util.RegistriesUtil;
import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.screen.CokeFurnaceScreen;
import io.github.aratakileo.greenhouses.screen.GreenhouseScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public final class ContainerMenus {
    public static final MenuType<GreenhouseContainerMenu> GREENHOUSE_CONTAINER_MENU = register(
            "greenhouse_menu",
            GreenhouseContainerMenu::new,
            GreenhouseScreen::new
    );
    public static final MenuType<CokeFurnaceContainerMenu> COKE_FURNACE_CONTAINER_MENU = register(
            "coke_furnace_menu",
            CokeFurnaceContainerMenu::new,
            CokeFurnaceScreen::new
    );

    private static <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> MenuType<T> register(
            @NotNull String name,
            @NotNull MenuType.MenuSupplier<T> menuSupplier,
            @NotNull MenuScreens.ScreenConstructor<T, S> screenConstructor
    ) {
        return RegistriesUtil.registerMenuType(Greenhouses.NAMESPACE.getLocation(name), menuSupplier, screenConstructor);
    }

    public static void init() {

    }
}
