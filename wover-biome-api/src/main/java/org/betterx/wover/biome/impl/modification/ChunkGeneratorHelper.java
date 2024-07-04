package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.mixin.ChunkGeneratorAccessor;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;

import com.google.common.base.Suppliers;

import java.util.List;
import java.util.function.Function;

public class ChunkGeneratorHelper {
    public static void rebuildFeaturesPerStep(ChunkGenerator generator, BiomeSource biomeSource) {
        if (generator instanceof ChunkGeneratorAccessor acc) {
            Function<Holder<Biome>, BiomeGenerationSettings> function
                    = (Holder<Biome> hh) -> hh.value().getGenerationSettings();

            acc.wover_setFeaturesPerStep(Suppliers.memoize(() -> FeatureSorter.buildFeaturesPerStep(
                    List.copyOf(biomeSource.possibleBiomes()),
                    (hh) -> function.apply(hh).features(),
                    true
            )));
        }
    }
}
