package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.model.MutantSnowGolemModel;
import fuzs.mutantmonsters.client.renderer.model.MBRenderType;
import fuzs.mutantmonsters.entity.mutant.MutantSnowGolemEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class MutantSnowGolemRenderer extends MobRenderer<MutantSnowGolemEntity, MutantSnowGolemModel> {
    static final ResourceLocation TEXTURE = MutantMonsters.getEntityTexture("mutant_snow_golem/mutant_snow_golem");
    private static final ResourceLocation JACK_O_LANTERN_TEXTURE = MutantMonsters.getEntityTexture("mutant_snow_golem/jack_o_lantern");
    private static final RenderType GLOW_RENDER_TYPE = MBRenderType.eyes(MutantMonsters.getEntityTexture("mutant_snow_golem/glow"));

    public MutantSnowGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantSnowGolemModel(context.bakeLayer(ClientModRegistry.MUTANT_SNOW_GOLEM)), 0.7F);
        this.addLayer(new JackOLanternLayer(this, context.getModelSet()));
        this.addLayer(new HeldBlockLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public void render(MutantSnowGolemEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        Player owner = entityIn.getOwner();
        if (owner != null) {
            matrixStackIn.pushPose();
            if (this.shouldShowName(entityIn)) {
                matrixStackIn.translate(0.0, 0.25874999165534973, 0.0);
            }

            this.renderNameTag(entityIn, owner.getDisplayName().copy().withStyle((style) -> {
                return style.withItalic(true);
            }), matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }

    }

    @Override
    public ResourceLocation getTextureLocation(MutantSnowGolemEntity entity) {
        return TEXTURE;
    }

    static class HeldBlockLayer extends RenderLayer<MutantSnowGolemEntity, MutantSnowGolemModel> {
        private final BlockRenderDispatcher blockRenderer;

        public HeldBlockLayer(RenderLayerParent<MutantSnowGolemEntity, MutantSnowGolemModel> entityRendererIn, BlockRenderDispatcher blockRenderer) {
            super(entityRendererIn);
            this.blockRenderer = blockRenderer;
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, MutantSnowGolemEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (livingEntity.isThrowing() && livingEntity.getThrowingTick() < 7) {
                matrixStackIn.pushPose();
                boolean leftHanded = livingEntity.isLeftHanded();
                float scale = Math.min(0.8F, ((float)livingEntity.getThrowingTick() + partialTicks) / 7.0F);
                matrixStackIn.translate(leftHanded ? -0.4 : 0.4, 0.0, 0.0);
                this.getParentModel().translateArm(leftHanded, matrixStackIn);
                matrixStackIn.translate(0.0, 0.9, 0.0);
                matrixStackIn.scale(-scale, -scale, scale);
                matrixStackIn.translate(-0.5, -0.5, 0.5);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
                this.blockRenderer.renderSingleBlock(Blocks.ICE.defaultBlockState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
                matrixStackIn.popPose();
            }

        }
    }

    static class JackOLanternLayer extends RenderLayer<MutantSnowGolemEntity, MutantSnowGolemModel> {
        private final MutantSnowGolemModel headModel;

        public JackOLanternLayer(RenderLayerParent<MutantSnowGolemEntity, MutantSnowGolemModel> entityRendererIn, EntityModelSet entityModelSet) {
            super(entityRendererIn);
            this.headModel = new MutantSnowGolemModel(entityModelSet.bakeLayer(ClientModRegistry.MUTANT_SNOW_GOLEM_HEAD)).setRenderHeadOnly();
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, MutantSnowGolemEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (livingEntity.hasJackOLantern()) {
                if (!livingEntity.isInvisible()) {
                    this.getParentModel().copyPropertiesTo(this.headModel);
                    renderColoredCutoutModel(this.headModel, MutantSnowGolemRenderer.JACK_O_LANTERN_TEXTURE, matrixStackIn, bufferIn, packedLightIn, livingEntity, 1.0F, 1.0F, 1.0F);
                }

                float green = Math.max(0.0F, 0.8F + 0.05F * Mth.cos(ageInTicks * 0.15F));
                float blue = Math.max(0.0F, 0.15F + 0.2F * Mth.cos(ageInTicks * 0.1F));
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(MutantSnowGolemRenderer.GLOW_RENDER_TYPE);
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, green, blue, 1.0F);
            }

        }
    }
}
