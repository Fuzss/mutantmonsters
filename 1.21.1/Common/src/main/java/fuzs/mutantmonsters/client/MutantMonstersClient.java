package fuzs.mutantmonsters.client;

import com.google.common.collect.Sets;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.*;
import fuzs.mutantmonsters.client.particle.EndersoulParticle;
import fuzs.mutantmonsters.client.particle.SkullSpiritParticle;
import fuzs.mutantmonsters.client.renderer.EndersoulHandRenderer;
import fuzs.mutantmonsters.client.renderer.HulkHammerModels;
import fuzs.mutantmonsters.client.renderer.entity.*;
import fuzs.mutantmonsters.client.renderer.entity.layers.CreeperMinionShoulderLayer;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.*;
import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.Arrays;
import java.util.EnumSet;

public class MutantMonstersClient implements ClientModConstructor {
    public static final ResourceLocation CHEMICAL_X_MODEL_PROPERTY = MutantMonsters.id("chemical_x");

    @Override
    public void onClientSetup() {
        SkullBlockRenderer.SKIN_BY_TYPE.put(ModRegistry.MUTANT_SKELETON_SKULL_TYPE,
                MutantMonsters.id("textures/entity/mutant_skeleton.png")
        );
        ItemModelDisplayOverrides.INSTANCE.register(EndersoulHandRenderer.ENDERSOUL_ITEM_MODEL,
                EndersoulHandRenderer.ENDERSOUL_BUILT_IN_MODEL, getVanillaItemModelDisplayOverrides()
        );
        ItemModelDisplayOverrides.INSTANCE.register(HulkHammerModels.HULK_HAMMER_ITEM_MODEL,
                HulkHammerModels.HULK_HAMMER_IN_HAND_MODEL, getVanillaItemModelDisplayOverrides()
        );
    }

    @Deprecated
    static ItemDisplayContext[] getVanillaItemModelDisplayOverrides() {
        // use the new registration method in Puzzles Lib
        return EnumSet.complementOf(Sets.newEnumSet(
                Arrays.asList(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED),
                ItemDisplayContext.class
        )).toArray(ItemDisplayContext[]::new);
    }

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(ModEntityTypes.BODY_PART_ENTITY_TYPE.value(), BodyPartRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(), CreeperMinionRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.CREEPER_MINION_EGG_ENTITY_TYPE.value(),
                CreeperMinionEggRenderer::new
        );
        context.registerEntityRenderer(ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value(), EndersoulCloneRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.ENDERSOUL_FRAGMENT_ENTITY_TYPE.value(),
                EndersoulFragmentRenderer::new
        );
        context.registerEntityRenderer(ModEntityTypes.MUTANT_ARROW_ENTITY_TYPE.value(), MutantArrowRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value(), MutantCreeperRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(), MutantEndermanRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(), MutantSkeletonRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                MutantSnowGolemRenderer::new
        );
        context.registerEntityRenderer(ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value(), MutantZombieRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.SKULL_SPIRIT_ENTITY_TYPE.value(), context1 -> {
            return new EntityRenderer<Entity>(context1) {

                @Override
                public ResourceLocation getTextureLocation(Entity entity) {
                    return null;
                }
            };
        });
        context.registerEntityRenderer(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value(), SpiderPigRenderer::new);
        context.registerEntityRenderer(ModEntityTypes.THROWABLE_BLOCK_ENTITY_TYPE.value(), ThrowableBlockRenderer::new);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.SKULL_BLOCK_ENTITY_TYPE.value(), SkullBlockRenderer::new);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRegisterLivingEntityRenderLayers(LivingEntityRenderLayersContext context) {
        context.registerRenderLayer(EntityType.PLAYER,
                (RenderLayerParent<Player, EntityModel<Player>> renderLayerParent, EntityRendererProvider.Context entityRendererProvider) -> {
                    return (RenderLayer<Player, EntityModel<Player>>) (RenderLayer<?, ?>) new CreeperMinionShoulderLayer<>(
                            (RenderLayerParent<Player, PlayerModel<Player>>) (RenderLayerParent<?, ?>) renderLayerParent,
                            entityRendererProvider.getModelSet()
                    );
                }
        );
    }

    @Override
    public void onRegisterAdditionalModels(AdditionalModelsContext context) {
        context.registerAdditionalModel(EndersoulHandRenderer.ENDERSOUL_BUILT_IN_MODEL,
                HulkHammerModels.HULK_HAMMER_IN_HAND_MODEL
        );
    }

    @Override
    public void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
        context.registerItemRenderer(new EndersoulHandRenderer(), ModItems.ENDERSOUL_HAND_ITEM.value());
    }

    @Override
    public void onRegisterParticleProviders(ParticleProvidersContext context) {
        context.registerParticleProvider(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), EndersoulParticle.Factory::new);
        context.registerParticleProvider(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value(),
                SkullSpiritParticle.Factory::new
        );
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON_SKULL,
                ClientModRegistry::createSkullModelLayer
        );
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_EGG,
                () -> CreeperMinionEggModel.createBodyLayer(new CubeDeformation(0.0F))
        );
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_EGG_ARMOR,
                () -> CreeperMinionEggModel.createBodyLayer(new CubeDeformation(1.0F))
        );
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION,
                () -> CreeperMinionModel.createBodyLayer(new CubeDeformation(0.0F))
        );
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_ARMOR,
                () -> CreeperMinionModel.createBodyLayer(new CubeDeformation(2.0F))
        );
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_SHOULDER,
                () -> CreeperMinionModel.createBodyLayer(new CubeDeformation(0.0F))
        );
        context.registerLayerDefinition(ClientModRegistry.CREEPER_MINION_SHOULDER_ARMOR,
                () -> CreeperMinionModel.createBodyLayer(new CubeDeformation(2.0F))
        );
        context.registerLayerDefinition(ClientModRegistry.ENDERSOUL_CLONE, EndermanModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.ENDERSOUL_FRAGMENT, EndersoulFragmentModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.ENDERSOUL_HAND_LEFT,
                () -> EndersoulHandModel.createBodyLayer(false)
        );
        context.registerLayerDefinition(ClientModRegistry.ENDERSOUL_HAND_RIGHT,
                () -> EndersoulHandModel.createBodyLayer(true)
        );
        context.registerLayerDefinition(ClientModRegistry.MUTANT_ARROW, MutantArrowModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_CREEPER,
                () -> MutantCreeperModel.createBodyLayer(new CubeDeformation(0.0F))
        );
        context.registerLayerDefinition(ClientModRegistry.MUTANT_CREEPER_ARMOR,
                () -> MutantCreeperModel.createBodyLayer(new CubeDeformation(2.0F))
        );
        context.registerLayerDefinition(ClientModRegistry.MUTANT_CROSSBOW, MutantCrossbowModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_ENDERMAN, MutantEndermanModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.ENDERMAN_CLONE, EndermanModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON, MutantSkeletonModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON_PART,
                MutantSkeletonPartModel::createBodyLayer
        );
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON_PART_SPINE, () -> {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            MutantSkeletonModel.Spine.createSpineLayer(root, -1);
            return LayerDefinition.create(mesh, 128, 128);
        });
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SNOW_GOLEM,
                () -> MutantSnowGolemModel.createBodyLayer(128, 64)
        );
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SNOW_GOLEM_HEAD,
                () -> MutantSnowGolemModel.createBodyLayer(64, 32)
        );
        context.registerLayerDefinition(ClientModRegistry.MUTANT_ZOMBIE, MutantZombieModel::createBodyLayer);
        context.registerLayerDefinition(ClientModRegistry.SPIDER_PIG, SpiderPigModel::createBodyLayer);
    }

    @Override
    public void onRegisterSkullRenderers(SkullRenderersContext context) {
        context.registerSkullRenderer(
                (entityModelSet, context1) -> context1.accept(ModRegistry.MUTANT_SKELETON_SKULL_TYPE,
                        new SkullModel(entityModelSet.bakeLayer(ClientModRegistry.MUTANT_SKELETON_SKULL))
                ));
    }

    @Override
    public void onRegisterEntitySpectatorShaders(EntitySpectatorShaderContext context) {
        context.registerSpectatorShader(ResourceLocationHelper.withDefaultNamespace("shaders/post/creeper.json"),
                ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(), ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value()
        );
        context.registerSpectatorShader(ResourceLocationHelper.withDefaultNamespace("shaders/post/invert.json"),
                ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value(), ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value()
        );
    }

    @Override
    public void onRegisterItemModelProperties(ItemModelPropertiesContext context) {
        context.registerItemProperty(CHEMICAL_X_MODEL_PROPERTY, (itemStack, clientLevel, livingEntity, i) -> {
            return itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(
                    ModRegistry.CHEMICAL_X_POTION) ? 1.0F : 0.0F;
        }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
    }

    @Override
    public ContentRegistrationFlags[] getContentRegistrationFlags() {
        return new ContentRegistrationFlags[]{ContentRegistrationFlags.DYNAMIC_RENDERERS};
    }
}
