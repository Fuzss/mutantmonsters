package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/mutant_skeleton.png");
    public static final ResourceLocation CROSSBOW_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_crossbow.png");

    public MutantSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantSkeletonModel(context.bakeLayer(ClientModRegistry.MUTANT_SKELETON),
                context.bakeLayer(ClientModRegistry.MUTANT_CROSSBOW)
        ), 0.6F);
        this.addLayer(new CrossbowLayer(this, context.getModelSet()));
    }

    @Override
    protected float getFlipDegrees(MutantSkeleton mutantSkeleton) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(MutantSkeleton mutantSkeleton) {
        return TEXTURE_LOCATION;
    }

    static class CrossbowLayer extends RenderLayer<MutantSkeleton, MutantSkeletonModel> {
        private final MutantArrowModel model;

        public CrossbowLayer(RenderLayerParent<MutantSkeleton, MutantSkeletonModel> renderer, EntityModelSet entityModelSet) {
            super(renderer);
            this.model = new MutantArrowModel(entityModelSet.bakeLayer(ClientModRegistry.MUTANT_ARROW));
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, MutantSkeleton mutantSkeleton, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            poseStack.pushPose();
            boolean leftHanded = mutantSkeleton.isLeftHanded();
            this.getParentModel().translateHand(leftHanded, poseStack);
            if (leftHanded) {
                poseStack.scale(-1.0F, 1.0F, -1.0F);
            }

            this.getParentModel().getCrossbow().renderToBuffer(poseStack, multiBufferSource.getBuffer(
                            this.getParentModel().getCrossbow().renderType(MutantSkeletonRenderer.CROSSBOW_TEXTURE_LOCATION)),
                    packedLight, OverlayTexture.NO_OVERLAY
            );
            poseStack.popPose();
            if (mutantSkeleton.getAnimation() == MutantSkeleton.SHOOT_ANIMATION &&
                    mutantSkeleton.getAnimationTick() > 10 && mutantSkeleton.getAnimationTick() < 26 ||
                    mutantSkeleton.getAnimation() == MutantSkeleton.MULTI_SHOT_ANIMATION &&
                            mutantSkeleton.getAnimationTick() > 16 && mutantSkeleton.getAnimationTick() < 24) {
                poseStack.pushPose();
                this.getParentModel().translateHand(leftHanded, poseStack);
                poseStack.translate(leftHanded ? 0.2 : -0.2, 0.4, -1.8);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.scale(1.2F, 1.2F, 1.2F);
                VertexConsumer vertexBuilder = multiBufferSource.getBuffer(
                        this.model.renderType(MutantArrowRenderer.TEXTURE_LOCATION));
                this.model.renderToBuffer(poseStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
    }
}
