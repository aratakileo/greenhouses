package io.github.aratakileo.greenhouses.recipe.greenhouse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.aratakileo.greenhouses.recipe.ItemRange;
import io.github.aratakileo.greenhouses.recipe.MultyOutputRecipe;
import io.github.aratakileo.greenhouses.recipe.RecipeSerializers;
import io.github.aratakileo.greenhouses.recipe.RecipeTypes;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GreenhouseRecipe extends MultyOutputRecipe<GreenhouseRecipeInput> {
    protected final Ingredient plant;
    protected final Ingredient ground;
    protected final List<ItemRange> result;
    protected final boolean needsWetGround;
    protected final int growthRate;

    public GreenhouseRecipe(
            @NotNull Ingredient plant,
            @NotNull Ingredient ground,
            @NotNull List<ItemRange> result,
            boolean needsWetGround,
            int growthRate
    ) {
        this.plant = plant;
        this.ground = ground;
        this.result = result;
        this.needsWetGround = needsWetGround;
        this.growthRate = growthRate;
    }

    private static boolean match(@NotNull Ingredient ingredient, @NotNull ItemStack inputStack) {
        return Arrays.stream(ingredient.getItems()).anyMatch(
                itemStack -> ItemStack.isSameItemSameComponents(
                            itemStack,
                            inputStack
                    ) && inputStack.getDamageValue() == itemStack.getDamageValue()
        );
    }

    @Override
    public boolean matches(@NotNull GreenhouseRecipeInput recipeInput, @NotNull Level level) {
        return !plant.isEmpty()
                && !ground.isEmpty()
                && recipeInput.isGroundWet() == needsWetGround()
                && match(plant, recipeInput.plantInput())
                && match(ground, recipeInput.groundInput());
    }

    @Override
    public @NotNull List<ItemStack> getResultItems() {
        return result.stream().map(ItemRange::getItemStack).toList();
    }

    @Override
    public @NotNull RecipeSerializer<GreenhouseRecipe> getSerializer() {
        return RecipeSerializers.GREENHOUSE_RECIPE_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<GreenhouseRecipe> getType() {
        return RecipeTypes.GREENHOUSE_RECIPE_TYPE;
    }

    public boolean needsWetGround() {
        return needsWetGround;
    }

    public int getGrowthRate() {
        return growthRate;
    }

    public @NotNull Ingredient getPlant() {
        return plant;
    }

    public @NotNull Ingredient getGround() {
        return ground;
    }

    public static class Serializer implements RecipeSerializer<GreenhouseRecipe> {
        private static final MapCodec<GreenhouseRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Ingredient.CODEC.fieldOf("plant").forGetter(GreenhouseRecipe::getPlant),
                Ingredient.CODEC.fieldOf("ground").forGetter(GreenhouseRecipe::getGround),
                ItemRange.getListCodec(1, GreenhouseUtil.OUTPUT_SLOTS)
                        .fieldOf("result")
                        .forGetter(recipe -> recipe.result),
                Codec.BOOL.optionalFieldOf(
                        "needs_wet_ground",
                        false
                ).forGetter(GreenhouseRecipe::needsWetGround),
                Codec.INT.optionalFieldOf(
                        "growth_rate",
                        GreenhouseUtil.RECIPE_DEFAULT_GROWTH_RATE
                ).forGetter(GreenhouseRecipe::getGrowthRate)
        ).apply(builder, GreenhouseRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, GreenhouseRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull GreenhouseRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
                return new GreenhouseRecipe(
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        ItemRange.LIST_STREAM_CODEC.decode(buffer),
                        buffer.readBoolean(),
                        buffer.readInt()
                );
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull GreenhouseRecipe recipe) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.plant);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ground);
                ItemRange.LIST_STREAM_CODEC.encode(buffer, recipe.result);
                buffer.writeBoolean(recipe.needsWetGround);
                buffer.writeInt(recipe.growthRate);
            }
        };

        @Override
        public @NotNull MapCodec<GreenhouseRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, GreenhouseRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
