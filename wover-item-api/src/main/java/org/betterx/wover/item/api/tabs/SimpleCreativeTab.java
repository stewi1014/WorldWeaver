package org.betterx.wover.item.api.tabs;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class SimpleCreativeTab {
    public interface CreativeTabPredicate {
        boolean contains(Item item);
    }

    public static CreativeTabPredicate BLOCKS = item -> item instanceof BlockItem;
    public static CreativeTabPredicate ITEMS = item -> !(item instanceof BlockItem);
    
    public final ResourceLocation id;
    public final ItemLike icon;
    public final Component title;
    public final CreativeTabPredicate predicate;

    public final ResourceKey<CreativeModeTab> key;

    public SimpleCreativeTab(ResourceLocation id, ItemLike icon, Component title, CreativeTabPredicate predicate) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.predicate = predicate;
        this.key = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                id
        );
        this.items = new LinkedList<>();
    }

    protected final List<Item> items;

    void addItem(Item item) {
        items.add(item);
    }

    public static class Builder {
        private final String name;
        private final ResourceLocation id;
        private ItemLike icon;
        private CreativeTabPredicate predicate = item -> true;
        private Component title;
        private final CreativeTabManager manager;

        Builder(@NotNull CreativeTabManager manager, @NotNull String name) {
            this.name = name;
            this.manager = manager;
            this.id = manager.C.mk(name + "_tab");
            this.title = Component.translatable("itemGroup." + manager.C.namespace + "." + name);
        }

        public Builder setIcon(ItemLike icon) {
            this.icon = icon;
            return this;
        }

        public Builder setPredicate(CreativeTabPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder setTitle(Component title) {
            this.title = title;
            return this;
        }

        public CreativeTabManager build() {
            if (icon == null)
                throw new IllegalStateException("Icon must be set");
            SimpleCreativeTab res = new SimpleCreativeTab(id, icon, title, predicate);
            manager.tabs.add(res);

            return manager;
        }
    }
}
