package io.github.aratakileo.greenhouses.recipe.greenhouse;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.aratakileo.greenhouses.util.RandomInt;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemWithRangeCount {
    public static final MapCodec<ItemWithRangeCount> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(holder -> holder.itemHolder),
            ExtraCodecs.intRange(0, 99)
                    .optionalFieldOf("min_count", -1)
                    .forGetter(holder -> holder.minCount),
            ExtraCodecs.intRange(0, 99)
                    .optionalFieldOf("max_count", -1)
                    .forGetter(holder -> holder.maxCount)
    ).apply(builder, ItemWithRangeCount::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemWithRangeCount> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ItemWithRangeCount decode(RegistryFriendlyByteBuf buffer) {
            return new ItemWithRangeCount(
                    ByteBufCodecs.holderRegistry(Registries.ITEM).decode(buffer),
                    buffer.readInt(),
                    buffer.readInt()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ItemWithRangeCount itemWithRangeCount) {
            ByteBufCodecs.holderRegistry(Registries.ITEM).encode(buffer, itemWithRangeCount.itemHolder);
            buffer.writeInt(itemWithRangeCount.minCount);
            buffer.writeInt(itemWithRangeCount.maxCount);
        }
    };

    final Holder<Item> itemHolder;
    final int minCount, maxCount;

    public ItemWithRangeCount(@NotNull Holder<Item> itemHolder, int minCount, int maxCount) {
        this.itemHolder = itemHolder;

        if (maxCount <= 0 && minCount <= 0) {
            this.minCount = 1;
            this.maxCount = 1;
            return;
        }

        if (maxCount > 0 && minCount == -1) {
            this.minCount = 1;
            this.maxCount = maxCount;
            return;
        }

        this.maxCount = Math.max(maxCount, minCount);
        this.minCount = minCount;
    }

    public @NotNull ItemStack asItemStack() {
        return new ItemStack(
                itemHolder.value(),
                minCount == maxCount ? minCount : RandomInt.get(minCount, maxCount)
        );
    }
}
