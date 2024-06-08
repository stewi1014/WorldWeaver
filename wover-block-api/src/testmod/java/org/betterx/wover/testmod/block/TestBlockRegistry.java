package org.betterx.wover.testmod.block;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.testmod.entrypoint.TestModWoverBlock;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus;

public class TestBlockRegistry {
    private static final BlockRegistry R = BlockRegistry.forMod(TestModWoverBlock.C);

    private TestBlockRegistry() {
    }

    public static final TestBlock TEST_BLOCK = R.register(
            "test_block",
            new TestBlock(Block.Properties.of().ignitedByLava().instabreak())
    );

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }
}
