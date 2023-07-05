package org.betterx.wover.surface.impl;

import org.betterx.wover.core.api.DatapackRegistryBuilder;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class SurfaceRuleRegistryImpl {
    public static final EventImpl<OnBootstrapRegistry<AssignedSurfaceRule>> BOOTSTRAP_SURFACE_RULE_REGISTRY
            = new EventImpl<>("BOOTSTRAP_SURFACE_RULE_REGISTRY");
    private static Map<ResourceKey<AssignedSurfaceRule>, AssignedSurfaceRule> KNOWN = new HashMap<>();
    
    private static void onBootstrap(BootstapContext<AssignedSurfaceRule> ctx) {
        BOOTSTRAP_SURFACE_RULE_REGISTRY.emit(c -> c.bootstrap(ctx));
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.register(
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY,
                AssignedSurfaceRule.CODEC,
                SurfaceRuleRegistryImpl::onBootstrap
        );

        WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(SurfaceRuleUtil::injectSurfaceRulesToAllDimensions, 500);
    }

    public static ResourceKey<AssignedSurfaceRule> createKey(
            ResourceLocation ruleID
    ) {
        return ResourceKey.create(
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY,
                ruleID
        );
    }

    @ApiStatus.Internal
    public static Holder<AssignedSurfaceRule> register(
            @NotNull BootstapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key,
            @NotNull ResourceKey<Biome> biomeKey,
            @NotNull SurfaceRules.RuleSource rules
    ) {
        if (biomeKey == null) {
            throw new IllegalStateException("Biome key is not set for surface rule '" + key.location() + "'");
        }

        return ctx.register(
                key,
                new AssignedSurfaceRule(rules, biomeKey.location())
        );
    }
}