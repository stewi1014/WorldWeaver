package org.betterx.wover.block.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BlockRegistry {
    private static final Map<ModCore, BlockRegistry> REGISTRIES = new HashMap<>();
    public final ModCore C;
    private final Map<ResourceLocation, Block> blocks = new HashMap<>();
    private Map<Block, TagKey<Block>[]> datagenTags;
    private ItemRegistry itemRegistry;

    private BlockRegistry(ModCore modeCore) {
        this.C = modeCore;
        this.itemRegistry = ItemRegistry.forMod(modeCore);

        if (ModCore.isDatagen()) {
            datagenTags = new HashMap<>();
        }
    }

    public static Stream<BlockRegistry> streamAll() {
        return REGISTRIES.values().stream();
    }

    public static BlockRegistry forMod(ModCore modCore) {
        return REGISTRIES.computeIfAbsent(modCore, c -> new BlockRegistry(modCore));
    }

    public Stream<Block> allBlocks() {
        return blocks.values().stream();
    }

    public Stream<BlockItem> allBlockItems() {
        return blocks
                .values()
                .stream()
                .filter(block -> block.asItem() instanceof BlockItem)
                .map(block -> (BlockItem) block.asItem());
    }

    public <T extends Block> T register(String path, T block, TagKey<Block>... tags) {
        if (block != null && block != Blocks.AIR) {
            final ResourceLocation id = _registerBlockOnly(path, block, tags);

            final BlockItem item;
            if (block instanceof CustomBlockItemProvider provider) {
                item = provider.getCustomBlockItem(id, defaultBlockItemSettings());
            } else {
                item = new BlockItem(block, defaultBlockItemSettings());
            }
            registerBlockItem(path, item);

            if (block.defaultBlockState().ignitedByLava()
                    && FlammableBlockRegistry.getDefaultInstance()
                                             .get(block)
                                             .getBurnChance() == 0) {
                FlammableBlockRegistry.getDefaultInstance().add(block, 5, 5);
            }
        }
        return block;
    }

    private ResourceLocation _registerBlockOnly(String path, Block block, TagKey<Block>... tags) {
        ResourceLocation id = C.mk(path);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        blocks.put(id, block);

        if (datagenTags != null) datagenTags.put(block, tags);
        return id;
    }

    public <T extends Block> T registerBlockOnly(String path, T block, TagKey<Block>... tags) {
        if (block != null && block != Blocks.AIR) {
            _registerBlockOnly(path, block, tags);
        }

        return block;
    }


    private BlockItem registerBlockItem(String path, BlockItem item) {
        return this.itemRegistry.register(path, item);
    }

    protected Item.Properties defaultBlockItemSettings() {
        return new Item.Properties();
    }

    public void bootstrapBlockTags(TagBootstrapContext<Block> ctx) {
        if (datagenTags != null) {
            datagenTags.forEach(ctx::add);
        }
    }
}