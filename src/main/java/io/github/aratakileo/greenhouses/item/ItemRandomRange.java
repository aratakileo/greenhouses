package io.github.aratakileo.greenhouses.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ItemRandomRange {
    private final RandomSource randomSource = RandomSource.create();
    private final Item item;
    private final int minCount, maxCount;

    private ItemRandomRange(@NotNull Item item, int minCount, int maxCount) {
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
        return minCount == maxCount ? minCount : randomSource.nextIntBetweenInclusive(minCount, maxCount);
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

    public static @NotNull ItemRandomRange of(@NotNull ItemStack itemStack) {
        return new ItemRandomRange(itemStack.getItem(), itemStack.getCount(), itemStack.getCount());
    }

    public static @NotNull ItemRandomRange of(@NotNull Holder<Item> itemHolder) {
        return of(itemHolder.value(), 1, 1);
    }

    public static @NotNull ItemRandomRange of(@NotNull Holder<Item> itemHolder, int count) {
        return of(itemHolder.value(), count, count);
    }

    public static @NotNull ItemRandomRange of(@NotNull Holder<Item> itemHolder, int minCount, int maxCount) {
        return of(itemHolder.value(), minCount, maxCount);
    }

    public static @NotNull ItemRandomRange of(@NotNull Item item) {
        return of(item, 1, 1);
    }

    public static @NotNull ItemRandomRange of(@NotNull Item item, int count) {
        return of(item, count, count);
    }

    public static @NotNull ItemRandomRange of(@NotNull Item item, int minCount, int maxCount) {
        minCount = Math.max(minCount, 0);
        maxCount = Math.max(Math.max(maxCount, 1), minCount);

        return new ItemRandomRange(item, minCount, maxCount);
    }

    private static @NotNull ItemRandomRange decodeValues(@NotNull Holder<Item> itemHolder, int minCount, int maxCount) {
        if (maxCount == -1 && minCount == -1)
            return of(itemHolder, 1);

        if (minCount == -1)
            return of(itemHolder, 1, maxCount);

        if (maxCount == -1)
            return of(itemHolder, minCount);

        return of(itemHolder, minCount, maxCount);
    }

    public static @NotNull Codec<List<ItemRandomRange>> getListCodec(int minSize, int maxSize) {
        return Codec.list(ItemRandomRange.CODEC.codec(), minSize, maxSize);
    }

    public static final MapCodec<ItemRandomRange> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(ItemRandomRange::getItemHolder),
            ExtraCodecs.intRange(0, 99)
                    .optionalFieldOf("min_count", -1)
                    .forGetter(ItemRandomRange::getMinCount),
            ExtraCodecs.intRange(0, 99)
                    .optionalFieldOf("max_count", -1)
                    .forGetter(ItemRandomRange::getMaxCount)
    ).apply(builder, ItemRandomRange::decodeValues));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemRandomRange> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ItemRandomRange decode(@NotNull RegistryFriendlyByteBuf buffer) {
            return ItemRandomRange.of(
                    ByteBufCodecs.holderRegistry(Registries.ITEM).decode(buffer),
                    buffer.readInt(),
                    buffer.readInt()
            );
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull ItemRandomRange itemRandomRange) {
            ByteBufCodecs.holderRegistry(Registries.ITEM).encode(buffer, itemRandomRange.getItemHolder());
            buffer.writeInt(itemRandomRange.minCount);
            buffer.writeInt(itemRandomRange.maxCount);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemRandomRange>> LIST_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull List<ItemRandomRange> decode(@NotNull RegistryFriendlyByteBuf buffer) {
            return buffer.readList(_buffer -> ItemRandomRange.STREAM_CODEC.decode((RegistryFriendlyByteBuf) _buffer));
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull List<ItemRandomRange> list) {
            buffer.writeCollection(
                    list,
                    (_buffer, value) -> ItemRandomRange.STREAM_CODEC.encode((RegistryFriendlyByteBuf) _buffer, value)
            );
        }
    };
}
