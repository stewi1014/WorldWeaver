package org.betterx.wover.events.mixin;

import org.betterx.wover.WoverEventMod;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldStem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(value = WorldStem.class, priority = 1500)
public class WorldStemMixin {
    @ModifyVariable(method = "<init>", argsOnly = true, at = @At(value = "INVOKE", target = "Ljava/lang/Record;<init>()V", shift = At.Shift.AFTER))
    LayeredRegistryAccess<RegistryLayer> wover_createWorldStem(LayeredRegistryAccess<RegistryLayer> registries) {
        WoverEventMod.C.LOG.debug("wover_createWorldStem: " + registries);
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(registries.compositeAccess());

        return WorldLifecycleImpl.ON_DIMENSION_LOAD.process(registries);
    }

}
