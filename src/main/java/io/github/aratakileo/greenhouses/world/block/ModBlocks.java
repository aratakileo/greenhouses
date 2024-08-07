package io.github.aratakileo.greenhouses.world.block;

import io.github.aratakileo.elegantia.client.ObjectRenderTypes;
import io.github.aratakileo.elegantia.util.RegistriesUtil;
import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.world.item.ModItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public final class ModBlocks {
    public static final BlockSetType TREATED_BLOCK_SET_TYPE = new BlockSetType("treated");
    public static final WoodType TREATED_WOOD_TYPE = new WoodType("treated", TREATED_BLOCK_SET_TYPE);

    public static final Block GROUT = registerBlock(
            "grout",
            new ColoredFallingBlock(new ColorRGBA(0xA1A3A3), net.minecraft.world.level.block.Blocks.CLAY.properties())
    );
    public static final Block COKE_BRICKS = registerBlock(
            "coke_bricks",
            new Block(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS.properties())

    );
    public static final Block COKE_BRICK_SLAB = registerBlock(
            "coke_brick_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(COKE_BRICKS))
    );
    public static final Block COKE_BRICK_STAIRS = registerBlock(
            "coke_brick_stairs",
            new StairBlock(COKE_BRICKS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(COKE_BRICKS))
    );
    public static final Block COKE_BRICK_WALL = registerBlock(
            "coke_brick_wall",
            new WallBlock(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICK_WALL.properties())
    );
    public static final Block COKE_FURNACE = registerBlock(
            "coke_furnace",
            new CokeFurnaceBlock(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS.properties())
    );

    public static final Block TREATED_PLANKS = registerBlock(
            "treated_planks",
            new Block(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.properties())

    );
    public static final Block TREATED_SLAB = registerBlock(
            "treated_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TREATED_PLANKS))
    );
    public static final Block TREATED_STAIRS = registerBlock(
            "treated_stairs",
            new StairBlock(TREATED_PLANKS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(TREATED_PLANKS))
    );
    public static final Block TREATED_FENCE = registerBlock(
            "treated_fence",
            new FenceBlock(net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE.properties())
    );
    public static final Block TREATED_FENCE_GATE = registerBlock(
            "treated_fence_gate",
            new FenceGateBlock(
                    TREATED_WOOD_TYPE,
                    net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE_GATE.properties()
            )
    );
    public static final Block TREATED_PRESSURE_PLATE = registerBlock(
            "treated_pressure_plate",
            new PressurePlateBlock(
                    TREATED_BLOCK_SET_TYPE,
                    net.minecraft.world.level.block.Blocks.DARK_OAK_PRESSURE_PLATE.properties()
            )
    );
    public static final Block TREATED_BUTTON = registerBlock(
            "treated_button",
            new ButtonBlock(
                    TREATED_BLOCK_SET_TYPE,
                    30,
                    net.minecraft.world.level.block.Blocks.DARK_OAK_BUTTON.properties()
            )
    );

    public static final Block GREENHOUSE = registerBlock(
            "greenhouse",
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
            )
    );

    private static @NotNull Block registerBlock(@NotNull String name, @NotNull Block block) {
        RegistriesUtil.registerBlock(Greenhouses.NAMESPACE.getLocation(name), block);
        ModItems.ITEMS.add(block.asItem());

        return block;
    }

    public static void init() {
        ObjectRenderTypes.putBlock(GREENHOUSE, RenderType.translucent());
    }
}
