package io.github.aratakileo.greenhouses;

import io.github.aratakileo.greenhouses.block.Blocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class CreativeModeTab {
    private final static ResourceLocation MOD_TAB_RESOURCE_LOCATION = Greenhouses.NAMESPACE.getIdentifier("item_group");
    public final static ResourceKey<net.minecraft.world.item.CreativeModeTab> MOD_TAB_RESOURCE_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), MOD_TAB_RESOURCE_LOCATION);
    public final static net.minecraft.world.item.CreativeModeTab MOD_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Blocks.GREENHOUSE))
            .title(Component.literal("Greenhouses"))
            .build();

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MOD_TAB_RESOURCE_KEY, MOD_TAB);

        ItemGroupEvents.modifyEntriesEvent(MOD_TAB_RESOURCE_KEY).register(tab -> Items.ITEMS.forEach(tab::accept));
    }
}
