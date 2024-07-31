package io.github.aratakileo.greenhouses.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CokeFurnaceRecipe implements Recipe<SingleRecipeInput> {
    protected final Ingredient ingredient;
    protected final ItemStack result;
    protected final int duration, producedCreosote;

    public CokeFurnaceRecipe(
            @NotNull Ingredient ingredient,
            @NotNull ItemStack result,
            int duration,
            int producedCreosote
    ) {
        this.ingredient = ingredient;
        this.result = result;
        this.duration = duration;
        this.producedCreosote = producedCreosote;
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput recipeInput, @NotNull Level level) {
        return Arrays.stream(ingredient.getItems()).anyMatch(itemStack -> itemStack.is(recipeInput.getItem(0).getItem()));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput recipeInput, HolderLookup.Provider provider) {
        return getResultItem(provider).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getDuration() {
        return duration;
    }

    public int getProducedCreosote() {
        return producedCreosote;
    }

    public @NotNull ItemStack getResult() {
        return result;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<CokeFurnaceRecipe> getSerializer() {
        return RecipeSerializers.COKE_FURNACE_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<CokeFurnaceRecipe> getType() {
        return RecipeTypes.COKE_FURNACE_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<CokeFurnaceRecipe> {
        private final static MapCodec<CokeFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                ExtraCodecs.intRange(100, 20_000)
                        .optionalFieldOf("duration", 900)
                        .forGetter(CokeFurnaceRecipe::getDuration),
                ExtraCodecs.intRange(1, CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE)
                        .optionalFieldOf("produced_creosote", 250)
                        .forGetter(CokeFurnaceRecipe::getProducedCreosote)
        ).apply(builder, CokeFurnaceRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, CokeFurnaceRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull CokeFurnaceRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
                return new CokeFurnaceRecipe(
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        ItemStack.STREAM_CODEC.decode(buffer),
                        buffer.readInt(),
                        buffer.readInt()
                );
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull CokeFurnaceRecipe recipe) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                buffer.writeInt(recipe.duration);
                buffer.writeInt(recipe.producedCreosote);
            }
        };

        @Override
        public @NotNull MapCodec<CokeFurnaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CokeFurnaceRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
