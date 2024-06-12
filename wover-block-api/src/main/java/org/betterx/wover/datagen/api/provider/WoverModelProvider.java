package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.impl.ModelProviderExclusions;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class WoverModelProvider implements WoverDataProvider<FabricModelProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    public WoverModelProvider(ModCore modCore) {
        this(modCore, modCore.namespace);
    }

    public WoverModelProvider(ModCore modCore, String title) {
        this.modCore = modCore;
        this.title = title;
    }

    protected void addFromRegistry(
            WoverBlockModelGenerators generator,
            BlockRegistry registry,
            boolean validate
    ) {
        addFromRegistry(generator, registry, validate, List.of());
    }

    protected void addFromRegistry(
            WoverBlockModelGenerators generator,
            BlockRegistry registry,
            boolean validate,
            List<Block> ignoreBlocks
    ) {
        registry
                .allBlocks()
                .forEach(block -> {
                    if (block instanceof BlockModelProvider bmp && !ignoreBlocks.contains(block))
                        bmp.provideBlockModels(generator);
                    else if (validate)
                        ModelProviderExclusions.excludeFromBlockModelValidation(block);

                    if (!validate)
                        ModelProviderExclusions.excludeFromBlockModelValidation(block);
                });
    }

    protected abstract void bootstrapBlockStateModels(WoverBlockModelGenerators generator);
    protected abstract void bootstrapItemModels(ItemModelGenerators itemModelGenerator);

    @Override
    public FabricModelProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FabricModelProvider(output) {
            @Override
            public String getName() {
                return super.getName() + " - " + title;
            }

            @Override
            public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
                bootstrapBlockStateModels(new WoverBlockModelGenerators(blockStateModelGenerator));
            }

            @Override
            public void generateItemModels(ItemModelGenerators itemModelGenerator) {
                bootstrapItemModels(itemModelGenerator);
            }
        };
    }
}
