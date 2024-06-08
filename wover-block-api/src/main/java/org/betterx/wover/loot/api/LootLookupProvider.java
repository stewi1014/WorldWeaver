package org.betterx.wover.loot.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootLookupProvider {
    private final HolderLookup.Provider provider;
    private final VanillaBlockLoot vanillaBlockLoot;

    public LootLookupProvider(HolderLookup.Provider provider) {
        this.vanillaBlockLoot = new VanillaBlockLoot(provider);
        this.provider = provider;
    }

    public HolderLookup.Provider getProvider() {
        return provider;
    }

    public LootTable.Builder createSingleItemTableWithSilkTouch(
            Block block,
            ItemLike itemLike,
            NumberProvider numberProvider
    ) {
        return vanillaBlockLoot.createSingleItemTableWithSilkTouch(block, itemLike, numberProvider);
    }
    
    public LootTable.Builder drop(Block block) {
        return vanillaBlockLoot.createSingleItemTable(block);
    }

    public LootTable.Builder dropPottedContents(FlowerPotBlock block) {
        return vanillaBlockLoot.createPotFlowerItemTable(block.getPotted());
    }

    public static ResourceKey<LootTable> getBlockLootTableKey(ModCore modCore, ResourceLocation blockId) {
        return ResourceKey.create(Registries.LOOT_TABLE, blockId.withPrefix("blocks/"));
    }
}
