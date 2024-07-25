package io.github.aratakileo.greenhouses.util;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class GreenhouseUtil {
    private static HashSet<Item> GROUNDS = null, PLANTS = null;

    public static @NotNull HashSet<Item> getGrounds() {
        if (GROUNDS == null) {
            GROUNDS = TagUtil.getItemsFromTag(ItemTags.DIRT);
            GROUNDS.add(Items.FARMLAND);
            GROUNDS.add(Items.SAND);
            GROUNDS.add(Items.SOUL_SAND);
        }

        return GROUNDS;
    }

    public static boolean isGround(@NotNull Item item) {
        return getGrounds().contains(item);
    }

    public static @NotNull HashSet<Item> getPlants() {
        if (PLANTS == null) {
            PLANTS = TagUtil.getItemsFromTag(ItemTags.SAPLINGS);
            PLANTS.addAll(TagUtil.getItemsFromTag(ItemTags.VILLAGER_PLANTABLE_SEEDS));
            PLANTS.add(Items.CACTUS);
            PLANTS.add(Items.SUGAR_CANE);
            PLANTS.add(Items.NETHER_WART);
            PLANTS.add(Items.BAMBOO);
        }

        return PLANTS;
    }

    public static boolean isPlant(@NotNull Item item) {
        return getPlants().contains(item);
    }
}
