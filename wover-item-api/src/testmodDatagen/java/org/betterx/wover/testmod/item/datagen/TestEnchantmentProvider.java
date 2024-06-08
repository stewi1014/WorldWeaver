package org.betterx.wover.testmod.item.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverEnchantmentProvider;
import org.betterx.wover.testmod.entrypoint.TestModWoverItemApi;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.DamageEntity;
import net.minecraft.world.item.enchantment.effects.DamageItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

public class TestEnchantmentProvider extends WoverEnchantmentProvider {
    public TestEnchantmentProvider(ModCore modCore) {
        super(modCore, "enchantments");
    }

    @Override
    protected void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);
        HolderGetter<DamageType> dmageGetter = context.lookup(Registries.DAMAGE_TYPE);
        TestModWoverItemApi.TEST_ENCHANT.register(context, Enchantment
                .enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                itemGetter.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                                1, 3,
                                Enchantment.dynamicCost(10, 20),
                                Enchantment.dynamicCost(60, 20),
                                8,
                                EquipmentSlotGroup.ANY
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.VICTIM,
                        EnchantmentTarget.ATTACKER,
                        AllOf.entityEffects(
                                new DamageEntity(
                                        LevelBasedValue.constant(1.0F),
                                        LevelBasedValue.constant(5.0F),
                                        dmageGetter.getOrThrow(DamageTypes.THORNS)
                                ),
                                new DamageItem(
                                        LevelBasedValue.constant(2.0F)
                                )
                        ),
                        LootItemRandomChanceCondition.randomChance(
                                EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(0.15F))
                        )
                )
        );
    }
}
