package org.betterx.wover.biome.api;

import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.event.OnBootstrapBiomes;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.impl.BiomeManagerImpl;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BiomeManager {
    public static final Event<OnBootstrapRegistry<Biome>> BOOTSTRAP_BIOME_REGISTRY
            = BiomeManagerImpl.BOOTSTRAP_BIOME_REGISTRY;
    public static final Event<OnBootstrapBiomes> BOOTSTRAP_BIOMES_WITH_DATA
            = BiomeManagerImpl.BOOTSTRAP_BIOMES_WITH_DATA;

    public static BiomeKey<BiomeBuilder.Vanilla> vanilla(ResourceLocation location) {
        return BiomeManagerImpl.vanilla(location);
    }

    public static BiomeKey<BiomeBuilder.Wrapped> wrapped(@NotNull ResourceKey<Biome> key) {
        return BiomeManagerImpl.wrapped(key);
    }

    /**
     * Get {@link BiomeData} for biome. This Method will use the {@link WorldState} to get the
     * {@link BiomeDataRegistry} and then get the {@link BiomeData} for the given biome.
     *
     * @param biome - {@link Holder<Biome>} from.
     * @return {@link BiomeData} or null if it was not found.
     */
    public static @Nullable BiomeData biomeDataForHolder(Holder<Biome> biome) {
        return biomeDataForHolder(WorldState.registryAccess(), biome);
    }

    /**
     * Get {@link BiomeData} for biome. This Method will use the {@link RegistryAccess} to get the
     * {@link BiomeDataRegistry} and then get the {@link BiomeData} for the given biome.
     *
     * @param acc   The RegistryAccess
     * @param biome - {@link Holder<Biome>} from.
     * @return {@link BiomeData} or null if it was not found.
     */
    public static @Nullable BiomeData biomeDataForHolder(RegistryAccess acc, Holder<Biome> biome) {
        if (acc != null) {
            final Registry<BiomeData> reg = acc.registryOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
            ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
            if (id != null) {
                return reg.get(id);
            }
        }
        return null;
    }
}
