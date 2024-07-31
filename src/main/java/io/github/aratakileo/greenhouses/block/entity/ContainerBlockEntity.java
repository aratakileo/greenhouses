package io.github.aratakileo.greenhouses.block.entity;

import io.github.aratakileo.greenhouses.ContainerAutoData;
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

    protected NonNullList<ItemStack> items;

    protected ContainerBlockEntity(
            @NotNull BlockEntityType<?> blockEntityType,
            @NotNull BlockPos blockPos,
            @NotNull BlockState blockState,
            @NotNull Block block,
            int slots
    ) {
        super(blockEntityType, blockPos, blockState);
        this.block = block;
        this.items = NonNullList.withSize(slots, ItemStack.EMPTY);
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
    protected void saveAdditional(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);

        for (final var field: this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CompoundDataField.class))
                saveFieldData(compoundTag, this, field, null);
        }

        ContainerHelper.saveAllItems(compoundTag, items, provider);
    }

    private void saveFieldData(
            @NotNull CompoundTag compoundTag,
            @NotNull Object instance,
            @NotNull Field field,
            @Nullable String prefix
    ) {
        field.setAccessible(true);

        if (hasUnsupportedType(field))
            throw new RuntimeException("Compound data field `%s` has unsupported type `%s`".formatted(
                    Classes.getFieldView(field),
                    field.getType().getName()
            ));

        final var attributeName = getCompoundAttributeName(field, prefix);

        try {
            if (field.getType() == boolean.class)
                compoundTag.putBoolean(attributeName, field.getBoolean(instance));
            else if (field.getType() == int.class)
                compoundTag.putInt(attributeName, field.getInt(instance));
            else for (final var subfield: ((ContainerAutoData)field.get(instance)).fields)
                saveFieldData(compoundTag, field.get(instance), subfield, field.getName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        ContainerHelper.loadAllItems(compoundTag, items, provider);

        for (final var field: this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CompoundDataField.class))
                loadFieldData(compoundTag, this, field, null);
        }
    }

    private void loadFieldData(
            @NotNull CompoundTag compoundTag,
            @NotNull Object instance,
            @NotNull Field field,
            @Nullable String prefix
    ) {
        field.setAccessible(true);

        if (hasUnsupportedType(field))
            throw new RuntimeException("Compound data field `%s` has unsupported type `%s`".formatted(
                    Classes.getFieldView(field),
                    field.getType().getName()
            ));

        final var attributeName = getCompoundAttributeName(field, prefix);

        try {
            if (field.getType() == boolean.class)
                field.setBoolean(instance, compoundTag.getBoolean(attributeName));
            else if (field.getType() == int.class)
                field.setInt(instance, compoundTag.getInt(attributeName));
            else for (final var subfield: ((ContainerAutoData)field.get(instance)).fields)
                loadFieldData(compoundTag, field.get(instance), subfield, field.getName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCompoundAttributeName(@NotNull Field field, @Nullable String prefix) {
        return "%s.%s".formatted(
                BuiltInRegistries.BLOCK.getKey(block).toString(),
                (
                        prefix != null && !prefix.isEmpty() ? "%s.".formatted(Strings.camelToSnake(prefix)) : ""
                ) + Strings.camelToSnake(field.getName())
        );
    }

    private static boolean hasUnsupportedType(@NotNull Field field) {
        return field.getType() != boolean.class
                && field.getType() != int.class
                && !ContainerAutoData.class.isAssignableFrom(field.getType());
    }



    @Override
    public abstract int @NotNull[] getSlotsForFace(@NotNull Direction direction);

    @Override
    public abstract boolean canTakeItemThroughFace(
            int slotIndex,
            @NotNull ItemStack itemStack,
            @NotNull Direction direction
    );

    @Override
    public abstract boolean canPlaceItemThroughFace(
            int slotIndex,
            @NotNull ItemStack itemStack,
            @Nullable Direction direction
    );

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CompoundDataField {}
}
