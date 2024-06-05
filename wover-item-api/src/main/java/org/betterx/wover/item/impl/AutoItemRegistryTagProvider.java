package org.betterx.wover.item.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

/**
 * Creates item tags for all items that were registered with an
 * {@link org.betterx.wover.item.api.ItemRegistry} and had
 * some tags added to them.
 * <p>
 * This provider is automatically registered to the global datapack by {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoItemRegistryTagProvider extends WoverTagProvider.ForItems {

    public AutoItemRegistryTagProvider(ModCore modCore) {
        //do not filter any tags
        super(modCore, null);
    }

    @Override
    protected void prepareTags(ItemTagBootstrapContext context) {
        ItemRegistry.streamAll().forEach(registry -> registry.bootstrapItemTags(context));
    }
}
