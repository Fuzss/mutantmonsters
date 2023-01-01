package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.MutantArrowModel;
import fuzs.mutantmonsters.client.model.MutantSkeletonModel;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MutantSkeletonRenderer extends MobRenderer<MutantSkeleton, MutantSkeletonModel> {
    public static final ResourceLocation TEXTURE = MutantMonsters.entityTexture("mutant_skeleton");
    private static final ResourceLocation CROSSBOW_TEXTURE = MutantMonsters.entityTexture("mutant_crossbow");

    public MutantSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantSkeletonModel(context.bakeLayer(ClientModRegistry.MUTANT_SKELETON), context.bakeLayer(ClientModRegistry.MUTANT_CROSSBOW)), 0.6F);
        this.addLayer(new CrossbowLayer(this, context.getModelSet()));
    }

    @Override
    protected float getFlipDegrees(MutantSkeleton entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(MutantSkeleton entity) {
        return TEXTURE;
    }

    static class CrossbowLayer extends RenderLayer<MutantSkeleton, MutantSkeletonModel> {
        private final MutantArrowModel model;

        public CrossbowLayer(RenderLayerParent<MutantSkeleton, MutantSkeletonModel> entityRendererIn, EntityModelSet entityModelSet) {
            super(entityRendererIn);
            this.model = new MutantArrowModel(entityModelSet.bakeLayer(ClientModRegistry.MUTANT_ARROW));
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, MutantSkeleton entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            matrixStackIn.pushPose();
            boolean leftHanded = entity.isLeftHanded();
            this.getParentModel().translateHand(leftHanded, matrixStackIn);
            if (leftHanded) {
                matrixStackIn.scale(-1.0F, 1.0F, -1.0F);
            }

            this.getParentModel().getCrossbow().renderToBuffer(matrixStackIn, bufferIn.getBuffer(this.getParentModel().getCrossbow().renderType(MutantSkeletonRenderer.CROSSBOW_TEXTURE)), packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
            if (entity.getAnimation() == MutantSkeleton.SHOOT_ANIMATION && entity.getAnimationTick() > 10 && entity.getAnimationTick() < 26 || entity.getAnimation() == MutantSkeleton.MULTI_SHOT_ANIMATION && entity.getAnimationTick() > 16 && entity.getAnimationTick() < 24) {
                matrixStackIn.pushPose();
                this.getParentModel().translateHand(leftHanded, matrixStackIn);
                matrixStackIn.translate(leftHanded ? 0.2 : -0.2, 0.4, -1.8);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                matrixStackIn.scale(1.2F, 1.2F, 1.2F);
                VertexConsumer vertexBuilder = bufferIn.getBuffer(this.model.renderType(MutantArrowRenderer.TEXTURE));
                this.model.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }
        }
    }
}
