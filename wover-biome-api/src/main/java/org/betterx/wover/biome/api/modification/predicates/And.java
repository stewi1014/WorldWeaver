package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.util.KeyDispatchDataCodec;

import java.util.List;

public record And(List<BiomePredicate> predicates) implements BiomePredicate {
    public static final KeyDispatchDataCodec<And> CODEC = KeyDispatchDataCodec.of(
            BiomePredicate.CODEC
                    .listOf()
                    .xmap(And::new, And::predicates)
                    .fieldOf("predicates"));

    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return predicates().stream().allMatch(p -> p.test(ctx));
    }
}
