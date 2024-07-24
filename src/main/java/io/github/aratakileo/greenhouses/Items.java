package io.github.aratakileo.greenhouses;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class Items {
    public final static ArrayList<Item> ITEMS = new ArrayList<>();

    public final static Item CREOSOTE_BUCKET = createItem(
            new BucketItem(
                    Fluids.WATER,
                    new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)
            ),
            "creosote_bucket"
    );
    public final static Item COKE_BRICK = createItem(
            new Item(new Item.Properties()),
            "coke_brick"
    );
    public final static Item COKE_COAL = createItem(
            new Item(new Item.Properties()),
            "coked_coal"
    );

    private static Item createItem(@NotNull Item item, @NotNull String name) {
        final var resourceLocation = ResourceLocation.fromNamespaceAndPath(GreenhousesInitializer.NAMESPACE_OR_MODID, name);

        Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);

        ITEMS.add(item);
        GreenhousesInitializer.LOGGER.info("Register item: {}", resourceLocation);

        return item;
    }

    public static void init() {
        FuelRegistry.INSTANCE.add(COKE_COAL, 3200);
    }
}
