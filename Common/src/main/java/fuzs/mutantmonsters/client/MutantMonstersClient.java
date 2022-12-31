package fuzs.mutantmonsters.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.particle.EndersoulParticle;
import fuzs.mutantmonsters.client.particle.SkullSpiritParticle;
import fuzs.mutantmonsters.client.renderer.entity.*;
import fuzs.mutantmonsters.client.renderer.entity.layers.CreeperMinionShoulderLayer;
import fuzs.mutantmonsters.client.renderer.entity.model.*;
import fuzs.mutantmonsters.client.renderer.model.MBRenderType;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.client.core.ClientAbstractions;
import fuzs.puzzleslib.client.core.ClientModConstructor;
import fuzs.puzzleslib.client.renderer.DynamicBuiltinModelItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MutantMonstersClient implements ClientModConstructor {

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(ModRegistry.BODY_PART_ENTITY_TYPE.get(), BodyPartRenderer::new);
        context.registerEntityRenderer(ModRegistry.CHEMICAL_X_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        context.registerEntityRenderer(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get(), CreeperMinionRenderer::new);
        context.registerEntityRenderer(ModRegistry.CREEPER_MINION_EGG_ENTITY_TYPE.get(), CreeperMinionEggRenderer::new);
        context.registerEntityRenderer(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get(), EndersoulCloneRenderer::new);
        context.registerEntityRenderer(ModRegistry.ENDERSOUL_FRAGMENT_ENTITY_TYPE.get(), EndersoulFragmentRenderer::new);
        context.registerEntityRenderer(ModRegistry.MUTANT_ARROW_ENTITY_TYPE.get(), MutantArrowRenderer::new);
        context.registerEntityRenderer(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get(), MutantCreeperRenderer::new);
        context.registerEntityRenderer(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get(), MutantEndermanRenderer::new);
        context.registerEntityRenderer(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), MutantSkeletonRenderer::new);
        context.registerEntityRenderer(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get(), MutantSnowGolemRenderer::new);
        context.registerEntityRenderer(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), MutantZombieRenderer::new);
        context.registerEntityRenderer(ModRegistry.SKULL_SPIRIT_ENTITY_TYPE.get(), context1 -> {
            return new EntityRenderer<Entity>(context1) {

                @Override
                public ResourceLocation getTextureLocation(Entity entity) {
                    return null;
                }
            };
        });
        context.registerEntityRenderer(ModRegistry.SPIDER_PIG_ENTITY_TYPE.get(), SpiderPigRenderer::new);
        context.registerEntityRenderer(ModRegistry.THROWABLE_BLOCK_ENTITY_TYPE.get(), ThrowableBlockRenderer::new);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.SKULL_BLOCK_ENTITY_TYPE.get(), SkullBlockRenderer::new);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRegisterLivingEntityRenderLayers(LivingEntityRenderLayersContext context) {
        context.registerRenderLayer(EntityType.PLAYER, (renderLayerParent, context1) -> {
            return new CreeperMinionShoulderLayer<>((RenderLayerParent<Player, PlayerModel<Player>>) renderLayerParent, context1.getModelSet());
        });
    }

    @Override
    public void onRegisterAdditionalModels(AdditionalModelsContext context) {
        context.registerAdditionalModel(ENDERSOUL_MODEL);
    }

    public static final ModelResourceLocation ENDERSOUL_MODEL = new ModelResourceLocation(MutantMonsters.id("endersoul_hand_in_hand"), "inventory");

    public static BakedModel mutantmonsters$getModel(BakedModel bakedModel, ItemStack itemStack, ItemModelShaper itemModelShaper, ModelResourceLocation model) {
        if (itemStack.is(ModRegistry.ENDERSOUL_HAND_ITEM.get())) {
            return itemModelShaper.getModelManager().getModel(model);
        }
        return bakedModel;
    }

    @Override
    public void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
        context.register(ModRegistry.ENDERSOUL_HAND_ITEM.get(), new DynamicBuiltinModelItemRenderer() {
            private static final ResourceLocation ENDER_SOUL_HAND_TEXTURE = MutantMonsters.id("textures/item/endersoul_hand_model.png");

            private final Minecraft minecraft = Minecraft.getInstance();
            private EndersoulHandModel enderSoulHandModel;

            @Override
            public void renderByItem(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
                matrices.pushPose();
                float ageInTicks = (float) this.minecraft.player.tickCount + this.minecraft.getFrameTime();
                this.enderSoulHandModel.setAngles();
                VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(vertexConsumers, MBRenderType.energySwirl(ENDER_SOUL_HAND_TEXTURE, ageInTicks * 0.008F, ageInTicks * 0.008F), true, stack.hasFoil());
                this.enderSoulHandModel.renderToBuffer(matrices, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 0.9F, 0.3F, 1.0F, 1.0F);
                matrices.popPose();
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                this.enderSoulHandModel = new EndersoulHandModel(this.minecraft.getEntityModels().bakeLayer(ClientModRegistry.ENDERSOUL_HAND_RIGHT), true);
            }
        });
    }

    @Override
    public void onRegisterParticleProviders(ParticleProvidersContext context) {
        context.registerParticleFactory(ModRegistry.ENDERSOUL_PARTICLE_TYPE.get(), EndersoulParticle.Factory::new);
        context.registerParticleFactory(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.get(), SkullSpiritParticle.Factory::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON_SKULL, ClientModRegistry::createSkullModelLayer);
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_EGG, () -> CreeperMinionEggModel.createBodyLayer(new CubeDeformation(0.0F)));
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_EGG_ARMOR, () -> CreeperMinionEggModel.createBodyLayer(new CubeDeformation(1.0F)));
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION, () -> CreeperMinionModel.createBodyLayer(new CubeDeformation(0.0F)));
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_ARMOR, () -> CreeperMinionModel.createBodyLayer(new CubeDeformation(2.0F)));
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_SHOULDER, () -> CreeperMinionModel.createBodyLayer(new CubeDeformation(0.0F)));
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_SHOULDER_ARMOR, () -> CreeperMinionModel.createBodyLayer(new CubeDeformation(2.0F)));
        context.registerLayerDefinition(ClientModRegistry.ENDERSOUL_CLONE, EndermanModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.ENDERSOUL_FRAGMENT, EndersoulFragmentModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.ENDERSOUL_HAND_LEFT, () -> EndersoulHandModel.createBodyLayer(false));
        context.registerLayerDefinition(ClientModRegistry.ENDERSOUL_HAND_RIGHT, () -> EndersoulHandModel.createBodyLayer(true));
        context.registerLayerDefinition(ClientModRegistry.MUTANT_ARROW, MutantArrowModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_CREEPER, () -> MutantCreeperModel.createBodyLayer(new CubeDeformation(0.0F)));
        context.registerLayerDefinition(ClientModRegistry.MUTANT_CREEPER_ARMOR, () -> MutantCreeperModel.createBodyLayer(new CubeDeformation(2.0F)));
        context.registerLayerDefinition(ClientModRegistry.MUTANT_CROSSBOW, MutantCrossbowModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_ENDERMAN, MutantEndermanModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.ENDERMAN_CLONE, EndermanModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON, MutantSkeletonModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON_PART, MutantSkeletonPartModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON_PART_SPINE, () -> {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            MutantSkeletonModel.Spine.createSpineLayer(root, -1);
            return LayerDefinition.create(mesh, 128, 128);
        });
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SNOW_GOLEM, () -> MutantSnowGolemModel.createBodyLayer(128, 64));
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SNOW_GOLEM_HEAD, () -> MutantSnowGolemModel.createBodyLayer(64, 32));
        context.registerLayerDefinition(ClientModRegistry.MUTANT_ZOMBIE, MutantZombieModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.SPIDER_PIG, SpiderPigModel::createBodyLayer);
    }

    @Override
    public void onClientSetup() {
        ClientAbstractions.INSTANCE.getSkullTypeSkins().put(ModRegistry.MUTANT_SKELETON_SKULL_TYPE, MutantMonsters.id("textures/entity/mutant_skeleton.png"));
    }

    @Override
    public void onRegisterSkullRenderers(SkullRenderersContext context) {
        context.register((entityModelSet, context1) -> context1.accept(ModRegistry.MUTANT_SKELETON_SKULL_TYPE, new SkullModel(entityModelSet.bakeLayer(ClientModRegistry.MUTANT_SKELETON_SKULL))));
    }

    public void onRegisterEntitySpectatorShader(EntitySpectatorShaderContext context) {
        context.register(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get(), new ResourceLocation("shaders/post/creeper.json"));
        context.register(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get(), new ResourceLocation("shaders/post/invert.json"));
        context.register(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get(), new ResourceLocation("shaders/post/creeper.json"));
        context.register(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get(), new ResourceLocation("shaders/post/invert.json"));
    }
}
