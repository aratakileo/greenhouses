package io.github.aratakileo.greenhouses.block.entity;

import io.github.aratakileo.greenhouses.util.Classes;
import io.github.aratakileo.greenhouses.util.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public abstract class ContainerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    protected final Block block;

    protected final int firstInputSlots;

    protected NonNullList<ItemStack> items;

    protected ContainerBlockEntity(
            @NotNull BlockEntityType<?> blockEntityType,
            @NotNull BlockPos blockPos,
            @NotNull BlockState blockState,
            @NotNull Block block,
            int slots,
            int firstInputSlots
    ) {
        super(blockEntityType, blockPos, blockState);
        this.block = block;
        this.items = NonNullList.withSize(slots, ItemStack.EMPTY);
        this.firstInputSlots = firstInputSlots;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return block.getName();
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> newItems) {
        items = newItems;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public int @NotNull[] getSlotsForFace(@NotNull Direction direction) {
        int[] result = new int[items.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }

        return result;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotIndex, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return slotIndex < firstInputSlots;
    }

    @Override
    public boolean canTakeItemThroughFace(int slotIndex, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return slotIndex >= firstInputSlots;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);

        for (final var field: this.getClass().getDeclaredFields())
            if (field.isAnnotationPresent(CompoundDataField.class)) {
                if (hasNotCompoundDataFieldSupportedType(field))
                    throw new RuntimeException(
                            "Compound data field `%s` has unsupported type `%s`".formatted(
                                    Classes.getFieldView(field),
                                    field.getType()
                            )
                    );

                final var attributeName = getCompoundAttributeName(field);

                try {
                    if (field.getType() == boolean.class)
                        compoundTag.putBoolean(attributeName, field.getBoolean(this));
                    else compoundTag.putInt(attributeName, field.getInt(this));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

        ContainerHelper.saveAllItems(compoundTag, items, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        ContainerHelper.loadAllItems(compoundTag, items, provider);

        for (final var field: this.getClass().getDeclaredFields())
            if (field.isAnnotationPresent(CompoundDataField.class)) {
                if (hasNotCompoundDataFieldSupportedType(field))
                    throw new RuntimeException(
                            "Compound data field `%s` has unsupported type `%s`".formatted(
                                    Classes.getFieldView(field),
                                    field.getType()
                            )
                    );

                final var attributeName = getCompoundAttributeName(field);

                try {
                    if (field.getType() == boolean.class)
                        field.setBoolean(this, compoundTag.getBoolean(attributeName));
                    else field.setInt(this, compoundTag.getInt(attributeName));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    private String getCompoundAttributeName(@NotNull Field field) {
        return "%s.%s".formatted(
                BuiltInRegistries.BLOCK.getKey(block).toString(),
                Strings.camelToSnake(field.getName())
        );
    }

    private static boolean hasNotCompoundDataFieldSupportedType(@NotNull Field field) {
        return field.getType() == boolean.class || field.getType() == Integer.class;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CompoundDataField {}
}
