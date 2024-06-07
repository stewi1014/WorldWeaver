package org.betterx.wover.complex.api.tool;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;


class ToolDescription<I extends TieredItem> extends ItemDescription<I> {
    public final ToolSlot slot;

    public ToolDescription(
            ModCore modCore,
            ToolSlot slot,
            String path,
            Supplier<I> creator
    ) {
        super(modCore, path, creator);
        this.slot = slot;

    }

    public void addRecipe(
            RecipeOutput ctx,
            ToolTier tier,
            ItemLike stick
    ) {
        if (item == null) return;
        if (tier == null) return;
        var repair = tier.toolTier.getRepairIngredient();
        var repairItems = repair.getItems();
        if (repairItems.length == 0) return;
        final ItemLike ingot = repairItems[0].getItem();

        var builder = RecipeBuilder.crafting(location, item)
                                   .addMaterial('#', ingot)
                                   .category(RecipeCategory.TOOLS);

        if (buildRecipe(item, stick, builder)) return;
        builder.category(slot.category).group(location.getPath()).build(ctx);
    }


}