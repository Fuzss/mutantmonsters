package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.client.model.MutantSnowGolemModel;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantSnowGolemRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.block.Blocks;

public class MutantSnowGolemHeldBlockLayer extends RenderLayer<MutantSnowGolemRenderState, MutantSnowGolemModel> {

    public MutantSnowGolemHeldBlockLayer(RenderLayerParent<MutantSnowGolemRenderState, MutantSnowGolemModel> renderer, BlockRenderDispatcher blockRenderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, MutantSnowGolemRenderState renderState, float yRot, float xRot) {
        if (renderState.isThrowing && renderState.throwingTime < 7.0F) {
            poseStack.pushPose();
            boolean isLeftHanded = renderState.mainArm == HumanoidArm.LEFT;
            float scale = Math.min(0.8F, renderState.throwingTime / 7.0F);
            poseStack.translate(isLeftHanded ? -0.4 : 0.4, 0.0, 0.0);
            this.getParentModel().translateArm(poseStack, isLeftHanded);
            poseStack.translate(0.0, 0.9, 0.0);
            poseStack.scale(-scale, -scale, scale);
            poseStack.translate(-0.5, -0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            nodeCollector.submitBlock(poseStack,
                    Blocks.ICE.defaultBlockState(),
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    renderState.outlineColor);
            poseStack.popPose();
        }
    }
}
