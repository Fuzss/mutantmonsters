package fuzs.mutantmonsters.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.*;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.particle.EndersoulParticle;
import fuzs.mutantmonsters.client.particle.SkullSpiritParticle;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.*;
import fuzs.mutantmonsters.client.renderer.entity.layers.CreeperMinionOnShoulderLayer;
import fuzs.mutantmonsters.client.renderer.special.EndersoulHandSpecialRenderer;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.world.item.ArmorBlockItem;
import fuzs.mutantmonsters.world.item.SkeletonArmorItem;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.*;
import fuzs.puzzleslib.api.client.event.v1.renderer.AddLivingEntityRenderLayersCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.ExtractRenderStateCallback;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ItemTooltipRegistry;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;

public class MutantMonstersClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        AddLivingEntityRenderLayersCallback.EVENT.register(CreeperMinionOnShoulderLayer::addLivingEntityRenderLayers);
        ExtractRenderStateCallback.EVENT.register(CreeperMinionOnShoulderLayer::onExtractRenderState);
    }

    @Override
    public void onClientSetup() {
        ItemTooltipRegistry.ITEM.registerItemTooltip(ArmorBlockItem.class, ArmorBlockItem::getDescriptionComponent);
        ItemTooltipRegistry.ITEM.registerItemTooltip(SkeletonArmorItem.class,
                SkeletonArmorItem::getDescriptionComponent);
    }

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(ModEntityTypes.BODY_PART_ENTITY_TYPE.value(), BodyPartRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(), CreeperMinionRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.CREEPER_MINION_EGG_ENTITY_TYPE.value(),
                CreeperMinionEggRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value(), EndersoulCloneRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.ENDERSOUL_FRAGMENT_ENTITY_TYPE.value(),
                EndersoulFragmentRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_ARROW_ENTITY_TYPE.value(), MutantArrowRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value(), MutantCreeperRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(), MutantEndermanRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(), MutantSkeletonRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                MutantSnowGolemRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value(), MutantZombieRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.SKULL_SPIRIT_ENTITY_TYPE.value(), SkullSpiritRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value(), SpiderPigRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.THROWABLE_BLOCK_ENTITY_TYPE.value(), ThrowableBlockRenderer::new);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.SKULL_BLOCK_ENTITY_TYPE.value(), SkullBlockRenderer::new);
    }

    @Override
    public void onRegisterItemModels(ItemModelsContext context) {
        context.registerSpecialModelRenderer(MutantMonsters.id("endersoul_hand"),
                EndersoulHandSpecialRenderer.Unbaked.MAP_CODEC);
    }

    @Override
    public void onRegisterParticleProviders(ParticleProvidersContext context) {
        context.registerParticleProvider(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), EndersoulParticle.Factory::new);
        context.registerParticleProvider(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value(),
                SkullSpiritParticle.Factory::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(ModModelLayers.MUTANT_SKELETON_SKULL, ModModelLayers::createSkullModelLayer);
        context.registerLayerDefinition(ModModelLayers.CREEPER_MINION_EGG,
                () -> CreeperMinionEggModel.createBodyLayer(CubeDeformation.NONE));
        context.registerLayerDefinition(ModModelLayers.CREEPER_MINION_EGG_ARMOR,
                () -> CreeperMinionEggModel.createBodyLayer(new CubeDeformation(1.0F)));
        context.registerLayerDefinition(ModModelLayers.CREEPER_MINION,
                () -> CreeperModel.createBodyLayer(CubeDeformation.NONE).apply(CreeperMinionModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModModelLayers.CREEPER_MINION_ARMOR,
                () -> CreeperModel.createBodyLayer(new CubeDeformation(2.0F))
                        .apply(CreeperMinionModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModModelLayers.CREEPER_MINION_SHOULDER,
                () -> CreeperModel.createBodyLayer(CubeDeformation.NONE).apply(CreeperMinionModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModModelLayers.CREEPER_MINION_SHOULDER_ARMOR,
                () -> CreeperModel.createBodyLayer(new CubeDeformation(2.0F))
                        .apply(CreeperMinionModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModModelLayers.ENDERSOUL_CLONE, EndermanModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.ENDERSOUL_FRAGMENT, EndersoulFragmentModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.ENDERSOUL_HAND_LEFT,
                () -> EndersoulHandModel.createBodyLayer(false));
        context.registerLayerDefinition(ModModelLayers.ENDERSOUL_HAND_RIGHT,
                () -> EndersoulHandModel.createBodyLayer(true));
        context.registerLayerDefinition(ModModelLayers.MUTANT_ARROW, MutantArrowModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.MUTANT_CREEPER,
                () -> MutantCreeperModel.createBodyLayer(CubeDeformation.NONE));
        context.registerLayerDefinition(ModModelLayers.MUTANT_CREEPER_ARMOR,
                () -> MutantCreeperModel.createBodyLayer(new CubeDeformation(2.0F)));
        context.registerLayerDefinition(ModModelLayers.MUTANT_CROSSBOW, MutantCrossbowModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.MUTANT_ENDERMAN, MutantEndermanModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.ENDERMAN_CLONE, EndermanModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.MUTANT_SKELETON, MutantSkeletonModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.MUTANT_SKELETON_PART, MutantSkeletonPartModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.MUTANT_SKELETON_PART_SPINE,
                MutantSkeletonModel.Spine::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.MUTANT_SNOW_GOLEM, MutantSnowGolemModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.MUTANT_SNOW_GOLEM_HEAD, MutantSnowGolemModel::createHeadLayer);
        context.registerLayerDefinition(ModModelLayers.MUTANT_ZOMBIE, MutantZombieModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.SPIDER_PIG, SpiderPigModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.SPIDER_PIG_BABY,
                () -> SpiderPigModel.createBodyLayer().apply(SpiderPigModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModModelLayers.SPIDER_PIG_SADDLE, SpiderPigModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.SPIDER_PIG_BABY_SADDLE,
                () -> SpiderPigModel.createBodyLayer().apply(SpiderPigModel.BABY_TRANSFORMER));
    }

    @Override
    public void onRegisterSkullRenderers(SkullRenderersContext context) {
        context.registerSkullRenderer(ModRegistry.MUTANT_SKELETON_SKULL_TYPE,
                MutantSkeletonRenderer.TEXTURE_LOCATION,
                (EntityModelSet entityModelSet) -> {
                    return new SkullModel(entityModelSet.bakeLayer(ModModelLayers.MUTANT_SKELETON_SKULL));
                });
    }

    @Override
    public void onRegisterEntitySpectatorShaders(EntitySpectatorShadersContext context) {
        context.registerSpectatorShader(ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(),
                ResourceLocationHelper.withDefaultNamespace("shaders/post/creeper.json"));
        context.registerSpectatorShader(ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value(),
                ResourceLocationHelper.withDefaultNamespace("shaders/post/creeper.json"));
        context.registerSpectatorShader(ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value(),
                ResourceLocationHelper.withDefaultNamespace("shaders/post/invert.json"));
        context.registerSpectatorShader(ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(),
                ResourceLocationHelper.withDefaultNamespace("shaders/post/invert.json"));
    }

    @Override
    public void onRegisterRenderPipelines(RenderPipelinesContext context) {
        context.registerRenderPipeline(ModRenderType.ENERGY_SWIRL_RENDER_PIPELINE);
    }
}
