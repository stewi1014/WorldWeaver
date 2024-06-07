package org.betterx.wover.complex.api.tool;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public abstract class EquipmentSet {
    public interface ToolFactory<I extends TieredItem> {
        I create(Tier tier, Item.Properties properties);
    }

    public interface ArmorFactory<I extends ArmorItem> {
        I create(Holder<ArmorMaterial> holder, ArmorItem.Type type, Item.Properties properties);
    }

    public final ToolTier toolTier;
    public final ArmorTier armorTier;
    public final String baseName;
    public final ModCore C;
    public final ItemLike handleItem;

    private final Map<ToolSlot, ToolDescription<?>> tools = new HashMap<>();
    private final Map<ArmorSlot, ArmorDescription<?>> armors = new HashMap<>();
    protected final EquipmentSet templateBaseSet;

    public EquipmentSet(
            ModCore C, String baseName,
            ToolTier toolTier, ArmorTier armorTier,
            ItemLike handleItem

    ) {
        this(C, baseName, toolTier, armorTier, handleItem, null);
    }

    public EquipmentSet(
            ModCore C, String baseName,
            ToolTier toolTier, ArmorTier armorTier,
            ItemLike handleItem, EquipmentSet templateBaseSet
    ) {
        this.C = C;
        this.baseName = baseName;
        this.toolTier = toolTier;
        this.armorTier = armorTier;
        this.handleItem = handleItem;
        this.templateBaseSet = templateBaseSet;
    }

    public <I extends TieredItem> void add(ToolSlot slot, ToolFactory<I> toolFactory) {
        add(slot, toolFactory, ToolSlot::buildProperties);
    }

    public <I extends TieredItem> void add(
            ToolSlot slot,
            ToolFactory<I> toolFactory,
            ToolSlot.PropertiesBuilder propertiesBuilder
    ) {
        tools.put(
                slot,
                new ToolDescription<>(C, slot, nameForSlot(slot), () -> toolFactory.create(toolTier.toolTier, propertiesBuilder.build(slot, toolTier)))
        );
    }

    public <I extends ArmorItem> void add(ArmorSlot slot, ArmorFactory<I> armorFactory) {
        add(slot, armorFactory, ArmorSlot::buildProperties);
    }

    public <I extends ArmorItem> void add(
            ArmorSlot slot,
            ArmorFactory<I> armorFactory,
            ArmorSlot.PropertiesBuilder propertiesBuilder
    ) {
        armors.put(
                slot,
                new ArmorDescription<>(C, slot, nameForSlot(slot), () -> armorFactory.create(armorTier.armorMaterial, slot.armorType, propertiesBuilder.build(slot, armorTier)))
        );
    }


    public void buildRecipes(RecipeOutput ctx) {
        for (var desc : tools.entrySet()) {
            desc.getValue().addRecipe(ctx, toolTier, handleItem);
        }

        for (var desc : armors.entrySet()) {
            desc.getValue().addRecipe(ctx, armorTier, handleItem, templateBaseSet);
        }
    }

    @NotNull
    protected String nameForSlot(ToolSlot slot) {
        return baseName + "_" + slot.name;
    }

    @NotNull
    protected String nameForSlot(ArmorSlot slot) {
        return baseName + "_" + slot.name;
    }

    public <I extends TieredItem> I get(ToolSlot slot) {
        return (I) tools.get(slot).getItem();
    }

    public <I extends ArmorItem> I get(ArmorSlot slot) {
        return (I) armors.get(slot).getItem();
    }
}

