package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import java.util.concurrent.CompletableFuture;

public abstract class WoverRecipeProvider implements WoverDataProvider<FabricRecipeProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    public WoverRecipeProvider(
            ModCore modCore,
            String title
    ) {
        this.title = title;
        this.modCore = modCore;
    }

    /**
     * Called, when the Recipes need to be created and added
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrap(RecipeOutput context);

    @Override
    public FabricRecipeProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FabricRecipeProvider(output, registriesFuture) {
            @Override
            public void buildRecipes(RecipeOutput exporter) {
                bootstrap(exporter);
            }

            @Override
            protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
                return identifier;
            }
        };
    }
}
