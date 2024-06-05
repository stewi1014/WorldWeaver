package org.betterx.wover.item.api.tabs;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.common.item.api.ItemWithCustomStack;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.ItemRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class CreativeTabManager {
    public final ModCore C;
    protected final List<SimpleCreativeTab> tabs = new LinkedList<>();

    public static CreativeTabManager start(ModCore modCore) {
        return new CreativeTabManager(modCore);
    }

    private CreativeTabManager(ModCore modCore) {
        this.C = modCore;
    }

    public SimpleCreativeTab.Builder createTab(String name) {
        return new SimpleCreativeTab.Builder(this, name);
    }

    public SimpleCreativeTab.Builder createBlockTab(ItemLike icon) {
        return new SimpleCreativeTab.Builder(this, "blocks").setIcon(icon).setPredicate(SimpleCreativeTab.BLOCKS);
    }

    public SimpleCreativeTab.Builder createItemsTab(ItemLike icon) {
        return new SimpleCreativeTab.Builder(this, "items").setIcon(icon).setPredicate(SimpleCreativeTab.ITEMS);
    }

    public CreativeTabManager processRegistries() {
        ItemRegistry
                .streamAll()
                .map(ItemRegistry::allItems)
                .forEach(this::process);

        BlockRegistry
                .streamAll()
                .map(BlockRegistry::allBlockItems)
                .map(stream -> stream.map(Item.class::cast))
                .forEach(this::process);
        return this;
    }

    public CreativeTabManager process(Stream<Item> items) {
        items.forEach(item -> {
            for (SimpleCreativeTab tab : tabs) {
                if (tab.predicate.contains(item)) {
                    tab.addItem(item);
                    break;
                }
            }
        });

        return this;
    }

    public void registerAll() {
        for (SimpleCreativeTab tab : tabs) {
            var tabItem = FabricItemGroup
                    .builder()
                    .icon(() -> new ItemStack(tab.icon))
                    .title(tab.title)
                    .displayItems((displayParameters, output) -> {
                        output.acceptAll(tab.items
                                .stream()
                                .map(i -> {
                                    var stack = new ItemStack(i);
                                    if (i instanceof ItemWithCustomStack item) {
                                        item.setupItemStack(stack, displayParameters.holders());
                                    }
                                    return stack;
                                })
                                .toList()
                        );
                    }).build();

            Registry.register(
                    BuiltInRegistries.CREATIVE_MODE_TAB,
                    tab.key,
                    tabItem
            );
        }
    }
}

