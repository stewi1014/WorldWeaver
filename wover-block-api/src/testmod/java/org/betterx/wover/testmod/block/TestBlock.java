package org.betterx.wover.testmod.block;

import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestBlock extends Block implements BlockLootProvider {
    public TestBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.createSingleItemTableWithSilkTouch(this, Items.DIAMOND, ConstantValue.exactly(3.0F));
    }
}
