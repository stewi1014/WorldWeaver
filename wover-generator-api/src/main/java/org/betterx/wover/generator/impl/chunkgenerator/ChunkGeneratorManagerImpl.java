package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.biome.mixin.ChunkGeneratorAccessor;
import org.betterx.wover.config.api.Configs;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.generator.api.chunkgenerator.ChunkGeneratorManager;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.state.api.WorldConfig;
import org.betterx.wover.state.api.WorldState;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelStorageSource;

import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkGeneratorManagerImpl {
    private static final ResourceLocation LEGACY_ID = LegacyHelper.BCLIB_CORE.convertNamespace(WoverChunkGenerator.ID);

    private static final List<String> GENERATOR_IDS = new ArrayList<>(1);

    /**
     * We need this for mods that use the DSL system to actually fix the generator data.
     * The DSL will check if the generator type is actually known, so we need to inject
     * our own generator into the DSL system.
     *
     * @param map A DSL map, that could contain anything. The Method checks if it contains
     *            the flat generator ("minecraft:flat") and if so, it will inject the
     *            Wover generator into the map
     * @return The altered map
     */
    @ApiStatus.Internal
    public static Map<String, Supplier<TypeTemplate>> addGeneratorDSL(Map<String, Supplier<TypeTemplate>> map) {
        if (map.containsKey("minecraft:flat") && !ModCore.isDatagen()) {
            Map<String, Supplier<TypeTemplate>> nMap = new HashMap<>(map);
            GENERATOR_IDS.forEach(id -> nMap.put(id, DSL::remainder));
            return ImmutableMap.copyOf(nMap);
        }
        return map;
    }

    @ApiStatus.Internal
    public static void initialize() {
        register(WoverChunkGenerator.ID, WoverChunkGenerator.CODEC);
        if (LegacyHelper.isLegacyEnabled()) {
            register(LEGACY_ID, LegacyHelper.wrap(WoverChunkGenerator.CODEC));
        }
        WorldConfig.registerMod(LibWoverWorldGenerator.C);

        WorldLifecycle.CREATED_NEW_WORLD_FOLDER.subscribe(ChunkGeneratorManagerImpl::onWorldCreation, ChunkGeneratorManager.CREATE_DIMENSION_CONFIG_PRIORITY);
    }

    private static void onWorldCreation(
            LevelStorageSource.LevelStorageAccess storage,
            RegistryAccess access,
            Holder<WorldPreset> currentPreset,
            WorldDimensions dimensions,
            boolean recreated
    ) {
        WorldGeneratorConfigImpl.createWorldConfig(currentPreset, dimensions);
    }

    public static void onWorldReCreate(
            LevelStorageSource.LevelStorageAccess storage,
            WorldCreationContext context
    ) {
        final var configuredPreset = WorldGeneratorConfigImpl.getPresetsNbtFromFolder(storage);
        final var dimensions = WorldGeneratorConfigImpl.loadWorldDimensions(
                WorldState.allStageRegistryAccess(),
                configuredPreset
        );

        for (var dimEntry : context.selectedDimensions().dimensions().entrySet()) {
            final var refDim = dimensions.get(dimEntry.getKey());
            if (refDim instanceof ConfiguredChunkGenerator refGen
                    && refGen.wover_getConfiguredWorldPreset() != null
                    && dimEntry.getValue().generator() instanceof ConfiguredChunkGenerator loadGen
                    && loadGen.wover_getConfiguredWorldPreset() == null) {
                loadGen.wover_setConfiguredWorldPreset(refGen.wover_getConfiguredWorldPreset());
            }
        }
    }

    public static void register(ResourceLocation location, MapCodec<? extends ChunkGenerator> codec) {
        final String idString = location.toString();
        if (GENERATOR_IDS.contains(idString)) {
            throw new IllegalStateException("Duplicate generator id: " + idString);
        }
        GENERATOR_IDS.add(idString);
        BuiltInRegistryManager.register(BuiltInRegistries.CHUNK_GENERATOR, location, codec);
    }

    public static String enumerateFeatureNamespaces(@NotNull ChunkGenerator chunkGenerator) {
        if (chunkGenerator instanceof ChunkGeneratorAccessor acc) {
            Supplier<List<FeatureSorter. StepFeatureData>> supplier = null; // acc.wover_getFeaturesPerStep();
            if (supplier != null) {
                final HashMap<String, Integer> namespaces = new HashMap<>();
                try {
                    List<FeatureSorter.StepFeatureData> list = supplier.get();
                    if (list != null) {
                        for (var features : list)
                            if (features != null) {
                                for (PlacedFeature feature : features.features()) {
                                    if (feature != null) {
                                        String namespace = null;
                                        if (WorldState.registryAccess() != null) {
                                            final ResourceLocation location = WorldState.registryAccess()
                                                                                        .registryOrThrow(Registries.PLACED_FEATURE)
                                                                                        .getKey(feature);
                                            if (location != null) {
                                                namespace = location.getNamespace();
                                            }
                                        }
                                        if (namespace == null
                                                && feature.feature() != null
                                                && feature.feature().unwrapKey().isPresent()) {
                                            namespace = feature
                                                    .feature()
                                                    .unwrapKey()
                                                    .get()
                                                    .location()
                                                    .getNamespace();
                                        }

                                        if (namespace == null) {
                                            namespace = "none";
                                        }

                                        namespaces.put(namespace, namespaces.getOrDefault(namespace, 0) + 1);
                                    }
                                }
                            }
                    }
                } catch (Throwable e) {
                    LibWoverWorldGenerator.C.log.warn("Failed to enumerate feature namespaces", e);
                }
                return namespaces.entrySet()
                                 .stream()
                                 .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
                                 .reduce((a, b) -> a + ", " + b)
                                 .orElse("none");
            }
        }
        return "unknown";
    }

    public static String printGeneratorInfo(@Nullable String className, @NotNull ChunkGenerator generator) {
        StringBuilder sb = new StringBuilder();
        sb.append(className == null ? generator.getClass().getSimpleName() : className)
          .append(" (")
          .append(Integer.toHexString(generator.hashCode()))
          .append(")");

        if (generator instanceof ConfiguredChunkGenerator cfg) {
            final var preset = cfg.wover_getConfiguredWorldPreset();
            sb.append("\n    preset     = ").append(preset == null ? "none" : preset.location());
        }

        if (generator instanceof NoiseBasedChunkGenerator noise) {
            final var key = noise.generatorSettings().unwrapKey();
            sb.append("\n    noise      = ").append(key.isEmpty() ? "custom" : key.get().location());
        }

        if (generator instanceof ChunkGeneratorAccessor) {
            sb.append("\n    features   = ").append(enumerateFeatureNamespaces(generator));
        }

        return sb.toString();
    }

    public static void printDimensionInfo(WorldDimensions dimensionRegistry) {
        if (!Configs.MAIN.verboseLogging.get()) return;

        printDimensionInfo("World Dimensions", dimensionRegistry.dimensions().entrySet());
    }

    public static void printDimensionInfo(Registry<LevelStem> dimensionRegistry) {
        if (!Configs.MAIN.verboseLogging.get()) return;

        printDimensionInfo("World Dimensions", dimensionRegistry.entrySet());
    }

    public static void printDimensionInfo(String title, WorldDimensions dimensionRegistry) {
        printDimensionInfo(title, dimensionRegistry.dimensions().entrySet());
    }

    public static void printDimensionInfo(String title, Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>> levels) {
        StringBuilder output = new StringBuilder(title + ": ");
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : levels) {
            output.append("\n - ").append(entry.getKey().location()).append(": ")
                  .append("\n     ").append(
                          entry.getValue()
                               .generator()
                               .toString()
                               .replace("\n", "\n     ")
                  )
                  .append("\n     ")
                  .append(
                          entry.getValue()
                               .generator()
                               .getBiomeSource()
                               .toString()
                               .replace("\n", "\n     ")
                  );
        }

        LibWoverWorldGenerator.C.log.info(output.toString());
    }
}
