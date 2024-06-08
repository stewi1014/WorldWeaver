package org.betterx.wover.testmod.item.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.TestModWoverItemApi;

public class TestModWoverItemApiDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(TestEnchantmentProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverItemApi.C;
    }

}
