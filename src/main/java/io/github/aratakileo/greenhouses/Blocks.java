package io.github.aratakileo.greenhouses;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class Blocks {
    public static final ArrayList<Block> BLOCKS = new ArrayList<>();

    public static final Block TREATED_PLANKS = createBlock("treated_planks");
    public static final Block TREATED_PLANKS_FAKE = createBlock("treated_planks_fake");
    public static final Block GREENHOUSE = createBlock(
            new TransparentBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BROWN)
                            .instrument(NoteBlockInstrument.HAT)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.CANDLE)
                            .isValidSpawn(net.minecraft.world.level.block.Blocks::never)
                            .isRedstoneConductor(net.minecraft.world.level.block.Blocks::never)
                            .isSuffocating(net.minecraft.world.level.block.Blocks::never)
                            .isViewBlocking(net.minecraft.world.level.block.Blocks::never)
                            .forceSolidOn()
                            .noOcclusion()
            ),
            "greenhouse",
            true
    );

    private static Block createBlock(@NotNull String name) {
        return createBlock(name, false);
    }

    private static Block createBlock(@NotNull String name, boolean translucent) {
        return createBlock(name, net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.properties(), translucent);
    }

    private static Block createBlock(@NotNull String name, @NotNull BlockBehaviour.Properties properties, boolean translucent) {
        final var block = new Block(properties);
        return createBlock(block, name, translucent);
    }

    private static Block createBlock(@NotNull Block block, @NotNull String name, boolean translucent) {
        final var resourceLocation = ResourceLocation.fromNamespaceAndPath(
                GreenhousesInitializer.NAMESPACE_OR_MODID,
                name
        );
        final var item = new BlockItem(block, new Item.Properties());

        Registry.register(BuiltInRegistries.BLOCK, resourceLocation, block);
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);

        if (translucent) BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());

        Items.ITEMS.add(item);
        BLOCKS.add(block);
        GreenhousesInitializer.LOGGER.info("Register block: {}", resourceLocation);

        return block;
    }

    public static void init() {

    }
}
