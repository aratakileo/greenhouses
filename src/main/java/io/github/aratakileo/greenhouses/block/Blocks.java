package io.github.aratakileo.greenhouses.block;

import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.item.Items;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class Blocks {
    public static final ArrayList<Block> BLOCKS = new ArrayList<>();

    public static final BlockSetType TREATED_BLOCK_SET_TYPE = new BlockSetType("treated");
    public static final WoodType TREATED_WOOD_TYPE = new WoodType("treated", TREATED_BLOCK_SET_TYPE);

    public static final Block GROUT = createBlock(
            new ColoredFallingBlock(new ColorRGBA(0xA1A3A3), net.minecraft.world.level.block.Blocks.CLAY.properties()),
            "grout"
    );
    public static final Block COKE_BRICKS = createBlock(
            new Block(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS.properties()),
            "coke_bricks"
    );
    public static final Block COKE_BRICK_SLAB = createBlock(
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(COKE_BRICKS)),
            "coke_brick_slab"
    );
    public static final Block COKE_BRICK_STAIRS = createBlock(
            new StairBlock(COKE_BRICKS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(COKE_BRICKS)),
            "coke_brick_stairs"
    );
    public static final Block COKE_BRICK_WALL = createBlock(
            new WallBlock(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICK_WALL.properties()),
            "coke_brick_wall"
    );
    public static final Block COKE_FURNACE = createBlock(
            new FurnaceBlock(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS.properties()),
            "coke_furnace"
    );

    public static final Block TREATED_PLANKS = createBlock(
            new Block(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.properties()),
            "treated_planks"
    );
    public static final Block TREATED_SLAB = createBlock(
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TREATED_PLANKS)),
            "treated_slab"
    );
    public static final Block TREATED_STAIRS = createBlock(
            new StairBlock(TREATED_PLANKS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(TREATED_PLANKS)),
            "treated_stairs"
    );
    public static final Block TREATED_FENCE = createBlock(
            new FenceBlock(net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE.properties()),
            "treated_fence"
    );
    public static final Block TREATED_FENCE_GATE = createBlock(
            new FenceGateBlock(
                    TREATED_WOOD_TYPE,
                    net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE_GATE.properties()
            ),
            "treated_fence_gate"
    );
    public static final Block TREATED_PRESSURE_PLATE = createBlock(
            new PressurePlateBlock(
                    TREATED_BLOCK_SET_TYPE,
                    net.minecraft.world.level.block.Blocks.DARK_OAK_PRESSURE_PLATE.properties()
            ),
            "treated_pressure_plate"
    );
    public static final Block TREATED_BUTTON = createBlock(
            new ButtonBlock(
                    TREATED_BLOCK_SET_TYPE,
                    30,
                    net.minecraft.world.level.block.Blocks.DARK_OAK_BUTTON.properties()
            ),
            "treated_button"
    );

    public static final Block GREENHOUSE = createBlock(
            new GreenhouseBlock(
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

    private static Block createBlock(@NotNull String name, @NotNull BlockBehaviour.Properties properties, boolean translucent) {
        final var block = new Block(properties);
        return createBlock(block, name, translucent);
    }

    private static Block createBlock(@NotNull Block block, @NotNull String name) {
        return createBlock(block, name, false);
    }

    private static Block createBlock(@NotNull Block block, @NotNull String name, boolean translucent) {
        final var resourceLocation = Greenhouses.NAMESPACE.getIdentifier(name);
        final var item = new BlockItem(block, new Item.Properties());

        Registry.register(BuiltInRegistries.BLOCK, resourceLocation, block);
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);

        if (translucent) BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());

        Items.ITEMS.add(item);
        BLOCKS.add(block);
        Greenhouses.LOGGER.info("Register block: {}", resourceLocation);

        return block;
    }

    public static void init() {

    }
}
