package org.betterx.wover.loot.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
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

    public LootItemCondition.Builder silkTouchCondition() {
        return vanillaBlockLoot.hasSilkTouch();
    }

    public LootTable.Builder dropWithSilkTouch(
            ItemLike withSilkTouch
    ) {
        return vanillaBlockLoot.createSilkTouchOnlyTable(withSilkTouch);
//        return LootTable
//                .lootTable()
//                .withPool(LootPool
//                        .lootPool()
//                        .setRolls(ConstantValue.exactly(1.0F))
//                        .add(LootItem.lootTableItem(withSilkTouch).when(vanillaBlockLoot.hasSilkTouch()))
//                );
    }

    public LootTable.Builder dropWithSilkTouchOrShears(
            ItemLike withSilkTouch
    ) {
        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .when(vanillaBlockLoot.hasShearsOrSilkTouch())
                        .setRolls(ConstantValue.exactly(1.0f))
                        .add(LootItem.lootTableItem(withSilkTouch)));
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

    public LootTable.Builder dropDoor(Block doorBlock) {
        return vanillaBlockLoot.createDoorTable(doorBlock);
    }

    /*
    this.
     */

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropSingleWithCondition(
            Block block,
            Property<T> property,
            T comparable
    ) {
        return vanillaBlockLoot.createSinglePropConditionTable(block, property, comparable);
    }

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropWithSilkTouchAndCondition(
            Block withSilkTouch,
            ItemLike withoutSilkTouch,
            NumberProvider numberProvider,
            Property<T> property,
            T comparable
    ) {
        return vanillaBlockLoot.createSilkTouchDispatchTable(
                withSilkTouch,
                vanillaBlockLoot.applyExplosionCondition(
                        withSilkTouch,
                        LootItem.lootTableItem(withoutSilkTouch)
                                .when(LootItemBlockStatePropertyCondition
                                        .hasBlockStateProperties(withSilkTouch)
                                        .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder
                                                .properties()
                                                .hasProperty(property, comparable))
                                )
                                .apply(SetItemCountFunction.setCount(numberProvider))
                )
        );
    }

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropWithSilkTouchAndCondition(
            Block withSilkTouch,
            Property<T> property,
            T comparable
    ) {
        return LootTable.lootTable().withPool(
                LootPool
                        .lootPool()
                        .when(vanillaBlockLoot.hasSilkTouch())
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(withSilkTouch)
                                     .when(LootItemBlockStatePropertyCondition
                                             .hasBlockStateProperties(withSilkTouch)
                                             .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder
                                                     .properties()
                                                     .hasProperty(property, comparable))
                                     )
                        )
        );
    }

    public LootTable.Builder dropPlant(Block plantBlock, ItemLike sapling) {
        return dropPlant(plantBlock, sapling, 0.125F, ConstantValue.exactly(1), 2);
    }

    public LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike sapling,
            float saplingChance,
            NumberProvider saplingCount,
            int fortuneBonus
    ) {
        return vanillaBlockLoot.createShearsDispatchTable(plantBlock, vanillaBlockLoot.applyExplosionDecay(plantBlock, (LootItem
                .lootTableItem(sapling)
                .when(LootItemRandomChanceCondition.randomChance(saplingChance)))
                .apply(SetItemCountFunction.setCount(saplingCount))
                .apply(ApplyBonusCount.addUniformBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE), fortuneBonus))));
    }

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            ItemLike seed,
            Property<T> property,
            T comparable
    ) {
        return this.dropPlant(plantBlock, fruit, ConstantValue.exactly(1), seed, ConstantValue.exactly(1), 0.571f, 3, property, comparable);
    }

    public LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            ItemLike seed,
            IntegerProperty property,
            int comparable
    ) {
        return this.dropPlant(plantBlock, fruit, ConstantValue.exactly(1), seed, ConstantValue.exactly(1), 0.571f, 3, property, comparable);
    }

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            NumberProvider fruitCount,
            ItemLike seed,
            NumberProvider seedCount,
            float probability,
            int extraRounds,
            Property<T> property,
            T comparable
    ) {
        LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(plantBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, comparable));
        return dropPlant(plantBlock, fruit, fruitCount, seed, seedCount, probability, extraRounds, condition);
    }

    public LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            NumberProvider fruitCount,
            ItemLike seed,
            NumberProvider seedCount,
            float probability,
            int extraRounds,
            IntegerProperty property,
            int comparable
    ) {
        LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(plantBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, comparable));
        return dropPlant(plantBlock, fruit, fruitCount, seed, seedCount, probability, extraRounds, condition);
    }

    public LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            NumberProvider fruitCount,
            ItemLike seed,
            NumberProvider seedCount,
            float probability,
            int extraRounds,
            LootItemCondition.Builder condition
    ) {
        return vanillaBlockLoot.applyExplosionDecay(
                plantBlock,
                LootTable.lootTable().withPool(
                        LootPool
                                .lootPool()
                                .add(LootItem
                                        .lootTableItem(fruit)
                                        .apply(SetItemCountFunction.setCount(fruitCount))
                                        .when(condition).otherwise(LootItem.lootTableItem(seed))

                                )
                ).withPool(
                        LootPool
                                .lootPool()
                                .when(condition)
                                .add(LootItem
                                        .lootTableItem(seed)
                                        .apply(SetItemCountFunction.setCount(seedCount))
                                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE), probability, extraRounds))
                                )
                )

        );

    }

    public static ResourceKey<LootTable> getBlockLootTableKey(ModCore modCore, ResourceLocation blockId) {
        return ResourceKey.create(Registries.LOOT_TABLE, blockId.withPrefix("blocks/"));
    }
}
