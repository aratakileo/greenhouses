package io.github.aratakileo.greenhouses.world.item;

import io.github.aratakileo.elegantia.util.RegistriesUtil;
import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.world.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

public final class ItemGroup {
    public final static net.minecraft.world.item.CreativeModeTab ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.GREENHOUSE))
            .title(Component.literal(Greenhouses.NAMESPACE.getModOrThrow().getName()))
            .build();

    public final static ResourceKey<net.minecraft.world.item.CreativeModeTab> ITEM_GROUP_RESOURCE
            = RegistriesUtil.registerItemGroupResource(ITEM_GROUP, Greenhouses.NAMESPACE);

    public static void init() {
        RegistriesUtil.registerItemGroupListener(ITEM_GROUP_RESOURCE, group -> ModItems.ITEMS.forEach(group::accept));
    }
}
