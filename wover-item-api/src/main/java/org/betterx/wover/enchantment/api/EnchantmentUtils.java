package org.betterx.wover.enchantment.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentUtils {
    private EnchantmentUtils() {
    }

    /**
     * Enchants an item in the world. This method is safe to call from anywhere. But it will only
     * work if the WorldState has a valid registryAccess.
     *
     * @param stack       The item to enchant
     * @param enchantment The enchantment to apply
     * @param level       The level of the enchantment
     * @param provider    The provider to use to lookup the enchantment Registry. If null, this method will return false.
     * @return True if the enchantment was applied, false otherwise.
     */
    public static boolean enchantInWorld(
            ItemStack stack,
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.Provider provider
    ) {
        if (provider == null) return false;
        return enchantInWorld(stack, enchantment, level, provider.lookup(Registries.ENCHANTMENT).orElse(null));
    }

    /**
     * Enchants an item in the world. This method is safe to call from anywhere. But it will only
     * work if the WorldState has a valid registryAccess.
     *
     * @param stack       The item to enchant
     * @param enchantment The enchantment to apply
     * @param level       The level of the enchantment
     * @param lookup      The lookup for the enchantment Registry. If null, this method will return false.
     * @return True if the enchantment was applied, false otherwise.
     */
    public static boolean enchantInWorld(
            ItemStack stack,
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        if (lookup == null) return false;

        return lookup
                .get(enchantment)
                .map(e -> {
                    stack.enchant(e, level);
                    return true;
                })
                .orElse(false);
    }
}
