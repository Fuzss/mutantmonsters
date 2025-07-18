package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class MutantEndermanHeldBlocksLayer extends RenderLayer<MutantEndermanRenderState, MutantEndermanModel> {
    private final BlockRenderDispatcher blockRenderer;

    public MutantEndermanHeldBlocksLayer(RenderLayerParent<MutantEndermanRenderState, MutantEndermanModel> renderer, BlockRenderDispatcher blockRenderer) {
        super(renderer);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        if (renderState.animation != MutantEnderman.CLONE_ANIMATION) {
            for (int i = 0; i < 4; ++i) {
                BlockState blockState = renderState.heldBlocks[i];
                if (blockState != null) {
                    poseStack.pushPose();
                    this.getParentModel().translateRotateArm(poseStack, i);
                    poseStack.translate(0.0, 1.2, 0.0);
                    float rotationAmount = renderState.ageInTicks + (i + 1) * 2.0F * Mth.PI;
                    poseStack.mulPose(Axis.XP.rotationDegrees(rotationAmount * 10.0F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(rotationAmount * 8.0F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(rotationAmount * 6.0F));
                    poseStack.scale(-0.75F, -0.75F, 0.75F);
                    poseStack.translate(-0.5, -0.5, 0.5);
                    poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                    this.blockRenderer.renderSingleBlock(blockState,
                            poseStack,
                            bufferSource,
                            packedLight,
                            OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                }
            }
        }
    }
}
