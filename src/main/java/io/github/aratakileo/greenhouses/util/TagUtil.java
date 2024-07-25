package io.github.aratakileo.greenhouses.util;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public final class TagUtil {
    public static <T> @NotNull HashSet<T> getElementsFromTag(
            @NotNull DefaultedRegistry<T> registry,
            @NotNull TagKey<T> tag
    ) {
        final var elements = new HashSet<T>();

        for (final var holder: registry.getTagOrEmpty(tag))
            elements.add(holder.value());

        return elements;
    }

    public static @NotNull HashSet<Item> getItemsFromTag(@NotNull TagKey<Item> tag) {
        return getElementsFromTag(BuiltInRegistries.ITEM, tag);
    }
}
