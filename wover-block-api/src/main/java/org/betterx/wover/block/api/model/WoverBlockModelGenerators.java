package org.betterx.wover.block.api.model;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class WoverBlockModelGenerators {
    public final BlockModelGenerators vanillaGenerator;

    public WoverBlockModelGenerators(
            BlockModelGenerators vanillaGenerator
    ) {
        this.vanillaGenerator = vanillaGenerator;
    }

    public void acceptBlockState(BlockStateGenerator blockStateGenerator) {
        this.vanillaGenerator.blockStateOutput.accept(blockStateGenerator);
    }

    public void delegateItemModel(Block block, ResourceLocation resourceLocation) {
        this.vanillaGenerator.delegateItemModel(block, resourceLocation);
    }

    public TexturedModel getTextureModels(Block block, TexturedModel defaultModel) {
        return vanillaGenerator.texturedModels.getOrDefault(block, defaultModel);
    }

    public Builder modelFor(Block block) {
        final TexturedModel texturedModel = this.getTextureModels(block, TexturedModel.CUBE.get(block));
        return modelFor(texturedModel);
    }

    public Builder modelFor(TexturedModel texturedModel) {
        return new Builder(texturedModel);
    }

    public static TextureMapping textureMappingOf(
            TextureSlot slotA,
            ResourceLocation locationA
    ) {
        return new TextureMapping().put(slotA, locationA);
    }

    public static TextureMapping textureMappingOf(
            TextureSlot slotA,
            ResourceLocation locationA,
            TextureSlot slotB,
            ResourceLocation locationB
    ) {
        return textureMappingOf(slotA, locationA).put(slotB, locationB);
    }

    public void createBookshelf(Block shelf, Block planks) {
        TextureMapping textureMapping = TextureMapping.column(TextureMapping.getBlockTexture(shelf), TextureMapping.getBlockTexture(planks));
        ResourceLocation resourceLocation = ModelTemplates.CUBE_COLUMN.create(shelf, textureMapping, vanillaGenerator.modelOutput);
        acceptBlockState(vanillaGenerator.createSimpleBlock(shelf, resourceLocation));
    }

    public void createLadder(Block ladderBlock) {
        vanillaGenerator.createNonTemplateHorizontalBlock(ladderBlock);
        vanillaGenerator.createSimpleFlatItemModel(ladderBlock);
    }

    public class Builder {
        private ResourceLocation fullBlockLocation;
        private final TexturedModel model;
        private final TextureMapping mapping;
        private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();

        private Builder(TexturedModel model) {
            this.model = model;
            this.mapping = model.getMapping();
        }

        public Builder createFullBlock(Block fullBlock) {
            this.fullBlockLocation = model
                    .getTemplate()
                    .create(fullBlock, mapping, vanillaGenerator.modelOutput);
            acceptBlockState(
                    BlockModelGenerators.createSimpleBlock(
                            fullBlock,
                            fullBlockLocation
                    )
            );

            return this;
        }

        public Builder createWall(Block wallBlock) {
            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.WALL_POST,
                    ModelTemplates.WALL_LOW_SIDE,
                    ModelTemplates.WALL_TALL_SIDE
            ).map(template -> template.create(wallBlock, mapping, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createWall(wallBlock, locations.get(0), locations.get(1), locations.get(2)));

            createInventoryModel(wallBlock, ModelTemplates.WALL_INVENTORY, mapping);

            return this;
        }

        public Builder createButton(Block buttonBlock) {
            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.BUTTON,
                    ModelTemplates.BUTTON_PRESSED
            ).map(template -> template.create(buttonBlock, mapping, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createButton(buttonBlock, locations.get(0), locations.get(1)));
            createInventoryModel(buttonBlock, ModelTemplates.BUTTON_INVENTORY, this.mapping);

            return this;
        }

        public Builder createDoor(Block doorBlock) {
            vanillaGenerator.createDoor(doorBlock);
            return this;
        }

        public Builder createCustomFence(Block fenceBlock) {
            final TextureMapping particles = TextureMapping.customParticle(fenceBlock);

            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.CUSTOM_FENCE_POST,
                    ModelTemplates.CUSTOM_FENCE_SIDE_NORTH,
                    ModelTemplates.CUSTOM_FENCE_SIDE_EAST,
                    ModelTemplates.CUSTOM_FENCE_SIDE_SOUTH,
                    ModelTemplates.CUSTOM_FENCE_SIDE_WEST
            ).map(template -> template.create(fenceBlock, particles, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createCustomFence(fenceBlock, locations.get(0), locations.get(1), locations.get(2), locations.get(3), locations.get(4)));
            createInventoryModel(fenceBlock, ModelTemplates.CUSTOM_FENCE_INVENTORY, particles);

            return this;
        }

        public Builder createFence(Block fenceBlock) {
            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.FENCE_POST,
                    ModelTemplates.FENCE_SIDE
            ).map(template -> template.create(fenceBlock, mapping, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createFence(fenceBlock, locations.get(0), locations.get(1)));
            createInventoryModel(fenceBlock, ModelTemplates.FENCE_INVENTORY, this.mapping);

            return this;
        }

        public Builder createCustomFenceGate(Block gateBlock) {
            final TextureMapping particles = TextureMapping.customParticle(gateBlock);

            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.CUSTOM_FENCE_GATE_OPEN,
                    ModelTemplates.CUSTOM_FENCE_GATE_CLOSED,
                    ModelTemplates.CUSTOM_FENCE_GATE_WALL_OPEN,
                    ModelTemplates.CUSTOM_FENCE_GATE_WALL_CLOSED
            ).map(template -> template.create(gateBlock, particles, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createFenceGate(gateBlock, locations.get(0), locations.get(1), locations.get(2), locations.get(3), false));

            return this;
        }

        public Builder createFenceGate(Block gateBlock) {
            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.FENCE_GATE_OPEN,
                    ModelTemplates.FENCE_GATE_CLOSED,
                    ModelTemplates.FENCE_GATE_WALL_OPEN,
                    ModelTemplates.FENCE_GATE_WALL_CLOSED
            ).map(template -> template.create(gateBlock, mapping, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createFenceGate(gateBlock, locations.get(0), locations.get(1), locations.get(2), locations.get(3), true));

            return this;
        }

        public Builder createPressurePlate(Block plateBlock) {
            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.PRESSURE_PLATE_UP,
                    ModelTemplates.PRESSURE_PLATE_DOWN
            ).map(template -> template.create(plateBlock, mapping, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createPressurePlate(plateBlock, locations.get(0), locations.get(1)));

            return this;
        }

        public Builder createSign(Block signBlock, Block wallSignBlock) {
            final ResourceLocation particleLocation = ModelTemplates.PARTICLE_ONLY.create(signBlock, this.mapping, vanillaGenerator.modelOutput);

            acceptBlockState(BlockModelGenerators.createSimpleBlock(signBlock, particleLocation));
            acceptBlockState(BlockModelGenerators.createSimpleBlock(wallSignBlock, particleLocation));

            vanillaGenerator.createSimpleFlatItemModel(signBlock.asItem());
            vanillaGenerator.skipAutoItemBlock(wallSignBlock);

            return this;
        }

        public Builder createStairs(Block stairBlock) {
            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.STAIRS_INNER,
                    ModelTemplates.STAIRS_STRAIGHT,
                    ModelTemplates.STAIRS_OUTER
            ).map(template -> this.computeModelIfAbsent(template, stairBlock)).toList();

            acceptBlockState(BlockModelGenerators.createStairs(stairBlock, locations.get(0), locations.get(1), locations.get(2)));
            delegateItemModel(stairBlock, locations.get(1));

            return this;
        }

        private Builder createFullBlockVariant(Block block) {
            final TexturedModel texturedModel = getTextureModels(block, TexturedModel.CUBE.get(block));
            final ResourceLocation resourceLocation = texturedModel.create(block, vanillaGenerator.modelOutput);

            acceptBlockState(BlockModelGenerators.createSimpleBlock(block, resourceLocation));

            return this;
        }

        private void createTrapdoor(Block block, boolean hasOrientation) {
            if (!hasOrientation) {
                vanillaGenerator.createTrapdoor(block);
            } else {
                vanillaGenerator.createOrientableTrapdoor(block);
            }
        }

        public Builder createSlab(Block slabBlock) {
            if (this.fullBlockLocation == null) {
                throw new IllegalStateException("Please call createFullBlock before calling createSlab");
            } else {
                final List<ResourceLocation> locations = Stream.of(
                        ModelTemplates.SLAB_BOTTOM,
                        ModelTemplates.SLAB_TOP
                ).map(template -> this.computeModelIfAbsent(template, slabBlock)).toList();

                acceptBlockState(BlockModelGenerators.createSlab(slabBlock, locations.get(0), locations.get(1), this.fullBlockLocation));
                delegateItemModel(slabBlock, locations.get(0));

                return this;
            }
        }

        private ResourceLocation computeModelIfAbsent(ModelTemplate modelTemplate, Block block) {
            return this.models.computeIfAbsent(modelTemplate, (m) -> m.create(block, this.mapping, vanillaGenerator.modelOutput));
        }

        private void createInventoryModel(Block wallBlock, ModelTemplate inventoryModel, TextureMapping mapping) {
            delegateItemModel(wallBlock, inventoryModel.create(wallBlock, mapping, vanillaGenerator.modelOutput));
        }
    }


}
