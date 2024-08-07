package io.github.aratakileo.greenhouses.world.item;

import io.github.aratakileo.elegantia.util.RegistriesUtil;
import io.github.aratakileo.elegantia.world.item.FuelRegistry;
import io.github.aratakileo.greenhouses.Greenhouses;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class ModItems {
    public final static ArrayList<Item> ITEMS = new ArrayList<>();

    public final static Item CREOSOTE_BUCKET = registerItem(
            "creosote_bucket",
            new BucketItem(
                    Fluids.WATER,
                    new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)
            )
    );
    public final static Item COKE_BRICK = registerItem(
            "coke_brick",
            new Item(new Item.Properties())
    );
    public final static Item COKED_COAL = registerItem(
            "coked_coal",
            new Item(new Item.Properties())
    );

    private static Item registerItem(@NotNull String name, @NotNull Item item) {
        ITEMS.add(item);

        return RegistriesUtil.registerItem(Greenhouses.NAMESPACE.getLocation(name), item);
    }

    public static void init() {
        FuelRegistry.add(COKED_COAL, 3200);
    }
}
