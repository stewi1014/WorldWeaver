package org.betterx.wover.entrypoint;

import org.betterx.wover.block.impl.predicate.AutoBlockRegistryTagProvider;
import org.betterx.wover.block.impl.predicate.BlockPredicatesImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;

import net.fabricmc.api.ModInitializer;

public class LibWoverBlockAndItem implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-block", "wover");

    @Override
    public void onInitialize() {
        WoverDataGenEntryPoint.registerAutoProvider(AutoBlockRegistryTagProvider::new);
        BlockPredicatesImpl.ensureStaticInitialization();
    }
}