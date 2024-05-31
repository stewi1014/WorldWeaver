package org.betterx.wover.common.compat.api;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;
import java.util.stream.Stream;


/**
 * An abstraction for a codec that encodes and decodes a map of values. In Minecraft 1.21, this is a wrapper around
 * {@link WMapCodec}.
 * <p>
 * Use {@link #create(Function)} to generate a new instace.
 *
 * @param <A> The type that this codec encodes and decodes
 */
public class WMapCodec<A> extends MapCodec<A> {
    /**
     * A Replacement for RecordCodecBuilder.mapCodec, which should work for Minecraft 1.20 and 1.21
     *
     * @param builder A function that takes a RecordCodecBuilder.Instance and returns an App<RecordCodecBuilder.Mu<O>, O>
     * @param <O>     The type that this codec encodes and decodes
     * @return A new WWMapCodec instance
     */
    public static <O> WMapCodec<O> create(final Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return build(builder.apply(RecordCodecBuilder.instance()));
    }

    //adapted from com.mojang.serialization.codecs.RecordCodecBuilder
    //this is original code from mojanag, adapted here to provide Type compatibilits between Minecraft versions
    private static <O> WMapCodec<O> build(final App<RecordCodecBuilder.Mu<O>, O> builderBox) {
        return new WMapCodec<O>(RecordCodecBuilder.build(builderBox));
    }

    private final MapCodec<A> target;

    private WMapCodec(MapCodec<A> target) {
        this.target = target;
    }

    @Override
    public <T> DataResult<A> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        return target.decode(ops, input);
    }

    @Override
    public <T> RecordBuilder<T> encode(final A input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        return target.encode(input, ops, prefix);
    }

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return target.keys(ops);
    }

    @Override
    public String toString() {
        return "WMapCodec[" + target.toString() + "]";
    }
}
