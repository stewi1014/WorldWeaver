package org.betterx.wover.loot.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.List;

public class LootLookupProvider {
    public record DropInfo(ItemLike item, NumberProvider numberProvider) {
        public DropInfo(ItemLike item, int count) {
            this(item, ConstantValue.exactly(count));
        }
    }

    private final HolderLookup.Provider provider;
    private final VanillaBlockLoot vanillaBlockLoot;
    private final HolderLookup.RegistryLookup<Enchantment> enchantmentLookup;

    public LootLookupProvider(HolderLookup.Provider provider) {
        this.vanillaBlockLoot = new VanillaBlockLoot(provider);
        this.provider = provider;
        this.enchantmentLookup = provider.lookupOrThrow(Registries.ENCHANTMENT);
    }

    public HolderLookup.Provider getProvider() {
        return provider;
    }

    public LootTable.Builder dropWithSilkTouch(
            Block withSilkTouch
    ) {
        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(withSilkTouch).when(vanillaBlockLoot.hasSilkTouch()))
                );
    }

    public LootTable.Builder dropWithSilkTouch(
            Block withSilkTouch,
            ItemLike withoutSilkTouch,
            NumberProvider numberProvider
    ) {
        return vanillaBlockLoot.createSingleItemTableWithSilkTouch(withSilkTouch, withoutSilkTouch, numberProvider);
    }

    public LootTable.Builder dropWithSilkTouch(
            Block withSilkTouch,
            List<DropInfo> withoutSilkTouch
    ) {
        if (withoutSilkTouch.isEmpty()) return dropWithSilkTouch(withSilkTouch);

        var mainBuilder = LootTable.lootTable();
        for (DropInfo dropInfo : withoutSilkTouch) {
            LootPoolSingletonContainer.Builder<? extends LootPoolSingletonContainer.Builder<?>> item = LootItem
                    .lootTableItem(dropInfo.item)
                    .apply(SetItemCountFunction.setCount(dropInfo.numberProvider));
            createSelfDropDispatchTable(mainBuilder, withSilkTouch, vanillaBlockLoot.hasSilkTouch(), vanillaBlockLoot.applyExplosionDecay(withSilkTouch, item));
        }

        return mainBuilder;
    }

    protected static void createSelfDropDispatchTable(
            LootTable.Builder tableBuilder,
            Block block,
            LootItemCondition.Builder builder,
            LootPoolEntryContainer.Builder<?> builder2
    ) {
        tableBuilder.withPool(LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(block).when(builder).otherwise(builder2)));
    }


    public LootTable.Builder drop(Block block) {
        return vanillaBlockLoot.createSingleItemTable(block);
    }

    public LootTable.Builder dropPottedContents(FlowerPotBlock block) {
        return vanillaBlockLoot.createPotFlowerItemTable(block.getPotted());
    }

    public LootTable.Builder dropOre(Block oreBlock, Item ore) {
        return vanillaBlockLoot.createOreDrop(oreBlock, ore);
    }

    public LootTable.Builder dropOre(Block oreBlock, Item ore, NumberProvider numberProvider) {
        return vanillaBlockLoot.createSilkTouchDispatchTable(
                oreBlock,
                vanillaBlockLoot.applyExplosionDecay(
                        oreBlock,
                        LootItem
                                .lootTableItem(ore)
                                .apply(SetItemCountFunction.setCount(numberProvider))
                                .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    public static ResourceKey<LootTable> getBlockLootTableKey(ModCore modCore, ResourceLocation blockId) {
        return ResourceKey.create(Registries.LOOT_TABLE, blockId.withPrefix("blocks/"));
    }
}
