package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.MutantSkeletonPartModel;
import fuzs.mutantmonsters.client.renderer.entity.state.BodyPartRenderState;
import fuzs.mutantmonsters.world.entity.MutantSkeletonBodyPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class BodyPartRenderer extends EntityRenderer<MutantSkeletonBodyPart, BodyPartRenderState> {
    private final MutantSkeletonPartModel model;

    public BodyPartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MutantSkeletonPartModel(context.bakeLayer(ModelLayerLocations.MUTANT_SKELETON_PART),
                context.bakeLayer(ModelLayerLocations.MUTANT_SKELETON_PART_SPINE));
    }

    @Override
    public void extractRenderState(MutantSkeletonBodyPart entity, BodyPartRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.xRot = entity.getXRot(partialTick);
        reusedState.yRot = entity.getYRot(partialTick);
        reusedState.partId = entity.getPart();
    }

    @Override
    public BodyPartRenderState createRenderState() {
        return new BodyPartRenderState();
    }

    @Override
    public void render(BodyPartRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(renderState, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.xRot));
        poseStack.scale(1.2F, -1.2F, -1.2F);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(MutantSkeletonRenderer.TEXTURE_LOCATION));
        this.model.getPart(renderState.partId)
                .render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
