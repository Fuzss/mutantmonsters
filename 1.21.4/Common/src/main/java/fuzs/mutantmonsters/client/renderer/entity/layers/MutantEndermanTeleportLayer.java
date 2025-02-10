package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;

public class MutantEndermanTeleportLayer extends EnderEnergySwirlLayer<MutantEndermanRenderState, MutantEndermanModel> {
    private boolean isShrinking;

    public MutantEndermanTeleportLayer(RenderLayerParent<MutantEndermanRenderState, MutantEndermanModel> renderer) {
        super(renderer);
    }

    public void setShrinking(boolean shrinking) {
        this.isShrinking = shrinking;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        if (renderState.animation == MutantEnderman.TELEPORT_ANIMATION && renderState.animationTime < 10.0F) {
            super.render(poseStack, bufferSource, packedLight, renderState, yRot, xRot);
        }
    }

    @Override
    protected float getScale(MutantEndermanRenderState renderState) {
        if (this.isShrinking) {
            return 2.2F - renderState.animationTime / 10.0F;
        } else {
            return 1.2F + renderState.animationTime / 10.0F;
        }
    }

    @Override
    protected void scale(PoseStack poseStack, float scale) {
        poseStack.scale(scale, scale * 0.8F, scale);
    }

    @Override
    protected float getAlpha(MutantEndermanRenderState renderState) {
        if (!this.isShrinking && renderState.animationTime >= 8.0F) {
            return 1.0F - (renderState.animationTime - 8.0F) / 2.0F;
        } else if (this.isShrinking && renderState.animationTime < 2.0F) {
            return renderState.animationTime / 2.0F;
        } else {
            return 1.0F;
        }
    }
}
