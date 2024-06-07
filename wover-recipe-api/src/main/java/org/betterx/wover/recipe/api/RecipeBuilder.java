package org.betterx.wover.recipe.api;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.recipe.impl.CraftingRecipeBuilderImpl;
import org.betterx.wover.recipe.impl.RecipeRuntimeProviderImpl;
import org.betterx.wover.recipe.impl.SmithingRecipeBuilderImpl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class RecipeBuilder {
    public static final Event<OnBootstrapRecipes> BOOTSTRAP_RECIPES =
            RecipeRuntimeProviderImpl.BOOTSTRAP_RECIPES;

    public static final CraftingRecipeBuilder crafting(ResourceLocation id, ItemLike output) {
        return new CraftingRecipeBuilderImpl(id, output);
    }

    public static final SmithingRecipeBuilder smithing(ResourceLocation id, ItemLike output) {
        return new SmithingRecipeBuilderImpl(id, output);
    }
}
