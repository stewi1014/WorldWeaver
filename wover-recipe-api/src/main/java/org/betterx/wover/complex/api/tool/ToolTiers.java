package org.betterx.wover.complex.api.tool;

import org.betterx.wover.complex.api.tool.ToolTier.ToolValues;

import net.minecraft.world.item.Tiers;

public class ToolTiers {
    public static ToolTier WOOD_TOOL = ToolTier
            .builder("wooden")
            .toolTier(Tiers.WOOD)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.2f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(0, -3.0f))
            .build();

    public static ToolTier STONE_TOOL = ToolTier
            .builder("stone")
            .toolTier(Tiers.STONE)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(7, -3.2f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-1, -2.0f))
            .build();

    public static ToolTier GOLD_TOOL = ToolTier
            .builder("golden")
            .toolTier(Tiers.GOLD)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.0f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(0, -3.0f))
            .build();

    public static ToolTier IRON_TOOL = ToolTier
            .builder("iron")
            .toolTier(Tiers.IRON)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.1f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-2, -1.0f))
            .build();

    public static ToolTier DIAMOND_TOOL = ToolTier
            .builder("diamond")
            .toolTier(Tiers.DIAMOND)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(5, -3.0f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-3, 0.0f))
            .build();

    public static ToolTier NETHERITE_TOOL = ToolTier
            .builder("netherite")
            .toolTier(Tiers.NETHERITE)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(5, -3.0f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-4, 0.0f))
            .build();


}
