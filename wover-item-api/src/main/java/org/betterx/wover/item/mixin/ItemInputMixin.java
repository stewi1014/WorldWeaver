package org.betterx.wover.item.mixin;

import org.betterx.wover.common.item.api.ItemWithCustomStack;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInput.class)
public class ItemInputMixin {
    @Shadow
    @Final
    private Holder<Item> item;

    @WrapOperation(
            method = "createItemStack",
            at = @At(value = "NEW", target = "net/minecraft/world/item/ItemStack")
    )
    public ItemStack wover_init(Holder<Item> item, int count, Operation<ItemStack> original) {
        ItemStack stack = original.call(item, count);
        if (item.value() instanceof ItemWithCustomStack pitem) {
            pitem.setupItemStack(stack, WorldState.registryAccess());
        }
        return stack;
    }
}
