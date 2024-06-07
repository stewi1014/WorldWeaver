package org.betterx.wover.complex.api.tool;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class ArmorDescription<I extends ArmorItem> extends ItemDescription<I> {
    private final ArmorSlot slot;

    public ArmorDescription(
            ModCore modCore,
            ArmorSlot slot,
            String path,
            Supplier<I> creator
    ) {
        super(modCore, path, creator);
        this.slot = slot;
    }

    public void addRecipe(
            RecipeOutput ctx,
            ArmorTier tier,
            ItemLike stick,
            @Nullable EquipmentSet sourceSet
    ) {
        if (item == null) return;
        if (tier == null) return;

        var repair = tier.armorMaterial.value().repairIngredient();
        var ingot = repair.get();
        if (ingot.isEmpty()) return;

        var values = tier.getValues(slot);
        if (values != null && values.smithingTemplate() != null && sourceSet != null) {
            RecipeBuilder
                    .smithing(location, item)
                    .template(values.smithingTemplate())
                    .base(sourceSet.get(this.slot))
                    .addon(ingot)
                    .category(slot.category)
                    .build(ctx);
        } else {
            var builder = RecipeBuilder.crafting(location, item)
                                       .addMaterial('#', ingot)
                                       .category(RecipeCategory.TOOLS);

            if (buildRecipe(item, stick, builder)) return;
            builder
                    .category(slot.category)
                    .group(location.getPath())
                    .build(ctx);
        }
    }
}
