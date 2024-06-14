package org.betterx.wover.block.api.model;

import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WoverBlockModelGenerators {
    public static final ResourceLocation CROSS = ResourceLocation.withDefaultNamespace("block/cross");
    public static final ResourceLocation CUBE = ResourceLocation.withDefaultNamespace("block/cube");
    public static final ResourceLocation CUBE_ALL = ResourceLocation.withDefaultNamespace("block/cube_all");
    public static final ResourceLocation COMPOSTER = LibWoverBlock.C.id("block/composter");

    public static final ModelTemplate COMPOSTER_MODEL = new ModelTemplate(Optional.of(COMPOSTER), Optional.empty(), TextureSlot.SIDE, TextureSlot.BOTTOM, TextureSlot.TOP);
    public final BlockModelGenerators vanillaGenerator;

    public WoverBlockModelGenerators(
            BlockModelGenerators vanillaGenerator
    ) {
        this.vanillaGenerator = vanillaGenerator;
    }

    public void createObsidianVariants(WoverBlockModelGenerators generators, Block obsidianBlock) {
        var model = generators.getTextureModels(obsidianBlock, TexturedModel.CUBE.get(obsidianBlock));
        var template = model.getTemplate();
        var modelLocation = template.create(obsidianBlock, model.getMapping(), generators.vanillaGenerator.modelOutput);
        final VariantProperties.Rotation[] rotations = {
                VariantProperties.Rotation.R0,
                VariantProperties.Rotation.R90,
                VariantProperties.Rotation.R180,
                VariantProperties.Rotation.R270
        };

        final Variant[] variants = new Variant[16];
        int idx = 0;
        for (VariantProperties.Rotation rotation : rotations) {
            for (VariantProperties.Rotation rotationY : rotations) {
                variants[idx] = Variant
                        .variant()
                        .with(VariantProperties.MODEL, modelLocation);
                if (rotation != VariantProperties.Rotation.R0)
                    variants[idx] = variants[idx].with(VariantProperties.X_ROT, rotation);

                if (rotationY != VariantProperties.Rotation.R0)
                    variants[idx] = variants[idx].with(VariantProperties.Y_ROT, rotationY);

                idx++;
            }
        }

        generators.acceptBlockState(MultiVariantGenerator.multiVariant(obsidianBlock, variants));
    }

    public void acceptBlockState(BlockStateGenerator blockStateGenerator) {
        this.vanillaGenerator.blockStateOutput.accept(blockStateGenerator);
    }

    public void acceptModelOutput(ResourceLocation id, Supplier<JsonElement> supplier) {
        this.vanillaGenerator.modelOutput.accept(id, supplier);
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
        return new Builder(texturedModel, texturedModel.getMapping());
    }

    public Builder modelFor(TexturedModel texturedModel, TextureMapping textureMapping) {
        return new Builder(texturedModel, textureMapping);
    }

    public Builder modelFor(Block block, TextureMapping textureMappingOverride) {
        final TexturedModel texturedModel = this.getTextureModels(block, TexturedModel.CUBE.get(block));
        return new Builder(texturedModel, textureMappingOverride);
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

    public void createHangingSign(Block baseBlock, Block hangingSignBlock, Block wallHangingSignBlock) {
        ResourceLocation resourceLocation = ModelTemplates.PARTICLE_ONLY.create(hangingSignBlock, new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(baseBlock)), vanillaGenerator.modelOutput);
        acceptBlockState(vanillaGenerator.createSimpleBlock(hangingSignBlock, resourceLocation));
        acceptBlockState(vanillaGenerator.createSimpleBlock(wallHangingSignBlock, resourceLocation));
        vanillaGenerator.createSimpleFlatItemModel(hangingSignBlock.asItem());
        vanillaGenerator.skipAutoItemBlock(wallHangingSignBlock);
    }

    public void createBarrel(Block barrelBlock) {
        ResourceLocation resourceLocation = TextureMapping.getBlockTexture(barrelBlock, "_top_open");
        acceptBlockState(MultiVariantGenerator
                .multiVariant(barrelBlock)
                .with(vanillaGenerator.createColumnWithFacing())
                .with(PropertyDispatch
                        .property(BlockStateProperties.OPEN)
                        .select(false, Variant
                                .variant()
                                .with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM.create(barrelBlock, this.vanillaGenerator.modelOutput))
                        )
                        .select(true, Variant
                                .variant()
                                .with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM
                                        .get(barrelBlock)
                                        .updateTextures((textureMapping) -> {
                                            textureMapping.put(TextureSlot.TOP, resourceLocation);
                                        })
                                        .createWithSuffix(barrelBlock, "_open", this.vanillaGenerator.modelOutput)
                                )
                        )
                )
        );
    }

    public void createComposter(Block composterBlock) {
        var mapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(composterBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(composterBlock, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(composterBlock, "_bottom"));
        var location = COMPOSTER_MODEL.create(composterBlock, mapping, vanillaGenerator.modelOutput);
        acceptBlockState(MultiPartGenerator
                .multiPart(composterBlock)
                .with(Variant.variant().with(VariantProperties.MODEL, location))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents1")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents2")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents3")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents4")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents5")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents6")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents7")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents_ready"))));
    }

    public class Builder {
        private ResourceLocation fullBlockLocation;
        private final TexturedModel model;
        private final TextureMapping mapping;
        private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();

        private Builder(TexturedModel model, TextureMapping mapping) {
            this.model = model;
            this.mapping = mapping;
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
            final List<ResourceLocation> locations = Stream
                    .of(
                            ModelTemplates.STAIRS_INNER,
                            ModelTemplates.STAIRS_STRAIGHT,
                            ModelTemplates.STAIRS_OUTER
                    )
                    .map(template -> this.computeModelIfAbsent(template, stairBlock)).toList();

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


        public Builder createChest(Block chestBlock) {
            final var baseModel = ModelTemplates
                    .PARTICLE_ONLY
                    .create(
                            ModelLocationUtils.getModelLocation(chestBlock),
                            this.mapping,
                            vanillaGenerator.modelOutput
                    );
            vanillaGenerator.skipAutoItemBlock(chestBlock);
            acceptBlockState(BlockModelGenerators.createSimpleBlock(chestBlock, baseModel));
            return this;
        }

        private ResourceLocation computeModelIfAbsent(ModelTemplate modelTemplate, Block block) {
            return this.models.computeIfAbsent(modelTemplate, (m) -> m.create(block, this.mapping, vanillaGenerator.modelOutput));
        }

        private void createInventoryModel(Block wallBlock, ModelTemplate inventoryModel, TextureMapping mapping) {
            delegateItemModel(wallBlock, inventoryModel.create(wallBlock, mapping, vanillaGenerator.modelOutput));
        }
    }


}
