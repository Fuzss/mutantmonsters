package fuzs.mutantmonsters.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.*;
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
import fuzs.puzzleslib.api.client.event.v1.renderer.ExtractRenderStateCallback;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ItemTooltipRegistry;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.entity.EntityType;

public class MutantMonstersClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ExtractRenderStateCallback.EVENT.register(CreeperMinionOnShoulderLayer::onExtractRenderState);
    }

    @Override
    public void onClientSetup() {
        ItemTooltipRegistry.registerItemTooltip(ArmorBlockItem.class, ArmorBlockItem::getDescriptionComponent);
        ItemTooltipRegistry.registerItemTooltip(SkeletonArmorItem.class, SkeletonArmorItem::getDescriptionComponent);
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
    public void onRegisterLivingEntityRenderLayers(LivingEntityRenderLayersContext context) {
        context.registerRenderLayer(EntityType.PLAYER, CreeperMinionOnShoulderLayer::new);
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
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_SKELETON_SKULL,
                ModelLayerLocations::createSkullModelLayer);
        context.registerLayerDefinition(ModelLayerLocations.CREEPER_MINION_EGG,
                () -> CreeperMinionEggModel.createBodyLayer(new CubeDeformation(0.0F)));
        context.registerLayerDefinition(ModelLayerLocations.CREEPER_MINION_EGG_ARMOR,
                () -> CreeperMinionEggModel.createBodyLayer(new CubeDeformation(1.0F)));
        context.registerLayerDefinition(ModelLayerLocations.CREEPER_MINION,
                () -> CreeperModel.createBodyLayer(new CubeDeformation(0.0F))
                        .apply(CreeperMinionModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModelLayerLocations.CREEPER_MINION_ARMOR,
                () -> CreeperModel.createBodyLayer(new CubeDeformation(2.0F))
                        .apply(CreeperMinionModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModelLayerLocations.CREEPER_MINION_SHOULDER,
                () -> CreeperModel.createBodyLayer(new CubeDeformation(0.0F))
                        .apply(CreeperMinionModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModelLayerLocations.CREEPER_MINION_SHOULDER_ARMOR,
                () -> CreeperModel.createBodyLayer(new CubeDeformation(2.0F))
                        .apply(CreeperMinionModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModelLayerLocations.ENDERSOUL_CLONE, EndermanModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.ENDERSOUL_FRAGMENT,
                EndersoulFragmentModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.ENDERSOUL_HAND_LEFT,
                () -> EndersoulHandModel.createBodyLayer(false));
        context.registerLayerDefinition(ModelLayerLocations.ENDERSOUL_HAND_RIGHT,
                () -> EndersoulHandModel.createBodyLayer(true));
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_ARROW, MutantArrowModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_CREEPER,
                () -> MutantCreeperModel.createBodyLayer(new CubeDeformation(0.0F)));
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_CREEPER_ARMOR,
                () -> MutantCreeperModel.createBodyLayer(new CubeDeformation(2.0F)));
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_CROSSBOW, MutantCrossbowModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_ENDERMAN, MutantEndermanModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.ENDERMAN_CLONE, EndermanModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_SKELETON, MutantSkeletonModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_SKELETON_PART,
                MutantSkeletonPartModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_SKELETON_PART_SPINE, () -> {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            MutantSkeletonModel.Spine.createSpineLayer(root, -1);
            return LayerDefinition.create(mesh, 128, 128);
        });
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_SNOW_GOLEM,
                () -> MutantSnowGolemModel.createBodyLayer(128, 64));
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_SNOW_GOLEM_HEAD,
                () -> MutantSnowGolemModel.createBodyLayer(64, 32));
        context.registerLayerDefinition(ModelLayerLocations.MUTANT_ZOMBIE, MutantZombieModel::createBodyLayer);
        context.registerLayerDefinition(ModelLayerLocations.SPIDER_PIG,
                () -> SpiderPigModel.createBodyLayer());
        context.registerLayerDefinition(ModelLayerLocations.SPIDER_PIG_BABY,
                () -> SpiderPigModel.createBodyLayer().apply(SpiderPigModel.BABY_TRANSFORMER));
        context.registerLayerDefinition(ModelLayerLocations.SPIDER_PIG_SADDLE,
                () -> SpiderPigModel.createBodyLayer());
        context.registerLayerDefinition(ModelLayerLocations.SPIDER_PIG_BABY_SADDLE,
                () -> SpiderPigModel.createBodyLayer()
                        .apply(SpiderPigModel.BABY_TRANSFORMER));
    }

    @Override
    public void onRegisterSkullRenderers(SkullRenderersContext context) {
        context.registerSkullRenderer(ModRegistry.MUTANT_SKELETON_SKULL_TYPE,
                MutantSkeletonRenderer.TEXTURE_LOCATION,
                (EntityModelSet entityModelSet) -> {
                    return new SkullModel(entityModelSet.bakeLayer(ModelLayerLocations.MUTANT_SKELETON_SKULL));
                });
    }

    @Override
    public void onRegisterEntitySpectatorShaders(EntitySpectatorShadersContext context) {
        context.registerSpectatorShader(ResourceLocationHelper.withDefaultNamespace("shaders/post/creeper.json"),
                ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(),
                ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value());
        context.registerSpectatorShader(ResourceLocationHelper.withDefaultNamespace("shaders/post/invert.json"),
                ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value(),
                ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value());
    }

    @Override
    public void onRegisterRenderPipelines(RenderPipelinesContext context) {
        context.registerRenderPipeline(ModRenderType.ENERGY_SWIRL_RENDER_PIPELINE);
    }
}
