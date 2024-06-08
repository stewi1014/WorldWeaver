package org.betterx.wover.loot.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockLootProvider {
    @Nullable
    LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    );
}
