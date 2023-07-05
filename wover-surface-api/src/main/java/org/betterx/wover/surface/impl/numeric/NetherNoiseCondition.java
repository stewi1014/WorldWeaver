package org.betterx.wover.surface.impl.numeric;

import org.betterx.wover.math.api.random.RandomHelper;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.api.numeric.NumericProvider;
import org.betterx.wover.surface.mixin.SurfaceRulesContextAccessor;

import com.mojang.serialization.Codec;

public class NetherNoiseCondition implements NumericProvider {
    public static final Codec<NetherNoiseCondition> CODEC = Codec
            .BYTE.fieldOf("nether_noise")
                 .xmap(
                         (obj) -> (NetherNoiseCondition) Conditions.NETHER_NOISE,
                         obj -> (byte) 0
                 )
                 .codec();


    public NetherNoiseCondition() {
    }


    @Override
    public Codec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

    @Override
    public int getNumber(SurfaceRulesContextAccessor context) {
        final int x = context.getBlockX();
        final int y = context.getBlockY();
        final int z = context.getBlockZ();
        double value = Conditions.NETHER_VOLUME_NOISE.noiseContext.noise.eval(
                x * Conditions.NETHER_VOLUME_NOISE.scaleX,
                y * Conditions.NETHER_VOLUME_NOISE.scaleY,
                z * Conditions.NETHER_VOLUME_NOISE.scaleZ
        );

        int offset = Conditions.NETHER_VOLUME_NOISE.noiseContext.random.nextInt(20) == 0 ? 3 : 0;


        float cmp = RandomHelper.inRange(Conditions.NETHER_VOLUME_NOISE.noiseContext.random, 0.4F, 0.5F);
        if (value > cmp || value < -cmp) return 2 + offset;

        if (value > Conditions.NETHER_VOLUME_NOISE.range.sample(Conditions.NETHER_VOLUME_NOISE.noiseContext.random))
            return 0 + offset;

        return 1 + offset;
    }
}