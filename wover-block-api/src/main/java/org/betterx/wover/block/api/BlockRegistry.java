package org.betterx.wover.block.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BlockRegistry {
    private static final Map<ModCore, BlockRegistry> REGISTRIES = new HashMap<>();
    public final ModCore C;
    private final Map<ResourceLocation, Block> blocks = new HashMap<>();
    private Map<Block, TagKey<Block>[]> datagenTags;

    private BlockRegistry(ModCore modeCore) {
        this.C = modeCore;

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

    public Block register(String path, Block block, TagKey<Block>... tags) {
        if (block != null && block != Blocks.AIR) {
            ResourceLocation id = C.mk(path);
            Registry.register(BuiltInRegistries.BLOCK, id, block);
            blocks.put(id, block);

            if (datagenTags != null) datagenTags.put(block, tags);
        }

        return block;
    }

    public void bootstrapBlockTags(TagBootstrapContext<Block> ctx) {
        if (datagenTags != null) {
            datagenTags.forEach(ctx::add);
        }
    }
}