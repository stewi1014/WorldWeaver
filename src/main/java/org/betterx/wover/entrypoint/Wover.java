package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.impl.registry.ModCoreImpl;
import org.betterx.wover.ui.api.VersionChecker;

import net.fabricmc.api.ModInitializer;

public class Wover implements ModInitializer {
    public static final ModCore C = ModCoreImpl.GLOBAL_MOD;

    @Override
    public void onInitialize() {
        VersionChecker.registerMod(C);
    }
}
