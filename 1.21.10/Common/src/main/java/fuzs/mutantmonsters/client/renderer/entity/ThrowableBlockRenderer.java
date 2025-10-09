package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.client.renderer.entity.state.ThrowableBlockRenderState;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class ThrowableBlockRenderer extends EntityRenderer<ThrowableBlock, ThrowableBlockRenderState> {

    public ThrowableBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.6F;
    }

    @Override
    public void extractRenderState(ThrowableBlock entity, ThrowableBlockRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.blockState = entity.getBlockState();
        reusedState.isLarge = entity.isLarge();
        reusedState.yRot = entity.getYRot(partialTick);
    }

    @Override
    public ThrowableBlockRenderState createRenderState() {
        return new ThrowableBlockRenderState();
    }

    @Override
    public void submit(ThrowableBlockRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        poseStack.pushPose();
        poseStack.translate(0.0, 0.5, 0.0);
        if (renderState.isLarge) {
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot));
        } else {
            poseStack.scale(-0.75F, -0.75F, 0.75F);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.ageInTicks * 20.0F));
        poseStack.mulPose(Axis.ZN.rotationDegrees(renderState.ageInTicks * 12.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        nodeCollector.submitBlock(poseStack,
                renderState.blockState,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                renderState.outlineColor);
        poseStack.popPose();
    }
}
