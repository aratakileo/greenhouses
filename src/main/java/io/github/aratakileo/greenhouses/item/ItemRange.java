package io.github.aratakileo.greenhouses.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.aratakileo.greenhouses.util.RandomInt;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ItemRange {
    final Item item;
    final int minCount, maxCount;

    private ItemRange(@NotNull Item item, int minCount, int maxCount) {
        this.item = item;
        this.maxCount = maxCount;
        this.minCount = minCount;
    }

    public int getMinCount() {
        return minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getTotalCount() {
        return minCount == maxCount ? minCount : RandomInt.get(minCount, maxCount);
    }

    public @NotNull Item getItem() {
        return item;
    }

    public @NotNull Holder.Reference<Item> getItemHolder() {
        return BuiltInRegistries.ITEM.getHolder(BuiltInRegistries.ITEM.getId(item)).orElseThrow();
    }

    public @NotNull ItemStack getItemStack() {
        return new ItemStack(
                item,
                getTotalCount()
        );
    }

    public static @NotNull ItemRange of(@NotNull ItemStack itemStack) {
        return new ItemRange(itemStack.getItem(), itemStack.getCount(), itemStack.getCount());
    }

    public static @NotNull ItemRange of(@NotNull Holder<Item> itemHolder) {
        return of(itemHolder.value(), 1, 1);
    }

    public static @NotNull ItemRange of(@NotNull Holder<Item> itemHolder, int count) {
        return of(itemHolder.value(), count, count);
    }

    public static @NotNull ItemRange of(@NotNull Holder<Item> itemHolder, int minCount, int maxCount) {
        return of(itemHolder.value(), minCount, maxCount);
    }

    public static @NotNull ItemRange of(@NotNull Item item) {
        return of(item, 1, 1);
    }

    public static @NotNull ItemRange of(@NotNull Item item, int count) {
        return of(item, count, count);
    }

    public static @NotNull ItemRange of(@NotNull Item item, int minCount, int maxCount) {
        minCount = Math.max(minCount, 0);
        maxCount = Math.max(Math.max(maxCount, 1), minCount);

        return new ItemRange(item, minCount, maxCount);
    }

    private static @NotNull ItemRange decodeValues(@NotNull Holder<Item> itemHolder, int minCount, int maxCount) {
        if (maxCount == -1 && minCount == -1)
            return of(itemHolder, 1);

        if (minCount == -1)
            return of(itemHolder, 1, maxCount);

        if (maxCount == -1)
            return of(itemHolder, minCount);

        return of(itemHolder, minCount, maxCount);
    }

    public static @NotNull Codec<List<ItemRange>> getListCodec(int minSize, int maxSize) {
        return Codec.list(ItemRange.CODEC.codec(), minSize, maxSize);
    }

    public static final MapCodec<ItemRange> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(ItemRange::getItemHolder),
            ExtraCodecs.intRange(0, 99)
                    .optionalFieldOf("min_count", -1)
                    .forGetter(ItemRange::getMinCount),
            ExtraCodecs.intRange(0, 99)
                    .optionalFieldOf("max_count", -1)
                    .forGetter(ItemRange::getMaxCount)
    ).apply(builder, ItemRange::decodeValues));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemRange> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ItemRange decode(@NotNull RegistryFriendlyByteBuf buffer) {
            return ItemRange.of(
                    ByteBufCodecs.holderRegistry(Registries.ITEM).decode(buffer),
                    buffer.readInt(),
                    buffer.readInt()
            );
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull ItemRange itemRange) {
            ByteBufCodecs.holderRegistry(Registries.ITEM).encode(buffer, itemRange.getItemHolder());
            buffer.writeInt(itemRange.minCount);
            buffer.writeInt(itemRange.maxCount);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemRange>> LIST_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull List<ItemRange> decode(@NotNull RegistryFriendlyByteBuf buffer) {
            return buffer.readList(_buffer -> ItemRange.STREAM_CODEC.decode((RegistryFriendlyByteBuf) _buffer));
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull List<ItemRange> list) {
            buffer.writeCollection(
                    list,
                    (_buffer, value) -> ItemRange.STREAM_CODEC.encode((RegistryFriendlyByteBuf) _buffer, value)
            );
        }
    };
}
