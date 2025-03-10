package org.betterx.wover.testmod.generator.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverFeatureProvider;
import org.betterx.wover.testmod.entrypoint.TestModWoverWorldGenerator;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureProvider extends WoverFeatureProvider {
    /**
     * Creates a new instance of {@link WoverFeatureProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public FeatureProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        TestModWoverWorldGenerator
                .TEST_VEGETATION
                .bootstrap(context)
                .add(Blocks.SHORT_GRASS, 180)
                .addAllStatesFor(BlockStateProperties.AGE_7, Blocks.WHEAT, 20)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        TestModWoverWorldGenerator
                .TEST_VEGETATION_PLACED
                .place(context)
                .vanillaNetherGround(24)
                .register();

        TestModWoverWorldGenerator
                .TEST_SCATTERED_PLACED
                .inlineConfiguration(context)
                .randomBlock()
                .add(Blocks.MOSSY_COBBLESTONE, 1)
                .add(Blocks.COBBLESTONE, 4)
                .inlinePlace()
                .betterNetherGround(100)
                .register();
    }
}
