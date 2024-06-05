package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.tabs.CreativeTabManager;
import org.betterx.wover.testmod.item.TestItemRegistry;

import net.minecraft.world.item.Items;

import net.fabricmc.api.ModInitializer;

public class TestModWoverItemApi implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-item-testmod");

    @Override
    public void onInitialize() {
        TestItemRegistry.ensureStaticallyLoaded();

        CreativeTabManager.start(C)
                          .createItemsTab(Items.WOODEN_AXE)
                          .build()
                          .processRegistries()
                          .registerAll();
    }
}