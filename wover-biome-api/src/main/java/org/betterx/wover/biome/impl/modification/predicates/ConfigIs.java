package org.betterx.wover.biome.impl.modification.predicates;

import de.ambertation.wunderlib.configs.AbstractConfig;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.config.api.Configs;
import org.betterx.wover.entrypoint.LibWoverCore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;

public record ConfigIs(ResourceLocation configFile, String path, String key,
                       String testValue) implements BiomePredicate {
    public static final MapCodec<ConfigIs> DIRECT_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceLocation.CODEC.fieldOf("config_file").forGetter(ConfigIs::configFile),
                    Codec.STRING.fieldOf("path").forGetter(ConfigIs::path),
                    Codec.STRING.fieldOf("key").forGetter(ConfigIs::key),
                    Codec.STRING.fieldOf("value").forGetter(ConfigIs::testValue)
            )
            .apply(
                    instance,
                    ConfigIs::new
            ));
    public static final KeyDispatchDataCodec<ConfigIs> CODEC = KeyDispatchDataCodec.of(DIRECT_CODEC);

    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    public static <T, R extends AbstractConfig<?>.Value<T, R>> ConfigIs of(
            AbstractConfig<?>.Value<T, R> value,
            T targetValue
    ) {
        if (value.getParentFile() == null) {
            throw new IllegalArgumentException("Value " + value + " must have a parent file.");
        }

        return new ConfigIs(
                value.getParentFile().location,
                value.token.path(),
                value.token.key(),
                String.valueOf(targetValue)
        );
    }

    @Override
    public boolean test(Context ctx) {
        final AbstractConfig<?> config = Configs.get(configFile);
        if (config == null) {
            LibWoverCore.C.log.verboseWarning("Config file %s not found", configFile);
            return false;
        }

        final AbstractConfig<?>.Value<?, ? extends AbstractConfig<?>.Value<?, ?>> value = config.getValue(path, key);
        if (value == null) {
            LibWoverCore.C.log.verboseWarning("Config value %s.%s not found in %s", path, key, configFile);
            return false;
        }

        return value.valueEquals(testValue);
    }
}
