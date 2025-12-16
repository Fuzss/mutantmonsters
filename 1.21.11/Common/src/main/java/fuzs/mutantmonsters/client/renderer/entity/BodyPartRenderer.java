package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.client.model.MutantSkeletonPartModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.entity.state.BodyPartRenderState;
import fuzs.mutantmonsters.world.entity.MutantSkeletonBodyPart;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class BodyPartRenderer extends EntityRenderer<MutantSkeletonBodyPart, BodyPartRenderState> {
    private final MutantSkeletonPartModel model;

    public BodyPartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MutantSkeletonPartModel(context.bakeLayer(ModModelLayers.MUTANT_SKELETON_PART),
                context.bakeLayer(ModModelLayers.MUTANT_SKELETON_PART_SPINE));
    }

    @Override
    public void extractRenderState(MutantSkeletonBodyPart entity, BodyPartRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.xRot = entity.getXRot(partialTick);
        reusedState.yRot = entity.getYRot(partialTick);
        reusedState.bodyPart = entity.getBodyPart();
    }

    @Override
    public BodyPartRenderState createRenderState() {
        return new BodyPartRenderState();
    }

    @Override
    public void submit(BodyPartRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.xRot));
        poseStack.scale(1.2F, -1.2F, -1.2F);
        ModelPart modelPart = this.model.getBodyPart(renderState.bodyPart);
        RenderType renderType = this.model.renderType(MutantSkeletonRenderer.TEXTURE_LOCATION);
        nodeCollector.submitModelPart(modelPart,
                poseStack,
                renderType,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                null);
        poseStack.popPose();
    }
}
