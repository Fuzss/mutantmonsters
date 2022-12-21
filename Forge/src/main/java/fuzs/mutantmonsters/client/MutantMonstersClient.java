package fuzs.mutantmonsters.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.particle.EndersoulParticle;
import fuzs.mutantmonsters.client.particle.SkullSpiritParticle;
import fuzs.mutantmonsters.client.renderer.entity.model.EndersoulHandModel;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.client.core.ClientModConstructor;
import fuzs.puzzleslib.client.renderer.DynamicBuiltinModelItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MutantMonstersClient implements ClientModConstructor {

    @Override
    public void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
        context.register(ModRegistry.ENDERSOUL_HAND_ITEM.get(), new DynamicBuiltinModelItemRenderer() {
            private static final ResourceLocation ENDER_SOUL_HAND_TEXTURE = MutantMonsters.prefix("textures/item/endersoul_hand_model.png");

            private final EndersoulHandModel enderSoulHandModel = new EndersoulHandModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientModRegistry.ENDER_SOUL_HAND_RIGHT), true);

            @Override
            public void renderByItem(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
                matrices.pushPose();
                float ageInTicks = (float) Minecraft.getInstance().player.tickCount + Minecraft.getInstance().getFrameTime();
                this.enderSoulHandModel.setAngles();
                VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(vertexConsumers, RenderType.energySwirl(ENDER_SOUL_HAND_TEXTURE, ageInTicks * 0.008F, ageInTicks * 0.008F), true, stack.hasFoil());
                this.enderSoulHandModel.renderToBuffer(matrices, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 0.9F, 0.3F, 1.0F, 1.0F);
                matrices.popPose();
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
        context.registerLayerDefinition(ClientModRegistry.ENDER_SOUL_HAND_LEFT, () -> EndersoulHandModel.createBodyLayer(false));
        context.registerLayerDefinition(ClientModRegistry.ENDER_SOUL_HAND_RIGHT, () -> EndersoulHandModel.createBodyLayer(true));
        context.registerLayerDefinition(ClientModRegistry.MUTANT_SKELETON_SKULL, ClientModRegistry::createSkullModelLayer);
    }
}
