package org.betterx.wover.complex.api.equipment;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArmorTier {
    public record ArmorValues(int durability, SmithingTemplateItem smithingTemplate) {
        public ArmorValues(int durability) {
            this(durability, null);
        }


        public ArmorValues copyWithOffset(ArmorValues offset) {
            return new ArmorValues(
                    durability + offset.durability,
                    offset.smithingTemplate
            );
        }
    }

    public final Holder<ArmorMaterial> armorMaterial;
    private final ArmorValues[] armorValues;
    public final String name;

    private ArmorTier(
            String name,
            Holder<ArmorMaterial> armorMaterial,
            ArmorValues[] armorValues
    ) {
        this.armorMaterial = armorMaterial;
        this.armorValues = armorValues;
        this.name = name;
    }

    @Nullable
    public ArmorValues getValues(ArmorSlot slot) {
        return armorValues[slot.slotIndex];
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public boolean is(Holder<ArmorMaterial> mat) {
        return mat.unwrapKey().map(this.armorMaterial::is).orElse(false);
    }

    @Override
    public String toString() {
        return "ArmorTier - " + this.name;
    }

    //a Builder class
    public static class Builder {
        private Holder<ArmorMaterial> armorMaterial;
        private final ArmorValues[] armorValues = new ArmorValues[ArmorSlot.values().length];
        private final String name;

        public Builder(String name) {
            this.name = name;
        }

        public Builder armorMaterial(Holder<ArmorMaterial> armorMaterial) {
            this.armorMaterial = armorMaterial;
            return this;
        }

        public Builder allArmorValues(ArmorValues armorValues) {
            Arrays.fill(this.armorValues, armorValues);
            return this;
        }

        public Builder armorValues(ArmorSlot slot, ArmorValues armorValues) {
            this.armorValues[slot.slotIndex] = armorValues;
            return this;
        }

        public Builder armorValuesWithOffset(ArmorTier source, ArmorValues offset) {
            for (int i = 0; i < armorValues.length; i++) {
                if (source.armorValues[i] != null)
                    this.armorValues[i] = source.armorValues[i].copyWithOffset(offset);
            }
            return this;
        }

        public ArmorTier build() {
            return new ArmorTier(name, armorMaterial, armorValues);
        }
    }

    /**
     * Create a new ArmorTier for the specified ArmorMaterial with where all Values are offset by the given amount
     *
     * @param newName     New name to use for the copy
     * @param newMaterial New ArmorMaterial to use or null if the one from this ArmorTier should be used
     * @param offset      Offset to apply to all values
     * @return New ArmorTier with the specified offset
     */
    public ArmorTier copyWithOffset(
            @NotNull String newName,
            @Nullable Holder<ArmorMaterial> newMaterial,
            ArmorValues offset
    ) {
        ArmorValues[] newValues = new ArmorValues[armorValues.length];
        for (int i = 0; i < armorValues.length; i++) {
            if (armorValues[i] != null)
                newValues[i] = armorValues[i].copyWithOffset(offset);
        }
        return new ArmorTier(newName, newMaterial == null ? this.armorMaterial : newMaterial, newValues);
    }
}
