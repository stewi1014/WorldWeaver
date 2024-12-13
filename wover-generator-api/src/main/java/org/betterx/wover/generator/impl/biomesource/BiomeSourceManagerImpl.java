package org.betterx.wover.generator.impl.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.config.api.DatapackConfigs;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;
import org.betterx.wover.generator.impl.biomesource.end.TheEndBiomesHelper;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;
import org.betterx.wover.util.ResourceLocationSet;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;

public class BiomeSourceManagerImpl {
    public static final ResourceLocation BIOME_CONFIG_FILE = LibWoverWorldGenerator.C.id("biome_config.json");
    public static final ResourceLocation MINECRAFT_WILDCARD
            = ResourceLocationSet.WildcardResourceLocation.forAllFrom(IntegrationCore.MINECRAFT);
    public static final String BIOME_EXCLUSION_TAG = "exclude";
    public static final String NO_FABRIC_REGISTER_TAG = "no_fabric_register";
    public static final String END_CATCH_ALL = "*:is_end";
    public static final String NETHER_CATCH_ALL = "*:is_nether";

    public static void register(ResourceLocation location, MapCodec<? extends BiomeSource> codec) {
        BuiltInRegistryManager.register(BuiltInRegistries.BIOME_SOURCE, location, codec);
    }

    @ApiStatus.Internal
    public static void initialize() {
        register(LibWoverWorldGenerator.C.id("nether_biome_source"), WoverNetherBiomeSource.CODEC);

        register(LibWoverWorldGenerator.C.id("end_biome_source"), WoverEndBiomeSource.CODEC);

        if (LegacyHelper.isLegacyEnabled()) {
            register(
                    LegacyHelper.BCLIB_CORE.id("nether_biome_source"),
                    LegacyHelper.wrap(WoverNetherBiomeSource.CODEC)
            );
            register(LegacyHelper.BCLIB_CORE.id("end_biome_source"), LegacyHelper.wrap(WoverEndBiomeSource.CODEC));
        }

        WorldLifecycle.RESOURCES_LOADED.subscribe(BiomeSourceManagerImpl::onResourcesLoaded);

        DatapackRegistryBuilder.onElementLoad(
                BiomeDataRegistry.BIOME_DATA_REGISTRY,
                BiomeSourceManagerImpl::didLoadBiomeData
        );
    }

    private static void didLoadBiomeData(ResourceKey<BiomeData> biomeDataKey, BiomeData biomeData) {
        if (biomeDataKey.location().getNamespace().equals("minecraft")) return;

        final ResourceKey<Biome> biomeKey = BiomeDataRegistry.createBiomeKey(biomeDataKey);
        if (!FABRIC_EXCLUDES.contains(biomeKey.location())) {
            if (biomeData.isIntendedFor(BiomeTags.IS_NETHER)) {
                for (var param : biomeData.generationData.parameterPoints()) {
                    if (!NetherBiomes.canGenerateInNether(biomeKey)) {
                        LibWoverWorldGenerator.C.log.verbose("Adding Nether Biome to Fabric: " + biomeKey.location() + " (" + param + ")");
                        NetherBiomes.addNetherBiome(biomeKey, param);
                    }
                }
            } else if (!TheEndBiomesHelper.canGenerateInEnd(biomeKey)) {
                if (biomeData.isIntendedFor(CommonBiomeTags.IS_END_LAND) || biomeData.isIntendedFor(BiomeTags.IS_END)) {
                    if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                        LibWoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                        TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 1.0f));
                    }
                    if (!TheEndBiomesHelper.canGenerateAsEndMidlands(biomeKey)) {
                        LibWoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                        TheEndBiomes.addMidlandsBiome(biomeKey, biomeKey, genChance(biomeData, 0.5f));
                    }
                } else if (biomeData.isIntendedFor(CommonBiomeTags.IS_END_HIGHLAND)) {
                    if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                        LibWoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                        TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 1.0f));
                    }
                } else if (biomeData.isIntendedFor(CommonBiomeTags.IS_END_CENTER)) {
                    if (!TheEndBiomesHelper.canGenerateAsMainIslandBiome(biomeKey)) {
                        LibWoverWorldGenerator.C.log.verbose("Adding End Center Biome to Fabric: " + biomeKey.location());
                        TheEndBiomes.addMainIslandBiome(biomeKey, genChance(biomeData, 1.0f));
                    }
                } else if (biomeData.isIntendedFor(CommonBiomeTags.IS_SMALL_END_ISLAND)) {
                    if (!TheEndBiomesHelper.canGenerateAsSmallIslandsBiome(biomeKey)) {
                        LibWoverWorldGenerator.C.log.verbose("Adding Small End Island Biome to Fabric: " + biomeKey.location());
                        TheEndBiomes.addSmallIslandsBiome(biomeKey, genChance(biomeData, 1.0f));
                    }
                } else if (biomeData.isIntendedFor(CommonBiomeTags.IS_END_MIDLAND)) {
                    if (!TheEndBiomesHelper.canGenerateAsEndMidlands(biomeKey)) {
                        if (biomeData instanceof WoverBiomeData woverData && woverData.parent != null) {
                            LibWoverWorldGenerator.C.log.verbose("Adding End Midland Biome to Fabric: " + biomeKey.location());
                            TheEndBiomes.addMidlandsBiome(woverData.parent, biomeKey, woverData.genChance);
                        } else if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                            LibWoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                            TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 0.5f));
                        }
                    }
                } else if (biomeData.isIntendedFor(CommonBiomeTags.IS_END_BARRENS)) {
                    if (!TheEndBiomesHelper.canGenerateAsEndBarrens(biomeKey)) {
                        if (biomeData instanceof WoverBiomeData woverData && woverData.parent != null) {
                            LibWoverWorldGenerator.C.log.verbose("Adding End Barrens Biome to Fabric: " + biomeKey.location());
                            TheEndBiomes.addBarrensBiome(woverData.parent, biomeKey, woverData.genChance);
                        } else if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                            LibWoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                            TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 0.33f));
                        }
                    }
                } else if (biomeData.isIntendedFor(BiomeTags.IS_END)) {
                    if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                        LibWoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                        TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 1.0f));
                    }
                }
            }
        }
    }

    private static float genChance(BiomeData data, float defaultChance) {
        return data instanceof WoverBiomeData woverData ? woverData.genChance : defaultChance;
    }

    private static final Map<TagKey<Biome>, Set<ResourceLocation>> EXCLUSIONS = new HashMap<>();
    private static final Set<ResourceLocation> FABRIC_EXCLUDES = new ResourceLocationSet();

    public static void onResourcesLoaded(ResourceManager resourceManager) {
        EXCLUSIONS.clear();
        FABRIC_EXCLUDES.clear();

        //ensure vanilla biomes will not be registered with fabric
        FABRIC_EXCLUDES.add(MINECRAFT_WILDCARD);

        DatapackConfigs
                .instance()
                .runForResource(resourceManager, BIOME_CONFIG_FILE, BiomeSourceManagerImpl::processBiomeConfigs);

        if (WorldState.registryAccess() != null && !EXCLUSIONS.isEmpty()) {
            WorldState.registryAccess()
                      .registry(Registries.LEVEL_STEM)
                      .ifPresent(levelStems -> levelStems.holders().forEach(holder -> {
                          if (holder.isBound()
                                  && holder.value().generator().getBiomeSource() instanceof ReloadableBiomeSource bs
                          ) {
                              bs.reloadBiomes();
                          }
                      }));
        }
    }

    public static Set<ResourceLocation> getExcludedBiomes(TagKey<Biome> tag) {
        return EXCLUSIONS.getOrDefault(tag, Set.of());
    }

    private static void addAllExclusions(List<TagKey<Biome>> tags, ResourceLocation biome) {
        tags.forEach(tag -> EXCLUSIONS.computeIfAbsent(tag, k -> new ResourceLocationSet()).add(biome));
    }

    private static void addBiomesToExclusion(
            JsonElement value,
            Consumer<ResourceLocation> adder
    ) {
        if (value.isJsonPrimitive()) {
            adder.accept(ResourceLocationSet.WildcardResourceLocation.parse(value.getAsString()));
        } else if (value.isJsonArray()) {
            value.getAsJsonArray()
                 .forEach(v -> adder.accept(ResourceLocationSet.WildcardResourceLocation.parse(v.getAsString())));
        }
    }

    private static void processBiomeConfigs(ResourceLocation location, JsonObject root) {
        if (root.has(BIOME_EXCLUSION_TAG)) {
            final JsonObject excludes = root.getAsJsonObject(BIOME_EXCLUSION_TAG);
            excludes.asMap().forEach((key, value) -> {
                if (key.equals(END_CATCH_ALL)) {
                    final List<TagKey<Biome>> endTags = WoverEndBiomeSource.TAGS;
                    addBiomesToExclusion(value, id -> addAllExclusions(endTags, id));
                } else if (key.equals(NETHER_CATCH_ALL)) {
                    final List<TagKey<Biome>> netherTags = WoverNetherBiomeSource.TAGS;
                    addBiomesToExclusion(value, id -> addAllExclusions(netherTags, id));
                } else {
                    final TagKey<Biome> tag = TagKey.create(Registries.BIOME, ResourceLocation.parse(key));
                    final Set<ResourceLocation> elements = EXCLUSIONS.computeIfAbsent(
                            tag,
                            k -> new ResourceLocationSet()
                    );
                    addBiomesToExclusion(value, elements::add);
                }
            });
        }

        if (root.has(NO_FABRIC_REGISTER_TAG)) {
            final JsonArray excludes = root.getAsJsonArray(NO_FABRIC_REGISTER_TAG);
            excludes.forEach(v -> FABRIC_EXCLUDES.add(ResourceLocationSet.WildcardResourceLocation.parse(v.getAsString())));
        }
    }

    public static String printBiomeSourceInfo(BiomeSource biomeSource) {
        Set<Holder<Biome>> biomes = Set.of();
        try {
            //biomes = biomeSource.possibleBiomes();
        } catch (Throwable e) {
            LibWoverWorldGenerator.C.log.warn("Error getting possible biomes from BiomeSource", e);
        }
        return biomeSource.getClass()
                          .getSimpleName() + " (" + Integer.toHexString(biomeSource.hashCode()) + ")" +
                "\n    biomes     = " + biomes.size() +
                "\n    namespaces = " + WoverBiomeSourceImpl.getNamespaces(biomes);
    }
}
