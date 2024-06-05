package org.betterx.wover.block.impl.predicate;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.world.level.block.Block;

/**
 * Creates item tags for all items that were registered with an
 * {@link org.betterx.wover.block.api.BlockRegistry} and had
 * some tags added to them.
 * <p>
 * This provider is automatically registered to the global datapack by {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoBlockRegistryTagProvider extends WoverTagProvider.ForBlocks {

    public AutoBlockRegistryTagProvider(ModCore modCore) {
        //do not filter any tags
        super(modCore, null);
    }

    @Override
    protected void prepareTags(TagBootstrapContext<Block> context) {
        BlockRegistry.streamAll().forEach(registry -> registry.bootstrapBlockTags(context));
    }
}
